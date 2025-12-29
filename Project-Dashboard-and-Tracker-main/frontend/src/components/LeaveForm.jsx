import React, { useState, useEffect } from "react";
import api from "../utils/api";

export default function LeaveForm({ onSuccess }) {
  const [leave, setLeave] = useState({
    leaveType: "",
    startDate: "",
    endDate: "",
    reason: "",
    projectManagerName: "", // 👈 store manager name instead of ID
    teamLeadName: "",
  });

  const [leaveTypes, setLeaveTypes] = useState([]);
  const [managers, setManagers] = useState([]);
  const [errors, setErrors] = useState({});
  const today = new Date().toISOString().split("T")[0];
  const [teamLeads, setTeamLeads] = useState([]);

  // ✅ Fetch leave types
  useEffect(() => {
    api
      .get("/leaves/types")
      .then((res) => setLeaveTypes(res.data))
      .catch((err) => console.error("Error fetching leave types:", err));
  }, []);

  // ✅ Fetch project managers
  useEffect(() => {
    api
      .get("/leaves/getRoles/PROJECT_MANAGER",{withCredentials:true}) // Ensure backend returns [{ name: "Alice" }, { name: "Bob" }]
      .then((res) => setManagers(res.data))
      .catch((err) => console.error("Error fetching project managers:", err));
  }, []);

  useEffect(() => {
    api
      .get("/leaves/getRoles/TEAM_LEAD",{withCredentials:true}) // Ensure backend returns [{ name: "Alice" }, { name: "Bob" }]
      .then((res) => setTeamLeads(res.data))
      .catch((err) => console.error("Error fetching team leads:", err));
  }, []);

  // 🔍 Validation
  const validate = () => {
    const newErrors = {};

    if (!leave.leaveType.trim()) newErrors.leaveType = "Please select a leave type";
    if (!leave.startDate) newErrors.startDate = "Start date is required";
    if (!leave.endDate) newErrors.endDate = "End date is required";
    if (leave.startDate && leave.endDate && leave.startDate > leave.endDate)
      newErrors.endDate = "End date must be after start date";
    if (!leave.reason.trim()) newErrors.reason = "Reason is required";
    if (!leave.projectManagerName.trim()) newErrors.projectManagerName = "Please select a project manager";
    if (!leave.teamLeadName.trim()) newErrors.teamLeadName = "Please select a team lead";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 🧩 Handle change
  const handleChange = (e) => {
    setLeave({ ...leave, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: "" });
  };

  // 🚀 Submit handler
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    try {
      await api.post("/leaves", leave); // backend should expect projectManagerName
      setLeave({
        leaveType: "",
        startDate: "",
        endDate: "",
        reason: "",
        projectManagerName: "",
        teamLeadName: ""
      });
      setErrors({});
      onSuccess && onSuccess();
    } catch (error) {
      alert(error.response?.data?.message)
      // console.error("Error submitting leave:", error);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white p-6 rounded-xl shadow-md space-y-4"
    >
      <h2 className="text-lg font-semibold text-gray-700">Apply for Leave</h2>

      {/* Leave Type */}
      <div>
        <select
          name="leaveType"
          value={leave.leaveType}
          onChange={handleChange}
          className={`w-full border p-2 rounded-md ${errors.leaveType ? "border-red-500" : ""
            }`}
        >
          <option value="">Select Leave Type</option>
          {leaveTypes.map((type) => (
            <option key={type} value={type}>
              {type.charAt(0) + type.slice(1).toLowerCase()}
            </option>
          ))}
        </select>
        {errors.leaveType && (
          <p className="text-red-500 text-sm mt-1">{errors.leaveType}</p>
        )}
      </div>

      {/* TEAM LEAD Name */}
      <div>
        <select
          name="teamLeadName"
          value={leave.teamLeadName}
          onChange={handleChange}
          className={`w-full border p-2 rounded-md ${errors.teamLeadName ? "border-red-500" : ""
            }`}
        >
          <option value="">Select Team Lead</option>
          {teamLeads.map((m) => (
            <option key={m} value={m}>
              {m}
            </option>
          ))}
        </select>
        {errors.teamLeadName && (
          <p className="text-red-500 text-sm mt-1">{errors.teamLeadName}</p>
        )}
      </div>

      {/* Project Manager Name */}
      <div>
        <select
          name="projectManagerName"
          value={leave.projectManagerName}
          onChange={handleChange}
          className={`w-full border p-2 rounded-md ${errors.projectManagerName ? "border-red-500" : ""
            }`}
        >
          <option value="">Select Project Manager</option>
          {managers.map((m) => (
            <option key={m} value={m}>
              {m}
            </option>
          ))}
        </select>
        {errors.projectManagerName && (
          <p className="text-red-500 text-sm mt-1">{errors.projectManagerName}</p>
        )}
      </div>

      {/* Dates */}
      <div className="flex gap-3">
        <div className="w-1/2">
          <input
            type="date"
            name="startDate"
            value={leave.startDate}
            onChange={handleChange}
            min={today}   // ⬅️ prevent selecting past date
            className={`w-full border p-2 rounded-md ${errors.startDate ? "border-red-500" : ""
              }`}
          />
          {errors.startDate && (
            <p className="text-red-500 text-sm mt-1">{errors.startDate}</p>
          )}
        </div>

        <div className="w-1/2">
          <input
            type="date"
            name="endDate"
            value={leave.endDate}
            onChange={handleChange}
            min={leave.startDate || today} // ⬅️ end date can't be before start date
            className={`w-full border p-2 rounded-md ${errors.endDate ? "border-red-500" : ""
              }`}
          />
          {errors.endDate && (
            <p className="text-red-500 text-sm mt-1">{errors.endDate}</p>
          )}
        </div>
      </div>

      {/* Reason */}
      <div>
        <textarea
          name="reason"
          value={leave.reason}
          onChange={handleChange}
          placeholder="Reason"
          className={`w-full border p-2 rounded-md ${errors.reason ? "border-red-500" : ""
            }`}
        />
        {errors.reason && (
          <p className="text-red-500 text-sm mt-1">{errors.reason}</p>
        )}
      </div>

      {/* Submit */}
      <button
        type="submit"
        className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700"
      >
        Submit
      </button>
    </form>
  );
}
