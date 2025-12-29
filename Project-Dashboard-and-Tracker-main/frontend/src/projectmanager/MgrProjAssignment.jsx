import React, { useEffect, useState, useRef } from "react";
import { PlusCircle, Trash2, Edit, Loader2, MessageCircle } from "lucide-react";
import api from "../utils/api";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { useAuth } from "../context/AuthContext";

export default function MgrProjAssignment() {
  const { activeUser } = useAuth();
  const role = activeUser?.role || "";
  // Dropdown data
  const [teamLeads, setTeamLeads] = useState([]);
  const [projects, setProjects] = useState([]);
  const [milestones, setMilestones] = useState([]);
  const [disciplines, setDisciplines] = useState([]);

  // Assign form state
  const [selectedTeamLead, setSelectedTeamLead] = useState("");
  const [selectedProject, setSelectedProject] = useState("");
  const [selectedMilestone, setSelectedMilestone] = useState("");
  const [selectedDiscipline, setSelectedDiscipline] = useState("");
  const [startDate, setStartDate] = useState(null);
  const [dueDate, setDueDate] = useState(null);

  // Assignments table
  const [assignments, setAssignments] = useState([]);
  const [editId, setEditId] = useState(null);

  const [teamleadAssignments, setTeamleadAssignments] = useState([]);
  const [updatingId, setUpdatingId] = useState(null);

  const assignmentsRef = useRef([]);
  const sleepLockRef = useRef(false);
  const sleepPausedIdsRef = useRef(new Set());

  const formatTime = (seconds) => {
    if (!seconds || seconds <= 0) return "00:00:00";

    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    return `${String(hrs).padStart(2, "0")}:${String(mins).padStart(2, "0")}:${String(secs).padStart(2, "0")}`;
  };

  const STATUS_OPTIONS = [
    { label: "Not Started", value: "NOT_STARTED" },
    { label: "In Progress", value: "IN_PROGRESS" },
    { label: "On Hold", value: "ON_HOLD" },
    { label: "Completed", value: "COMPLETED" }
  ];

  const statusColors = {
    NOT_STARTED: "bg-gray-300 text-gray-800",
    IN_PROGRESS: "bg-blue-300 text-blue-800",
    ON_HOLD: "bg-orange-300 text-orange-800",
    COMPLETED: "bg-green-300 text-green-800",
  };

  // Fetch initial dropdowns
  useEffect(() => {
    (role === "PROJECT_MANAGER") ? api.get("/employees/getProjectManagers/TEAM_LEAD")
      .then(res => setTeamLeads(res.data))
      .catch(err => console.error(err)) : api.get("/employees/getProjectManagers/EMPLOYEE")
        .then(res => setTeamLeads(res.data))
        .catch(err => console.error(err))

    api.get("/projects/getAllByUser")
      .then(res => setProjects(res.data))
      .catch(err => console.error(err));
  }, []);

  // Fetch milestones & disciplines whenever project changes
  useEffect(() => {
    if (!selectedProject) return;
    if (role === "PROJECT_MANAGER") {
      api.get(`/projects/${selectedProject}/milestones`)
        .then(res => setMilestones(res.data))
        .catch(err => console.error(err));

      api.get(`/projects/${selectedProject}/disciplines`)
        .then(res => setDisciplines(res.data))
        .catch(err => console.error(err));
    } else if (role === "TEAM_LEAD") {
      api.get(`/assignment/assign/${selectedProject}/milestones`, { withCredentials: true })
        .then(res => setMilestones(res.data))
        .catch(err => console.error(err));

      api.get(`/assignment/assign/${selectedProject}/disciplines`, { withCredentials: true })
        .then(res => setDisciplines(res.data))
        .catch(err => console.error(err));
    }


  }, [selectedProject]);

  // Fetch assignments
  const fetchAssignments = () => {
    api.get("/assignment/getAllAssignments", { withCredentials: true })
      .then(res => {
        if (res.data.success) setAssignments(res.data.data);
      })
      .catch(err => console.error(err));
  };

  const fetchTeamleadAssignments = () => {
    api.get("/assignment/getAllTeamLeadAssignments", { withCredentials: true })
      .then(res => {
        if (res.data.success) setTeamleadAssignments(res.data.data);
      })
      .catch(err => console.error(err));
  };

  useEffect(() => {
    fetchAssignments();
    if (role === "TEAM_LEAD")
      fetchTeamleadAssignments();
  }, []);

  const formatDate = (date) => {
    if (!date) return null;
    const d = new Date(date);
    const month = (d.getMonth() + 1).toString().padStart(2, "0");
    const day = d.getDate().toString().padStart(2, "0");
    return `${d.getFullYear()}-${month}-${day}`;
  };

  // Assign or Edit
  const handleSubmit = async () => {
    if (!selectedTeamLead || !selectedProject || selectedMilestone === "" || selectedDiscipline === "" || !startDate || !dueDate) {
      alert("All fields are required!");
      return;
    }

    const payload = {
      teamLeadId: selectedTeamLead,
      projectId: Number(selectedProject),
      milestoneId: Number(selectedMilestone),
      disciplineId: Number(selectedDiscipline),
      startDate: formatDate(startDate),
      dueDate: formatDate(dueDate),
      status: "ASSIGNED"
    };

    try {
      const endpoint = editId ? `/assignment/assign/${editId}` : "/assignment/assignProject";
      const method = editId ? "put" : "post";

      const res = await api[method](endpoint, payload, { withCredentials: true });

      if (res.data.success) {
        alert(res.data.message);
        fetchAssignments(); // Refresh table
        resetForm();
      } else {
        alert("Failed: " + res.data.message);
      }
    } catch (err) {
      console.error(err);
      alert("Something went wrong!");
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure to delete?")) return;
    try {
      const res = await api.delete(`/assignment/deleteAssignment/${id}`, { withCredentials: true });
      fetchAssignments();
      alert(res.data.message || "Deleted successfully");
    } catch (err) {
      console.error(err);
      alert("Delete failed!");
    }
  };

  const handleEdit = async (assignment) => {
    try {
      setEditId(assignment.id);

      const teamLead = teamLeads.find(tl => tl.name === assignment.employeeName);
      const project = projects.find(p => p.name === assignment.projectName);

      if (!project) {
        console.error("Project not found for assignment", assignment);
        return;
      }

      setSelectedTeamLead(teamLead ? String(teamLead.employeeId) : "");
      setSelectedProject(String(project.id));
      setStartDate(new Date(assignment.startDate));
      setDueDate(new Date(assignment.dueDate));

      let milestonesData = [];
      let disciplinesData = [];

      const milestonesUrl =
        role === "PROJECT_MANAGER"
          ? `/projects/${project.id}/milestones`
          : `/assignment/assign/${project.id}/milestones`;

      const disciplinesUrl =
        role === "PROJECT_MANAGER"
          ? `/projects/${project.id}/disciplines`
          : `/assignment/assign/${project.id}/disciplines`;

      try {
        const [milestonesRes, disciplinesRes] = await Promise.all([
          api.get(milestonesUrl),
          api.get(disciplinesUrl)
        ]);

        milestonesData = milestonesRes.data || [];
        disciplinesData = disciplinesRes.data || [];

        setMilestones(milestonesData);
        setDisciplines(disciplinesData);
      } catch (error) {
        console.error("Failed to load milestones or disciplines", error);
        setMilestones([]);
        setDisciplines([]);
        return; // ⛔ Stop execution to avoid mapping crash
      }

      // ✅ Map assignment milestones to IDs (FIXED)
      const milestoneId = String(
        milestonesData.find(m => assignment.milestoneName === m.name)?.id || ""
      );
      setSelectedMilestone(milestoneId);

      // ✅ Map assignment disciplines to IDs (FIXED)
      const disciplineId = String(disciplinesData
        .find(d =>
          assignment.disciplineName === d.name)?.id || ""
      );
      setSelectedDiscipline(disciplineId);

    } catch (err) {
      console.error("handleEdit failed:", err);
    }
  };

  useEffect(() => {
    assignmentsRef.current = teamleadAssignments;
  }, [teamleadAssignments]);

  useEffect(() => {
    let last = Date.now();

    const pauseTimers = async () => {
      const running = assignmentsRef.current.filter(
        a => a.status === "IN_PROGRESS"
      );

      for (const a of running) {
        if (!sleepPausedIdsRef.current.has(a.id)) {
          sleepPausedIdsRef.current.add(a.id);
          await api.patch(`/assignment/pauseTimer/${a.id}`, {}, { withCredentials: true });
        }
      }
    };

    const resumeTimers = async () => {
      for (const id of sleepPausedIdsRef.current) {
        await api.patch(`/assignment/startTimer/${id}`, {}, { withCredentials: true });
      }
      sleepPausedIdsRef.current.clear();
      sleepLockRef.current = false;
    };

    const detectSleep = async () => {
      const now = Date.now();
      if (now - last > 20000 && !sleepLockRef.current) {
        console.log("💤 SYSTEM SLEEP");
        sleepLockRef.current = true;
        await pauseTimers();
      }
      last = now;
    };

    const onLock = async () => {
      if (!sleepLockRef.current) {
        console.log("🔒 SYSTEM LOCK");
        sleepLockRef.current = true;
        await pauseTimers();
      }
    };

    const onUnlock = async () => {
      if (sleepPausedIdsRef.current.size > 0) {
        console.log("🔓 SYSTEM UNLOCK");
        await resumeTimers();
      }
    };

    const interval = setInterval(detectSleep, 5000);

    window.addEventListener("blur", onLock);        // ✅ Win + L
    window.addEventListener("pagehide", onLock);   // ✅ Sleep / tab kill
    window.addEventListener("focus", onUnlock);    // ✅ Unlock

    document.addEventListener("visibilitychange", () => {
      document.hidden ? onLock() : onUnlock();
    });

    return () => {
      clearInterval(interval);
      window.removeEventListener("blur", onLock);
      window.removeEventListener("pagehide", onLock);
      window.removeEventListener("focus", onUnlock);
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

      if (res.data.success) {
        fetchTeamleadAssignments();
      }
    } catch (err) {
      console.error(err);
      alert("Status update failed");
    } finally {
      setUpdatingId(null);
    }
  };


  const resetForm = () => {
    setEditId(null);
    setSelectedTeamLead("");
    setSelectedProject("");
    setSelectedMilestone("");
    setMilestones([]);
    setSelectedDiscipline("");
    setDisciplines([]);
    setStartDate(null);
    setDueDate(null);
  };

  return (
    <div className="p-6">
      <h1 className="text-4xl font-extrabold mb-8 flex items-center gap-4 text-indigo-600 drop-shadow-sm">
        <span className="text-3xl">📌</span>
        <span className="tracking-wide">Project Assignment</span>
      </h1>

      {/* Form */}
      <div className="bg-white rounded-2xl shadow-lg p-6 border border-indigo-100">
        <h2 className="text-2xl font-semibold mb-6 text-indigo-700 flex items-center gap-2">
          <PlusCircle size={22} /> Assign / Edit Project
        </h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
          <select className="border rounded-lg p-2 mt-2 bg-white text-sm shadow-sm focus:ring-2 focus:ring-indigo-300" value={selectedTeamLead} onChange={e => setSelectedTeamLead(e.target.value)}>
            {(role == "PROJECT_MANAGER") ? <option value="">Select Team Lead</option> : <option value="">Select Employee</option>}
            {teamLeads.map(tl => <option key={tl.employeeId} value={tl.employeeId}>{tl.name}</option>)}
          </select>

          <select className="border rounded-lg p-2 mt-2 bg-white text-sm shadow-sm focus:ring-2 focus:ring-indigo-300" value={selectedProject} onChange={e => setSelectedProject(e.target.value)}>
            <option value="">Select Project</option>
            {projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
          </select>

          {/* <select
            multiple
            className="border rounded-lg p-2 mt-2 bg-white text-sm shadow-sm focus:ring-2 focus:ring-indigo-300" // increase height for multiple selection
            value={selectedMilestones}
            onChange={(e) => {
              const options = Array.from(e.target.selectedOptions, option => option.value);
              setSelectedMilestones(options);
            }}
          >
            <option value="">Select Milestones</option>
            {milestones.map(m => (
              <option key={m.id} value={m.id}>
                {m.name}
              </option>
            ))}
          </select> */}

          <select
            className="border rounded-lg p-2 mt-2 bg-white text-sm shadow-sm focus:ring-2 focus:ring-indigo-300"
            value={selectedMilestone}
            onChange={(e) => setSelectedMilestone(e.target.value)}
          >
            <option value="">Select Milestone</option>
            {milestones.map(m => (
              <option key={m.id} value={m.id}>
                {m.name}
              </option>
            ))}
          </select>


          {/* <select
            multiple
            className="border rounded-lg p-2 mt-2 bg-white text-sm shadow-sm focus:ring-2 focus:ring-indigo-300" // increase height for multiple selection
            value={selectedDisciplines}
            onChange={(e) => {
              const options = Array.from(e.target.selectedOptions, option => option.value);
              setSelectedDisciplines(options);
            }}
          >
            <option value="">Select Disciplines</option>
            {disciplines.map(d => (
              <option key={d.id} value={d.id}>
                {d.name}
              </option>
            ))}
          </select> */}

          <select
            className="border rounded-lg p-2 mt-2 bg-white text-sm shadow-sm focus:ring-2 focus:ring-indigo-300"
            value={selectedDiscipline}
            onChange={(e) => setSelectedDiscipline(e.target.value)}
          >
            <option value="">Select Discipline</option>
            {disciplines.map(d => (
              <option key={d.id} value={d.id}>
                {d.name}
              </option>
            ))}
          </select>

          <DatePicker
            selected={startDate}
            onChange={date => {
              setStartDate(date);

              // ✅ If due date is before new start date, reset it
              if (dueDate && date > dueDate) {
                setDueDate(null);
              }
            }}
            minDate={new Date()}   // ✅ TODAY is the minimum allowed
            placeholderText="Start Date"
            className="border p-2 rounded w-full"
            dateFormat="yyyy-MM-dd"
          />


          <DatePicker
            selected={dueDate}
            onChange={date => setDueDate(date)}
            minDate={startDate || new Date()}  // ✅ Must be >= start date
            placeholderText="Due Date"
            className="border p-2 rounded w-full"
            dateFormat="yyyy-MM-dd"
          />

          <button className="bg-gradient-to-r from-indigo-500 to-purple-600 text-white px-6 py-2 rounded-xl
             shadow hover:shadow-lg transition-all flex items-center gap-2 justify-center" onClick={handleSubmit}>
            <PlusCircle size={18} /> {editId ? "Update" : "Assign"}
          </button>
        </div>
      </div>

      {/* Assignments Table */}
      {(role == "TEAM_LEAD") &&
        <div>
          <h2 className="text-lg font-medium mb-2">📋 Team Lead Tasks</h2>
          <table className="w-full bg-white rounded shadow">
            <thead className="bg-indigo-50 text-indigo-700">
              <tr>

                <th className="p-3 text-center font-semibold">Project</th>
                <th className="p-3 text-center font-semibold">Milestone</th>
                <th className="p-3 text-center font-semibold">Discipline</th>
                <th className="p-3 text-center font-semibold">Start Date</th>
                <th className="p-3 text-center font-semibold">Due Date</th>
                <th className="p-3 text-center font-semibold">Time Spent</th>
                <th className="p-3 text-center font-semibold">Status</th>
              </tr>
            </thead>
            <tbody>
              {teamleadAssignments.length === 0 ? (
                <tr>
                  <td colSpan="8" className="text-center p-3 text-gray-500">No assignments yet</td>
                </tr>
              ) : teamleadAssignments.map(a => (
                <tr key={a.id} className="border-b">
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
                  {/* Status Badge + Dropdown */}
                  <td className="p-2 text-center">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${statusColors[a.status]}`}
                    >
                      {a.status.replace("_", " ")}
                    </span>

                    <select
                      className="border p-2 rounded bg-gray-50 mt-2"
                      value={a.status}
                      disabled={updatingId === a.id}
                      onChange={(e) => updateStatus(a.id, e.target.value)}
                    >
                      {STATUS_OPTIONS.map((s) => (
                        <option key={s.value} value={s.value}>
                          {s.label}
                        </option>
                      ))}
                    </select>

                    {updatingId === a.id && (
                      <div className="text-indigo-600 text-sm flex items-center justify-center gap-1 mt-1 animate-pulse">
                        <Loader2 className="animate-spin w-4 h-4" />
                        Updating...
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      }

      {(role == "PROJECT_MANAGER") ? <h2 className="text-lg font-medium mb-2">📋 Team Lead Tasks</h2> : <h2 className="text-lg font-medium mb-2">📋 Employee Tasks</h2>}
      <table className="w-full bg-white rounded shadow">
        <thead className="bg-gray-100">
          <tr>
            {(role == "PROJECT_MANAGER") ? <th className="p-2 text-center">Team Lead</th> : <th className="p-2 text-center">Employee</th>}
            <th className="p-3 text-center font-semibold">Department</th>
            <th className="p-3 text-center font-semibold">Project</th>
            <th className="p-3 text-center font-semibold">Milestone</th>
            <th className="p-3 text-center font-semibold">Discipline</th>
            <th className="p-3 text-center font-semibold">Start Date</th>
            <th className="p-3 text-center font-semibold">Due Date</th>
            <th className="p-3 text-center font-semibold">Time Spent</th>
            <th className="p-3 text-center font-semibold">Status</th>
            <th className="p-3 text-center font-semibold">Actions</th>
          </tr>
        </thead>
        <tbody>
          {assignments.length === 0 ? (
            <tr>
              <td colSpan="8" className="text-center p-3 text-gray-500">No assignments yet</td>
            </tr>
          ) : assignments.map(a => (
            <tr key={a.id} className="border-b">
              <td className="p-2 text-center">{a.employeeName}</td>
              <td className="p-2 text-center">{a.department}</td>
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
              <td className="p-2 text-center"><span
                className={`px-4 py-1 rounded-full text-xs font-bold shadow-sm ${statusColors[a.status]}`}
              >
                {a.status.replace("_", " ")}
              </span></td>
              <td className="p-2 flex gap-3 justify-center">
                <button className="text-blue-600 hover:bg-blue-100 p-2 rounded-lg transition" onClick={() => handleEdit(a)}>
                  <Edit size={20} />
                </button>
                <button className="text-red-600 hover:bg-red-100 p-2 rounded-lg transition" onClick={() => handleDelete(a.id)}>
                  <Trash2 size={20} />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

    </div>
  );
}
