import React, { useEffect, useState } from "react";
import { FaUserFriends } from "react-icons/fa";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import api from "../utils/api";
import LeaveForm from "../components/LeaveForm";
import LeaveTable from "../components/LeaveTable";

export default function EmployeeLeave() {
  const [leaves, setLeaves] = useState([]);
  const [summary, setSummary] = useState(null);
  const [holidays, setHolidays] = useState([]);
  const [showCalendar, setShowCalendar] = useState(false); // toggle calendar

  const fetchLeaves = async () => {
    const res = await api.get("/leaves/employee");
    setLeaves(res.data);

    if (res.data.length > 0) {
      const first = res.data[0];
      setSummary({
        yearlyCasualLeave: first.yearlyCasualLeave,
        pendingCasualLeave: first.pendingCasualLeave,
        yearlyPaidLeave: first.yearlyPaidLeave,
        pendingPaidLeave: first.pendingPaidLeave
      });
    }
  };

  const fetchHolidays = async () => {
    try {
      const res = await api.get("/leaves/getHolidays");
      setHolidays(res.data.map(h => h.holidayDate));
    } catch (err) {
      console.error("Failed to fetch holidays", err);
    }
  };

  // Mock holidays
  // useEffect(() => {
  //  fetchHolidays();
  // }, []);

  useEffect(() => {
    fetchLeaves();
    fetchHolidays();
  }, []);

  const events = [
    ...holidays.map(date => ({
      title: "Holiday",
      start: date,
      allDay: true,
      color: "red"
    })),
    ...leaves.map(l => ({
      title: "Your Leave",
      start: l.startDate,
      end: l.endDate,
      color: "blue"
    }))
  ];

  return (
    <div className="max-w-7xl mx-auto mt-8 space-y-8">

      {/* Header */}
      <div className="flex items-center space-x-3">
        <div className="p-3 bg-gradient-to-r from-blue-500 to-indigo-600 rounded-full shadow-lg text-white">
          <FaUserFriends className="w-6 h-6" />
        </div>
        <h2 className="text-3xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-600">
          Employee Leaves
        </h2>
      </div>

      {/* Summary Cards */}
      {summary && (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="bg-white shadow-lg p-4 rounded-xl border text-center">
            <h4 className="text-sm font-semibold text-gray-600">Yearly Casual Leaves</h4>
            <p className="text-xl font-bold text-blue-700">{summary.yearlyCasualLeave}</p>
          </div>
          <div className="bg-white shadow-lg p-4 rounded-xl border text-center">
            <h4 className="text-sm font-semibold text-gray-600">Pending Casual Leaves</h4>
            <p className="text-xl font-bold text-indigo-700">{summary.pendingCasualLeave}</p>
          </div>
          <div className="bg-white shadow-lg p-4 rounded-xl border text-center">
            <h4 className="text-sm font-semibold text-gray-600">Total Earned Leaves</h4>
            <p className="text-xl font-bold text-green-700">{summary.yearlyPaidLeave}</p>
          </div>
          <div className="bg-white shadow-lg p-4 rounded-xl border text-center">
            <h4 className="text-sm font-semibold text-gray-600">Pending Earned Leaves</h4>
            <p className="text-xl font-bold text-rose-700">{summary.pendingPaidLeave}</p>
          </div>
        </div>
      )}

      {/* Toggle Calendar Button */}
      {/* Toggle Calendar Button */}
      <div className="mt-4 flex justify-start">
        <button
          onClick={() => setShowCalendar(!showCalendar)}
          className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg shadow-md font-semibold transition-all duration-200"
        >
          {showCalendar ? "Hide Calendar" : "Show Calendar"}
        </button>
      </div>

      {/* FullCalendar (conditionally rendered) */}
      {showCalendar && (
        <div className="bg-gradient-to-r from-indigo-100 via-purple-100 to-pink-100 rounded-3xl shadow-xl p-3 w-full max-w-sm border border-gray-200 mt-2">
          <h3 className="text-base md:text-lg font-bold mb-3 text-indigo-800">
            Leave Calendar
          </h3>

          <FullCalendar
            plugins={[dayGridPlugin, interactionPlugin]}
            initialView="dayGridMonth"
            events={events}
            headerToolbar={{
              left: "prev,next today",
              center: "title",
              right: ""
            }}
            aspectRatio={0.9}
            dayMaxEventRows={true}
            height={220}
            contentHeight={200}
            eventColor="blue"
            eventDisplay="block"
            eventClassNames={(arg) => [
              "text-xs font-semibold rounded-lg px-1 py-0.5 shadow-sm transition-all duration-200",
              arg.event.backgroundColor === "red" && "bg-red-400 text-red-900 hover:bg-red-500",
              arg.event.backgroundColor === "blue" && "bg-blue-400 text-blue-900 hover:bg-blue-500",
            ]}
            dayCellClassNames={(arg) => [
              "hover:bg-indigo-50 transition-colors duration-150 rounded-lg"
            ]}
          />

          <p className="mt-2 text-xs text-gray-600">
            <span className="text-red-500 font-semibold">Red:</span> Holidays | <span className="text-blue-500 font-semibold">Blue:</span> Your Leaves
          </p>
        </div>
      )}



      {/* Leave Form & Table */}
      <LeaveForm onSuccess={fetchLeaves} />
      <LeaveTable leaves={leaves} />
    </div>
  );
}
