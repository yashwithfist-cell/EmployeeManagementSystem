import React, { useState } from "react";
import api from "../utils/api";
import { FaCalendarAlt } from "react-icons/fa";

export default function EmpAttendanceLog() {
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [attendance, setAttendance] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!fromDate || !toDate) {
      alert("Please select both From Date and To Date");
      return;
    }

    setLoading(true);
    try {
      const res = await api.get("/attendancelog/searchByEmployee", {
        params: { fromDate, toDate },
      });
      setAttendance(res.data);
    } catch (error) {
      console.error(error);
      alert("Error fetching attendance data");
    }
    setLoading(false);
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case "PRESENT":
        return "bg-green-100 text-green-800";
      case "ABSENT":
        return "bg-red-100 text-red-800";
      case "HALF_PRESENT":
        return "bg-yellow-100 text-yellow-800";
      default:
        return "bg-gray-100 text-gray-800";
    }
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      {/* Card */}
      <div className="bg-white rounded-2xl shadow-lg p-6 space-y-6">
        <h2 className="flex items-center gap-3 text-2xl font-extrabold text-white bg-gradient-to-r from-indigo-600 to-purple-600 
  rounded-xl px-4 py-3 shadow-md border-b-2 border-white">
  <FaCalendarAlt className="text-xl" />
  Attendance Search
</h2>

        {/* Search Filters */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-4">
          <input
            type="date"
            value={fromDate}
            onChange={(e) => setFromDate(e.target.value)}
            className="flex-1 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-400 outline-none"
            placeholder="From Date"
          />
          <input
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
            className="flex-1 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-indigo-400 outline-none"
            placeholder="To Date"
          />
          <button
            onClick={handleSearch}
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-2 rounded-lg shadow-md font-semibold transition-all duration-200"
          >
            Search
          </button>
        </div>

        {/* Loading */}
        {loading && (
          <p className="text-center text-gray-500">Searching...</p>
        )}

        {/* Attendance Table */}
        {attendance.length > 0 ? (
          <div className="overflow-x-auto rounded-lg shadow-inner">
            <table className="w-full text-center min-w-[800px] border-collapse">
              <thead>
                <tr className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white">
                  <th className="p-3 rounded-tl-lg">Emp ID</th>
                  <th className="p-3">Emp Name</th>
                  <th className="p-3">Date</th>
                  <th className="p-3">Total Hours</th>
                  <th className="p-3">Worked Hours</th>
                  <th className="p-3">Status</th>
                  <th className="p-3">In Time</th>
                  <th className="p-3 rounded-tr-lg">Out Time</th>
                </tr>
              </thead>
              <tbody>
                {attendance.map((a, i) => (
                  <tr
                    key={i}
                    className={`${
                      i % 2 === 0 ? "bg-gray-50" : "bg-white"
                    } hover:bg-indigo-50 transition-colors duration-200`}
                  >
                    <td className="p-2 font-medium">{a.empId}</td>
                    <td className="p-2 font-medium">{a.username}</td>
                    <td className="p-2">{a.date}</td>
                    <td className="p-2">{a.totalHours}</td>
                    <td className="p-2">{a.totalHoursWorked}</td>
                    <td className="p-2">
                      <span
                        className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusBadge(
                          a.status
                        )}`}
                      >
                        {a.status.replace("_", " ")}
                      </span>
                    </td>
                    <td className="p-2">{a.inTime ? a.inTime.replace("T", " ") : "-"}</td>
                    <td className="p-2">{a.outTime ? a.outTime.replace("T", " ") : "-"}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          !loading && (
            <p className="text-center text-gray-500 mt-4">
              No results to display
            </p>
          )
        )}
      </div>
    </div>
  );
}
