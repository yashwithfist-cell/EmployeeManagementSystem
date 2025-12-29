import React, { useState } from "react";
import api from "../utils/api";

export default function SA_SystemLogAdmin() {
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [username, setUsername] = useState("");
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);

  const formatTime = (ms) => {
    const sec = Math.floor(ms / 1000);
    const h = Math.floor(sec / 3600);
    const m = Math.floor((sec % 3600) / 60);
    const s = sec % 60;
    return `${h.toString().padStart(2, "0")}h:${m.toString().padStart(2, "0")}m:${s.toString().padStart(2, "0")}s`;
  };

  const formatDateTime12hr = (dateTime) => {
    if (!dateTime) return "-";
    const date = new Date(dateTime);
    return date.toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: true
    });
  };

  const handleSearch = async () => {
    if (!fromDate || !toDate) {
      alert("Please select date range.");
      return;
    }
    setLoading(true);
    try {
      const res = await api.get(`/systemattendance/all`, {
        params: { fromDate, toDate, username },
      });
      setData(res.data);
    } catch (e) {
      console.error(e);
      alert("Error fetching logs");
    }
    setLoading(false);
  };

  return (
    <div className="p-6 bg-white shadow-xl rounded-lg max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold mb-5 text-center">System Attendance Log</h1>

      {/* Filters */}
      <div className="flex flex-wrap gap-4 justify-center mb-5">
        <div>
          <label className="text-sm">From Date</label>
          <input type="date" className="border p-2 rounded w-full"
            value={fromDate} onChange={(e) => setFromDate(e.target.value)} />
        </div>
        <div>
          <label className="text-sm">To Date</label>
          <input type="date" className="border p-2 rounded w-full"
            value={toDate} onChange={(e) => setToDate(e.target.value)} />
        </div>
        <div>
          <label className="text-sm">Employee Username (optional)</label>
          <input type="text" placeholder="Search by username" className="border p-2 rounded w-full"
            value={username} onChange={(e) => setUsername(e.target.value)} />
        </div>
        <button onClick={handleSearch}
          className="bg-green-600 text-white px-5 py-2 rounded hover:bg-green-700 mt-auto">
          Search
        </button>
      </div>

      {/* Table */}
      {loading ? (
        <p className="text-center text-lg font-semibold">Loading...</p>
      ) : (
        <div className="overflow-x-auto">
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
                <th className="p-2">Total Time</th>
              </tr>
            </thead>
            <tbody>
              {data.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center py-4 text-gray-500">
                    No data found
                  </td>
                </tr>
              ) : (
                data.map((log, index) => (
                  <tr key={index} className="hover:bg-gray-100 text-center border-b">
                    <td className="p-2">{log.date}</td>
                    <td className="p-2 font-semibold">{log.username}</td>
                    <td className="p-2">{formatDateTime12hr(log.startTime)}</td>
                    <td className="p-2">{formatDateTime12hr(log.endTime)}</td>
                    <td className="p-2 text-green-600 font-bold">{formatTime(log.totalWorkMs)}</td>
                    <td className="p-2 text-yellow-600 font-bold">{formatTime(log.totalBreakMs)}</td>
                    <td className="p-2 text-blue-600 font-bold">{formatTime(log.totalMeetingMs)}</td>
                    <td className="p-2 text-blue-600 font-bold">{formatTime(log.totalEventMs)}</td>
                    <td className="p-2 text-blue-600 font-bold">
                      {formatTime(log.totalWorkMs - log.totalBreakMs)}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
