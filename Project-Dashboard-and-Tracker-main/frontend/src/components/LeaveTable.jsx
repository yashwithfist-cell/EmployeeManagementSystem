import React from "react";
import { useAuth } from "../context/AuthContext";

export default function LeaveTable({ leaves, onAction }) {
  const { activeUser } = useAuth();
  const role = activeUser.role;

  // Normalize status for display
  const formatStatus = (status) => status?.replace(/_/g, " ") || "-";

  // Show action column for all relevant roles
  const shouldShowActionColumn =
    role === "HUMAN_RESOURCE" || role === "PROJECT_MANAGER" || role === "TEAM_LEAD";

  if (!leaves || leaves.length === 0) {
    return (
      <div className="bg-white p-6 rounded-xl shadow-md text-center text-gray-500">
        No leave requests found.
      </div>
    );
  }

  return (
    <div className="overflow-x-auto bg-white shadow-lg rounded-xl border border-gray-200">
      <table className="w-full text-sm text-gray-700 border-collapse">
        <thead>
          <tr className="bg-gradient-to-r from-blue-600 to-indigo-600 text-white text-left">
            <th className="p-3 text-center font-semibold rounded-tl-xl">ID</th>
            {role != "EMPLOYEE" && <th className="p-3 text-center font-semibold">Employee</th>}
            <th className="p-3 text-center font-semibold">Leave Type</th>
            <th className="p-3 text-center font-semibold">Dates</th>
            <th className="p-3 text-center font-semibold">Leave Days</th>
            <th className="p-3 text-center font-semibold">Reason</th>
            <th className="p-3 text-center font-semibold">Status</th>
            {shouldShowActionColumn && (
              <th className="p-3 text-center font-semibold rounded-tr-xl">Action</th>
            )}
          </tr>
        </thead>

        <tbody>
          {leaves.map((leave, index) => (
            <tr
              key={leave.id ?? `leave-${index}`}
              className={`transition duration-150 ${index % 2 === 0 ? "bg-gray-50" : "bg-white"
                } hover:bg-blue-50`}
            >
              <td className="p-3 text-center font-medium text-gray-800">{leave.id}</td>
              {role != "EMPLOYEE" && <td className="p-3 text-center">{leave.username}</td>}
              <td className="p-3 text-center capitalize">{leave.leaveType?.toLowerCase() || "-"}</td>
              <td className="p-3 text-center text-gray-600">
                {leave.startDate} → {leave.endDate}
              </td>
              <td className="p-3 text-center">{leave.days ?? "-"}</td>
              <td className="p-3 text-center">{leave.reason || "-"}</td>
              <td className="p-3 text-center">
                <span
                  className={`px-3 py-1 rounded-full text-xs font-semibold ${leave.status === "APPROVED_BY_HUMAN_RESOURCE"
                    ? "bg-green-100 text--700"
                    : leave.status === "REJECTED"
                      ? "bg-red-100 text-red-700"
                      : "bg-yellow-100 text-yellow-700"
                    }`}
                >
                  {leave.status === "APPROVED_BY_HUMAN_RESOURCE" ? "APPROVED BY HR" : formatStatus(leave.status)}
                </span>
              </td>

              {shouldShowActionColumn && (
                <td className="p-3 text-center space-x-2">
                  {/* Always show buttons for the role */}
                  <button
                    className="bg-green-500 hover:bg-green-600 text-white px-3 py-1 rounded-lg text-xs font-semibold shadow-sm transition-all duration-200"
                    onClick={() => onAction(leave.id, `APPROVED BY ${role.replace("_", " ")}`)}
                  >
                    Approve
                  </button>
                  <button
                    className="bg-red-500 hover:bg-red-600 text-white px-3 py-1 rounded-lg text-xs font-semibold shadow-sm transition-all duration-200"
                    onClick={() => onAction(leave.id, "REJECTED")}
                  >
                    Reject
                  </button>
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
