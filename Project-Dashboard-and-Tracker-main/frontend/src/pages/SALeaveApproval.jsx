import React, { useEffect, useState } from "react";
import api from "../utils/api";
import LeaveTable from "../components/LeaveTable";
import { useAuth } from "../context/AuthContext";
import { FaClipboardCheck } from "react-icons/fa";

export default function SALeaveApproval() {
  const [leaves, setLeaves] = useState([]);
  const { activeUser } = useAuth();
  const role = activeUser?.role || "";

  const endpointMap = {
    PROJECT_MANAGER: "/leaves/getAllByManager",
    TEAM_LEAD: "/leaves/getAllByTeamLead",
    HUMAN_RESOURCE: "/leaves"
  };

  const key = Object.keys(endpointMap).find(k => role.includes(k));

  useEffect(() => {
    const fetchLeaves = async () => {
      if (!role) return;

      try {
        const res = await api.get(endpointMap[key]);
        setLeaves(res.data);
      } catch (err) {
        console.error("Error fetching leaves:", err);
      }
    };

    fetchLeaves();
  }, [role]);

  const handleAction = async (id, status) => {
    try {
      await api.put(`/leaves/${id}/status`, null, { params: { status } });
      const res = await api.get(endpointMap[key]);
      setLeaves(res.data);
    } catch (err) {
      console.error("Error updating leave status:", err);
    }
  };

  if (!role)
    return (
      <div className="w-full flex justify-center mt-10">
        <p className="animate-pulse text-lg text-gray-500 font-medium">
          Loading dashboard...
        </p>
      </div>
    );

  return (
    <div className="max-w-6xl mx-auto mt-10 space-y-6 animate-fadeIn">
      {/* Header with Icon */}
      <div className="flex items-center space-x-4">
        <div className="p-3 bg-gradient-to-r from-blue-600 to-indigo-700 rounded-full shadow-lg text-white">
          <FaClipboardCheck className="w-7 h-7" />
        </div>
        <div>
          <h2 className="text-3xl font-extrabold tracking-tight text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-indigo-700">
            Leave Approval Dashboard
          </h2>
          <p className="text-gray-600 font-medium">
            Review and take action on employee leave requests
          </p>
        </div>
      </div>

      {/* Role Display Card */}
      <div className="bg-gradient-to-r from-indigo-50 to-purple-50 border p-4 rounded-xl shadow-sm">
        <p className="text-gray-700 font-medium">
          Signed in as:
          <span className="ml-2 px-2 py-1 rounded-lg text-sm font-semibold text-white bg-gradient-to-r from-indigo-600 to-purple-600">
            {role==="HUMAN_RESOURCE" ? "HR" : role}
          </span>
        </p>
      </div>

      {/* Leave Table Card */}
      <div className="bg-white rounded-2xl shadow-xl border p-6 hover:shadow-2xl transition-all duration-300">
        <LeaveTable leaves={leaves} onAction={handleAction} />

        {leaves.length === 0 && (
          <p className="text-center text-gray-600 font-medium mt-4">
            No leave requests pending for approval 🎉
          </p>
        )}
      </div>
    </div>
  );
}
