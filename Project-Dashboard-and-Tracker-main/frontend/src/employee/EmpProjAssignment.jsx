import React, { useEffect, useState, useRef } from "react";
import api from "../utils/api";
import { Loader2 } from "lucide-react";

export default function EmpProjAssignment() {
  const [assignments, setAssignments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [updatingId, setUpdatingId] = useState(null);

  // 💤 refs for sleep detection
  const assignmentsRef = useRef([]);
  const sleepLockRef = useRef(false);
  const sleepPausedIdsRef = useRef(new Set());

  const formatTime = (seconds) => {
    if (!seconds || seconds <= 0) return "00:00:00";
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${String(hrs).padStart(2, "0")}:${String(mins).padStart(
      2,
      "0"
    )}:${String(secs).padStart(2, "0")}`;
  };

  const STATUS_OPTIONS = [
    { label: "Not Started", value: "NOT_STARTED" },
    { label: "In Progress", value: "IN_PROGRESS" },
    { label: "On Hold", value: "ON_HOLD" },
    { label: "Completed", value: "COMPLETED" },
  ];

  const statusColors = {
    NOT_STARTED: "bg-gray-300 text-gray-800",
    IN_PROGRESS: "bg-blue-300 text-blue-800",
    ON_HOLD: "bg-orange-300 text-orange-800",
    COMPLETED: "bg-green-300 text-green-800",
  };

  useEffect(() => {
    fetchAssignments();
  }, []);

  const fetchAssignments = async () => {
    try {
      setLoading(true);
      const res = await api.get("/assignment/getAllAssignments", {
        withCredentials: true,
      });
      if (res.data.success) setAssignments(res.data.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // 💤 keep assignments synced
  useEffect(() => {
    assignmentsRef.current = assignments;
  }, [assignments]);

  // 💤 AUTO-PAUSE ON PC SLEEP
  useEffect(() => {
    let last = Date.now();
    let mounted = true; // safety flag

    const pauseTimers = async () => {
      const inProgress = assignmentsRef.current.filter(
        (a) => a.status === "IN_PROGRESS" && a.timerRunning
      );

      for (const a of inProgress) {
        sleepPausedIdsRef.current.add(a.id);
        try {
          await api.patch(`/assignment/pauseTimer/${a.id}`, {}, { withCredentials: true });
        } catch (e) {
          console.error("Pause failed", a.id, e);
        }
      }
    };

    const resumeTimers = async () => {
      for (const id of sleepPausedIdsRef.current) {
        try {
          await api.patch(`/assignment/startTimer/${id}`, {}, { withCredentials: true });
        } catch (e) {
          console.error("Resume failed", id, e);
        }
      }

      sleepPausedIdsRef.current.clear();
      sleepLockRef.current = false;
    };

    const interval = setInterval(async () => {
      if (!mounted) return;

      const now = Date.now();
      const diff = now - last;

      // 💤 PC SLEEP detected
      if (diff > 20000 && !sleepLockRef.current) {
        console.log("💤 PC SLEEP detected");
        sleepLockRef.current = true;
        await pauseTimers();
      }

      last = now;
    }, 5000);

    const onVisibilityChange = async () => {
      if (!mounted) return;

      // 🔒 PC LOCK / TAB HIDDEN
      if (document.hidden && !sleepLockRef.current) {
        console.log("🔒 PC LOCK detected → pausing timers");
        sleepLockRef.current = true;
        await pauseTimers();
      }

      // 🔓 PC UNLOCK / WAKE → AUTO RESUME
      if (!document.hidden && sleepPausedIdsRef.current.size > 0) {
        console.log("🔓 PC UNLOCK → auto resume");
        await resumeTimers();
      }
    };

    document.addEventListener("visibilitychange", onVisibilityChange);

    return () => {
      mounted = false;
      clearInterval(interval);
      document.removeEventListener("visibilitychange", onVisibilityChange);
    };
  }, []);

  const updateStatus = async (id, newStatus) => {
    try {
      setUpdatingId(id);
      const res = await api.patch(
        `/assignment/updateStatus/${id}/${newStatus}`,
        { withCredentials: true }
      );

      if (newStatus === "IN_PROGRESS") {
        await api.patch(`/assignment/startTimer/${id}`, {}, { withCredentials: true });
      }
      if (newStatus === "ON_HOLD") {
        await api.patch(`/assignment/pauseTimer/${id}`, {}, { withCredentials: true });
      }
      if (newStatus === "COMPLETED") {
        await api.patch(`/assignment/stopTimer/${id}`, {}, { withCredentials: true });
      }

      if (res.data.success) fetchAssignments();
    } catch (err) {
      console.error(err);
      alert("Status update failed");
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div className="p-6">
      <h1 className="text-4xl font-extrabold mb-6 flex items-center gap-4 text-indigo-600">
        <span className="text-2xl">📌</span> Project Tasks
      </h1>

      <div className="bg-white shadow-lg rounded-xl p-4">
        <h2 className="text-xl font-semibold mb-4">Assigned Work</h2>

        {loading ? (
          <div className="flex justify-center p-4">
            <Loader2 className="h-8 w-8 animate-spin text-indigo-600" />
          </div>
        ) : (
          <table className="w-full bg-white rounded shadow">
            <thead className="bg-gray-100">
              <tr>
                <th className="p-2">Project</th>
                <th className="p-2">Milestone</th>
                <th className="p-2">Discipline</th>
                <th className="p-2">Start Date</th>
                <th className="p-2">Due Date</th>
                <th className="p-2">Time Spent</th>
                <th className="p-2">Status</th>
                <th className="p-2">Approve/Reject</th>
                <th className="p-2">Comments</th>
              </tr>
            </thead>

            <tbody>
              {assignments.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center p-4 text-gray-500">
                    No assignments found.
                  </td>
                </tr>
              ) : (
                assignments.map((a) => (
                  <tr key={a.id} className="border-b hover:bg-gray-50">
                    <td className="p-2 text-center">{a.projectName}</td>
                    <td className="p-2 text-center">
                      {a.milestoneName}
                    </td>
                    <td className="p-2 text-center">
                      {a.disciplineName}
                    </td>
                    <td className="p-2 text-center">{a.startDate}</td>
                    <td className="p-2 text-center">{a.dueDate}</td>
                    <td className="p-2 text-center">
                      {formatTime(a.totalWorkedSeconds)}
                    </td>
                    <td className="p-2 text-center">
                      <span
                        className={`px-3 py-1 rounded-full text-xs font-semibold ${statusColors[a.status]}`}
                      >
                        {a.status.replace("_", " ")}
                      </span>

                      <select
                        className="border p-2 rounded bg-gray-50 mt-2"
                        value={a.status}
                        disabled={updatingId === a.id || a.finalized}
                        onChange={(e) => updateStatus(a.id, e.target.value)}
                      >
                        {STATUS_OPTIONS.map((s) => (
                          <option key={s.value} value={s.value}>
                            {s.label}
                          </option>
                        ))}
                      </select>

                      {updatingId === a.id && (
                        <div className="text-indigo-600 text-sm flex items-center justify-center gap-1 mt-1">
                          <Loader2 className="animate-spin w-4 h-4" />
                          Updating...
                        </div>
                      )}
                    </td>
                    <td className="p-2 text-center"><span className={`px-2 py-1 rounded-lg text-xs font-semibold 
      ${a.taskStatus.startsWith("APPROVED") ? "bg-green-100 text-green-800" : "bg-red-100 text-red-800"}`}>
                      {a.taskStatus.replaceAll("_", " ")}
                    </span></td>
                    <td className="p-2 text-center">{a.headComment}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}
