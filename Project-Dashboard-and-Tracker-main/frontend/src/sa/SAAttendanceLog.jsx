import React, { useState } from "react";
import axios from "axios";
import api from "../utils/api";

export default function SAAttendanceLog() {
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
            const res = await api.get("/attendancelog/search", {
                params: { fromDate, toDate },
            });
            setAttendance(res.data);
        } catch (error) {
            console.error(error);
            alert("Error fetching attendance data");
        }
        setLoading(false);
    };



    return (
        <div className="p-6 bg-white rounded-xl shadow-lg">
            <h2 className="text-xl font-bold mb-4 text-gray-800">
                Attendance Search
            </h2>

            {/* Search Filters */}
            <div className="flex space-x-4 mb-6">
                <div className="flex-1">
                    <input
                        type="date"
                        value={fromDate}
                        onChange={(e) => setFromDate(e.target.value)}
                        className="w-full px-3 py-2 border rounded-md"
                        placeholder="From Date"
                    />
                </div>

                <div className="flex-1">
                    <input
                        type="date"
                        value={toDate}
                        onChange={(e) => setToDate(e.target.value)}
                        className="w-full px-3 py-2 border rounded-md"
                        placeholder="To Date"
                    />
                </div>

                <button
                    onClick={handleSearch}
                    className="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-md shadow-md font-semibold"
                >
                    Search
                </button>
            </div>

            {/* Loading State */}
            {loading && <p className="text-center text-gray-600">Searching...</p>}

            {/* Attendance Table */}
            {attendance.length > 0 ? (
                <div className="overflow-x-auto">
                    <table className="w-full border rounded-lg">
                        <thead>
                            <tr className="bg-blue-600 text-white">
                                <th className="p-2">Emp ID</th>
                                <th className="p-2">Emp Name</th>
                                <th className="p-2">Date</th>
                                <th className="p-2">Total Hours</th>
                                <th className="p-2">Total Worked</th>
                                <th className="p-2">Status</th>
                                <th className="p-2">In Time</th>
                                <th className="p-2">Out Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            {attendance.map((a, i) => {
                                let rowColor = "";
                                if (a.status === "PRESENT") rowColor = "bg-green-100"; // green for present
                                else if (a.status === "ABSENT") rowColor = "bg-red-100"; // red for absent
                                else if (a.status === "HALF_PRESENT") rowColor = "bg-yellow-100"; // yellow for half present
                                else rowColor = i % 2 === 0 ? "bg-gray-50" : "bg-white"; // default row colors

                                return (
                                    <tr key={i} className={`${rowColor} text-center`}>
                                        <td className="p-2 font-semibold">{a.empId}</td>
                                        <td className="p-2 font-semibold">{a.username}</td>
                                        <td className="p-2">{a.date}</td>
                                        <td className="p-2">{a.totalHours}</td>
                                        <td className="p-2">{a.totalHoursWorked}</td>
                                        <td className="p-2">{a.status}</td>
                                        <td className="p-2">{a.inTime ? a.inTime.replace("T", " ") : "-"}</td>
                                        <td className="p-2">{a.outTime ? a.outTime.replace("T", " ") : "-"}</td>
                                    </tr>
                                );
                            })}
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
    );
}
