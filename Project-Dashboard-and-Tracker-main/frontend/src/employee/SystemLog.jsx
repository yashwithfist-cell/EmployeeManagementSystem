import React, { useState, useEffect, useRef } from "react";
import api from "../utils/api";
import { useAuth } from "../context/AuthContext.js";

export default function SystemLog() {
  const { activeUser } = useAuth();
  const { username } = activeUser || {};

  const [mode, setMode] = useState(null); // "start" or "stop"
  const [waitingApproval, setWaitingApproval] = useState(false);
  const [waitingStopApproval, setWaitingStopApproval] = useState(false);
  const [stopNotifId, setStopNotifId] = useState(null);

  const [isTraining, setIsTraining] = useState(false);
  const [trainingElapsed, setTrainingElapsed] = useState(0);
  const [trainingDesc, setTrainingDesc] = useState("");
  const [showTrainingPopup, setShowTrainingPopup] = useState(false);
  const startTrainingRef = useRef(0);
  const endTrainingRef = useRef(0);

  // Work & Break
  const [isWorking, setIsWorking] = useState(false);
  const [isOnBreak, setIsOnBreak] = useState(false);
  const [workElapsed, setWorkElapsed] = useState(0);
  const [breakElapsed, setBreakElapsed] = useState(0);
  const [notifId, setNotifId] = useState(0);

  // Meeting & Event
  const [isMeeting, setIsMeeting] = useState(false);
  const [meetingElapsed, setMeetingElapsed] = useState(0);

  const [isEvent, setIsEvent] = useState(false);
  const [eventElapsed, setEventElapsed] = useState(0);

  // Logs & loading
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  // Timer ref for system sleep handling
  const lastTick = useRef(Date.now());

  const [currentPage, setCurrentPage] = useState(1);
  const recordsPerPage = 10; // showing 10 rows per page

  const indexOfLastRecord = currentPage * recordsPerPage;
  const indexOfFirstRecord = indexOfLastRecord - recordsPerPage;
  const currentRecords = data.slice(indexOfFirstRecord, indexOfLastRecord);

  const totalPages = Math.ceil(data.length / recordsPerPage);

  const handlePrev = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };

  const handleNext = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };



  // Format milliseconds to HH:mm:ss
  const formatTime = (ms) => {
    const sec = Math.floor(ms / 1000);
    const h = Math.floor(sec / 3600);
    const m = Math.floor((sec % 3600) / 60);
    const s = sec % 60;
    return `${h.toString().padStart(2, "0")}h:${m.toString().padStart(2, "0")}m:${s.toString().padStart(2, "0")}s`;
  };

  const formatDateTime12hr = (dt) => {
    if (!dt) return "--";
    const date = new Date(dt);
    return date.toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: true,
    });
  };

  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await api.get("/systemattendance/getEmpSysLog", { withCredentials: true });
      setData(res.data);
      setCurrentPage(1);
    } catch (e) {
      console.error(e);
      alert("Error fetching logs");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  // Main interval (1s) to handle elapsed timers, even during system sleep
  useEffect(() => {
    const interval = setInterval(() => {
      const now = Date.now();
      const diff = now - lastTick.current;

      // Work / Break logic
      if (isWorking) {
        if (isOnBreak) {
          setBreakElapsed((prev) => prev + diff);
        } else {
          setWorkElapsed((prev) => prev + diff);
        }

        // Meeting / Event always add to their timers
        if (isMeeting) setMeetingElapsed((prev) => prev + diff);
        if (isEvent) setEventElapsed((prev) => prev + diff);
        if (isTraining) setTrainingElapsed((prev) => prev + diff);
      }

      lastTick.current = now;
    }, 1000);

    return () => clearInterval(interval);
  }, [isWorking, isOnBreak, isMeeting, isEvent, isTraining]);

  // Visibility change to auto-start break if needed
  useEffect(() => {
    const handleVisibilityChange = async () => {
      if (document.hidden) {
        // Only pause work if not in meeting/event
        if (isWorking && !isOnBreak && !isMeeting && !isEvent) {
          await handleBreakStart(); // auto-break or pause work
        }
      } else {
        // Resume break only if previously paused
        if (isOnBreak) await handleBreakStop();
      }
    };
    document.addEventListener("visibilitychange", handleVisibilityChange);
    return () => document.removeEventListener("visibilitychange", handleVisibilityChange);
  }, [isWorking, isOnBreak, isMeeting, isEvent, isTraining]);


  // Handlers
  const handleStartWork = async () => {
    setIsWorking(true);
    lastTick.current = Date.now();
    await api.post("/systemattendance/start", { username });
  };

  const handleBreakStart = async () => {
    setIsOnBreak(true);
    lastTick.current = Date.now();
    await api.post("/systemattendance/break/start", { username });
  };

  const handleBreakStop = async () => {
    setIsOnBreak(false);
    lastTick.current = Date.now();
    await api.post("/systemattendance/break/stop", { username });
  };

  const handleStopWork = async () => {
    setIsWorking(false);
    setIsOnBreak(false);
    await api.post("/systemattendance/stop", {
      username,
      totalWorkMs: workElapsed,
      totalBreakMs: breakElapsed,
      totalMeetingMs: meetingElapsed,
      totalEventMs: eventElapsed,
      totalTrainingMs: trainingElapsed,
    });
    setWorkElapsed(0);
    setBreakElapsed(0);
    setMeetingElapsed(0);
    setEventElapsed(0);
    setTrainingElapsed(0);
    setIsMeeting(false);
    setIsEvent(false);
    setIsTraining(false);
    fetchData();
  };

  // Meeting handlers
  const handleMeetingStart = () => {
    setIsMeeting(true);
    lastTick.current = Date.now();
  };
  const handleMeetingStop = () => {
    setIsMeeting(false);
    lastTick.current = Date.now();
  };

  // Event handlers
  const handleEventStart = () => {
    setIsEvent(true);
    lastTick.current = Date.now();
  };
  const handleEventStop = () => {
    setIsEvent(false);
    lastTick.current = Date.now();
  };

  // if (isTraining) setTrainingElapsed((prev) => prev + diff);

  const handleTrainingStart = () => {
    setMode("start");
    setShowTrainingPopup(true);
  };

  const handleTrainingStop = async () => {
    setMode("stop");
    setShowTrainingPopup(true);
    setWaitingStopApproval(false);  // reset stop approval
    setTrainingDesc("");
  };

  const sendTrainingRequest = async () => {
    try {
      const res = await api.put(`/notifications/logStatus/${trainingDesc}`);
      setNotifId(res.data);     // store only ID, important!
      setWaitingApproval(true);
    } catch (e) {
      console.error(e);
      alert("Error sending request");
    }
  };

  const checkStartTraining = async () => {
    if (!notifId) return;

    const res = await api.get(`/notifications/getTrainingStatus/${notifId}`);
    if (!res.data) return alert("Not approved yet");

    const now = Date.now();
    startTrainingRef.current = now;
    lastTick.current = now;

    setIsTraining(true);
    setShowTrainingPopup(false);
    setWaitingApproval(false);
  };

  const sendStopTrainingRequest = async () => {
    try {
      const res = await api.put(`/notifications/logStatus/${trainingDesc}`);
      setStopNotifId(res.data);
      setWaitingStopApproval(true);
    } catch (e) {
      console.error(e);
      alert("Error sending stop request");
    }
  };

  const checkStopTrainingApproval = async () => {
    try {
      const res = await api.get(`/notifications/getTrainingStatus/${stopNotifId}`);
      if (!res.data) return alert("Stop Training not approved yet");

      const now = Date.now();
      endTrainingRef.current = now;

      await api.post("/systemattendance/training", null, {
        params: {
          startTrainingTime: startTrainingRef.current,
          endTrainingTime: now,
          trainingDesc
        }
      });

      // reset states
      setIsTraining(false);
      setShowTrainingPopup(false);
      setWaitingStopApproval(false);

    } catch (e) {
      console.error(e);
      alert("Error checking stop approval");
    }
  };




  return (
    <>
      <div className="p-6 bg-gray-100 rounded-xl max-w-6xl mx-auto text-center shadow-lg">
        <h1 className="text-2xl font-bold mb-4">Work Tracker</h1>

        {/* Work/Break/Meeting/Event timers */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <div>
            <p className="font-semibold">Work</p>
            <p>{formatTime(workElapsed)}</p>
          </div>
          <div>
            <p className="font-semibold">Break</p>
            <p>{formatTime(breakElapsed)}</p>
          </div>
          <div>
            <p className="font-semibold">Meeting</p>
            <p>{formatTime(meetingElapsed)}</p>
          </div>
          <div>
            <p className="font-semibold">Event</p>
            <p>{formatTime(eventElapsed)}</p>
          </div>
          <div>
            <p className="font-semibold">Training</p>
            <p>{formatTime(trainingElapsed)}</p>
          </div>

        </div>

        {/* Buttons */}
        <div className="flex flex-wrap justify-center gap-3 mt-4">
          {!isWorking && (
            <button onClick={handleStartWork} className="bg-green-600 text-white px-4 py-2 rounded">
              Login
            </button>
          )}
          {isWorking && !isOnBreak && (
            <button onClick={handleBreakStart} className="bg-yellow-500 text-white px-4 py-2 rounded">
              Start Break
            </button>
          )}
          {isOnBreak && (
            <button onClick={handleBreakStop} className="bg-blue-500 text-white px-4 py-2 rounded">
              Stop Break
            </button>
          )}
          {isWorking && (
            <button onClick={handleStopWork} className="bg-red-600 text-white px-4 py-2 rounded">
              Logout
            </button>
          )}
          {/* Meeting Buttons */}
          {isWorking && !isMeeting && (
            <button onClick={handleMeetingStart} className="bg-purple-600 text-white px-4 py-2 rounded">
              Start Meeting
            </button>
          )}
          {isMeeting && (
            <button onClick={handleMeetingStop} className="bg-purple-400 text-white px-4 py-2 rounded">
              Stop Meeting
            </button>
          )}
          {/* Event Buttons */}
          {isWorking && !isEvent && (
            <button onClick={handleEventStart} className="bg-indigo-600 text-white px-4 py-2 rounded">
              Start Event
            </button>
          )}
          {isEvent && (
            <button onClick={handleEventStop} className="bg-indigo-400 text-white px-4 py-2 rounded">
              Stop Event
            </button>
          )}
          {/* Training Buttons */}
          {isWorking && !isTraining && (
            <button
              onClick={handleTrainingStart}
              className="bg-orange-600 text-white px-4 py-2 rounded"
            >
              Start Training
            </button>
          )}
          {isTraining && (
            <button
              onClick={handleTrainingStop}
              className="bg-orange-400 text-white px-4 py-2 rounded"
            >
              Stop Training
            </button>
          )}

        </div>

        {/* Table */}
        <div className="overflow-x-auto mt-6">
          {loading ? (
            <p className="text-center font-semibold py-4">Loading...</p>
          ) : (
            <table className="w-full text-sm border-collapse">
              <thead>
                <tr className="bg-gray-800 text-white">
                  <th className="p-2">Date</th>
                  <th className="p-2">Username</th>
                  <th className="p-2">Start Time</th>
                  <th className="p-2">Stop Time</th>
                  <th className="p-2">Work Time</th>
                  <th className="p-2">Break Time</th>
                  <th className="p-2">Meeting Time</th>
                  <th className="p-2">Event Time</th>
                  <th className="p-2">Training Time</th>
                  <th className="p-2">Total Time</th>
                </tr>
              </thead>
              <tbody>
                {data.length === 0 ? (
                  <tr>
                    <td colSpan="9" className="text-center py-4 text-gray-500">
                      No data found
                    </td>
                  </tr>
                ) : (
                  currentRecords.map((log, index) => (
                    <tr key={log.id || index} className="hover:bg-gray-100 text-center border-b">
                      <td className="p-2">{log.date}</td>
                      <td className="p-2 font-semibold">{log.username}</td>
                      <td className="p-2">{formatDateTime12hr(log.startTime)}</td>
                      <td className="p-2">{formatDateTime12hr(log.endTime)}</td>
                      <td className="p-2 text-green-600 font-bold">{formatTime(log.totalWorkMs)}</td>
                      <td className="p-2 text-yellow-600 font-bold">{formatTime(log.totalBreakMs)}</td>
                      <td className="p-2 text-purple-600 font-bold">{formatTime(log.totalMeetingMs)}</td>
                      <td className="p-2 text-indigo-600 font-bold">{formatTime(log.totalEventMs)}</td>
                      <td className="p-2 text-orange-600 font-bold">{formatTime(log.totalTrainingMs)}</td>
                      <td className="p-2 text-blue-600 font-bold">
                        {formatTime(
                          log.totalWorkMs - log.totalBreakMs + (log.totalMeetingMs || 0) + (log.totalEventMs || 0)
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          )}
          {totalPages > 1 && !loading && data.length > 0 && (
            <div className="flex justify-center items-center mt-4 gap-4">
              <button
                onClick={handlePrev}
                disabled={currentPage === 1 || loading}
                className="px-3 py-1 bg-gray-300 rounded disabled:opacity-50"
              >
                Prev
              </button>

              <span className="font-semibold">
                Page {currentPage} of {totalPages}
              </span>

              <button
                onClick={handleNext}
                disabled={currentPage === totalPages || loading}
                className="px-3 py-1 bg-gray-300 rounded disabled:opacity-50"
              >
                Next
              </button>
            </div>
          )}
        </div>
      </div>
      {showTrainingPopup && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded-lg w-80 text-center shadow-xl">
            <h2 className="font-bold text-lg mb-2">Training Description</h2>
            <textarea
              className="w-full border rounded p-2"
              rows="3"
              placeholder="Enter training description..."
              value={trainingDesc}
              onChange={(e) => setTrainingDesc(e.target.value)}
            />
            <div className="flex justify-end gap-3 mt-4">
              <button
                className="px-3 py-1 bg-gray-400 text-white rounded"
                onClick={() => setShowTrainingPopup(false)}
              >
                Cancel
              </button>

              {mode === "start" && !waitingApproval && (
                <button className="px-3 py-1 bg-orange-600 text-white rounded"
                  onClick={sendTrainingRequest}>
                  Request
                </button>
              )}

              {mode === "start" && waitingApproval && (
                <button className="px-3 py-1 bg-orange-600 text-white rounded"
                  onClick={checkStartTraining}>
                  Start
                </button>
              )}

              {mode === "stop" && !waitingStopApproval && (
                <button className="px-3 py-1 bg-orange-600 text-white rounded"
                  onClick={sendStopTrainingRequest}>
                  Request Stop
                </button>
              )}

              {mode === "stop" && waitingStopApproval && (
                <button className="px-3 py-1 bg-orange-600 text-white rounded"
                  onClick={checkStopTrainingApproval}>
                  Stop
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}
