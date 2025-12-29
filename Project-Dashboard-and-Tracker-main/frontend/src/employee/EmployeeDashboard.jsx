import React, { useState, useEffect } from 'react';
import api from '../utils/api'; // Ensure this path points to your configured axios instance
import { FaHourglassHalf, FaPlusCircle, FaTimesCircle, FaCheckCircle, FaProjectDiagram, FaCalendarAlt, FaClock } from 'react-icons/fa'; // Importing icons for visual cues

const EmployeeDashboard = () => {
    // State for the list of worklogs displayed in the table
    const [myWorklogs, setMyWorklogs] = useState([]);
    // State for the "Add New Entry" form
    const [newEntry, setNewEntry] = useState({
        date: new Date().toISOString().split("T")[0],
        task: "",
        description: "",
        projectId: "",
        milestoneId: "",
        disciplineId: "",
        startTime: "",
        endTime: "",
    });

    // State for the cascading dropdowns
    const [projects, setProjects] = useState([]);
    const [milestones, setMilestones] = useState([]);
    const [disciplines, setDisciplines] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false); // New state for form submission

    // Fetch initial data (employee's worklogs and all projects) when the component loads
    useEffect(() => {
        const fetchData = async () => {
            try {
                const [worklogsRes, projectsRes] = await Promise.all([
                    api.get("/worklogs/my"),
                    api.get("/projects/list"),
                ]);
                setMyWorklogs(worklogsRes.data);
                setProjects(projectsRes.data);
            } catch (error) {
                console.error("Failed to fetch initial data:", error);
                alert("Could not load initial data. Please check the console for details.");
            }
        };
        fetchData();
    }, []);

    // Handle cascading dropdowns and resetting state when the project changes
    useEffect(() => {
        const fetchProjectDetails = async () => {
            if (newEntry.projectId) {
                setIsLoading(true);
                setNewEntry(prev => ({ ...prev, milestoneId: '', disciplineId: '' }));
                setMilestones([]);
                setDisciplines([]);

                try {
                    const [milestonesRes, disciplinesRes] = await Promise.all([
                        api.get(`/projects/${newEntry.projectId}/milestones`),
                        api.get(`/projects/${newEntry.projectId}/disciplines`),
                    ]);
                    setMilestones(milestonesRes.data);
                    setDisciplines(disciplinesRes.data);
                } catch (error) {
                    console.error("Failed to fetch project details:", error);
                }
                setIsLoading(false);
            } else {
                setMilestones([]);
                setDisciplines([]);
            }
        };
        fetchProjectDetails();
    }, [newEntry.projectId]);

    // Handle input changes in the form
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setNewEntry((prev) => ({ ...prev, [name]: value }));
    };

    // Handle form submission to add a new worklog entry
    const handleAddEntry = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        // Client-side validation (milestoneId is now optional)
        if (!newEntry.projectId || !newEntry.disciplineId) {
            alert("Please ensure a Project and Discipline are selected.");
            setIsSubmitting(false);
            return;
        }

        try {
            const response = await api.post("/worklogs/my", newEntry);
            setMyWorklogs((prevLogs) => [response.data, ...prevLogs]); // Prepend new log
            // Reset the form after successful submission
            setNewEntry({
                date: new Date().toISOString().split("T")[0],
                task: "",
                description: "",
                projectId: "",
                milestoneId: "",
                disciplineId: "",
                startTime: "",
                endTime: "",
            });
        } catch (error) {
            console.error("Failed to save worklog entry:", error);
            const errorMsg = error.response?.data?.message || "Could not save entry.";
            alert(`Error: ${errorMsg}`);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        // Main container with a clean background and padding
        <div className="min-h-screen bg-gray-50 p-6 sm:p-10">
            <h1 className="text-4xl font-extrabold text-gray-800 mb-8 tracking-tight">My Daily Worklog</h1>

            {/* Form for adding a new entry with a more prominent shadow and rounded corners */}
            <div className="bg-white p-8 rounded-2xl shadow-xl border border-gray-100 mb-10">
                <h2 className="text-2xl font-semibold text-gray-700 mb-6 flex items-center">
                    <FaPlusCircle className="text-green-500 mr-2" /> Add a New Worklog Entry
                </h2>
                <form onSubmit={handleAddEntry} className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {/* Column 1 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Date</label>
                        <div className="flex items-center relative">
                            <FaCalendarAlt className="absolute left-3 text-gray-400" />
                            <input type="date" name="date" value={newEntry.date} onChange={handleInputChange} className="pl-10 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" required />
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Project</label>
                        <div className="flex items-center relative">
                            <FaProjectDiagram className="absolute left-3 text-gray-400" />
                            <select name="projectId" value={newEntry.projectId} onChange={handleInputChange} className="pl-10 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors bg-white" required>
                                <option value="">Select a Project</option>
                                {projects.map((p) => (<option key={p.id} value={p.id}>{p.name}</option>))}
                            </select>
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Milestone (Optional)</label>
                        <select name="milestoneId" value={newEntry.milestoneId} onChange={handleInputChange} disabled={!newEntry.projectId || isLoading} className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors disabled:bg-gray-100 disabled:cursor-not-allowed">
                            <option value="">{isLoading ? "Loading..." : "Select a Milestone (Optional)"}</option>
                            {milestones.map((m) => (<option key={m.id} value={m.id}>{m.name}</option>))}
                        </select>
                    </div>

                    {/* Column 2 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Discipline</label>
                        <select name="disciplineId" value={newEntry.disciplineId} onChange={handleInputChange} disabled={!newEntry.projectId || isLoading} className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors disabled:bg-gray-100 disabled:cursor-not-allowed" required>
                            <option value="">{isLoading ? "Loading..." : "Select a Discipline"}</option>
                            {disciplines.map((d) => (<option key={d.id} value={d.id}>{d.name}</option>))}
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Task</label>
                        <input type="text" name="task" value={newEntry.task} onChange={handleInputChange} placeholder="e.g., Coordination, Modelling" className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Description</label>
                        <input type="text" name="description" value={newEntry.description} onChange={handleInputChange} placeholder="Briefly describe the task" className="pl-3 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" />
                    </div>

                    {/* Column 3 */}
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">Start Time</label>
                        <div className="flex items-center relative">
                            <FaClock className="absolute left-3 text-gray-400" />
                            <input type="time" name="startTime" value={newEntry.startTime} onChange={handleInputChange} className="pl-10 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" required />
                        </div>
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-600 mb-1">End Time</label>
                        <div className="flex items-center relative">
                            <FaClock className="absolute left-3 text-gray-400" />
                            <input type="time" name="endTime" value={newEntry.endTime} onChange={handleInputChange} className="pl-10 pr-3 py-2 w-full border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors" required />
                        </div>
                    </div>
                    <div className="self-end pt-5">
                        <button type="submit" disabled={isSubmitting} className="w-full bg-blue-600 text-white font-bold py-3 px-6 rounded-lg shadow-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 transition-all duration-300 transform hover:scale-105 disabled:bg-gray-400 disabled:cursor-not-allowed">
                            {isSubmitting ? (
                                <span className="flex items-center justify-center">
                                    <FaHourglassHalf className="animate-spin mr-2" /> Saving...
                                </span>
                            ) : (
                                "Add Entry"
                            )}
                        </button>
                    </div>
                </form>
            </div>

            {/* Table of existing worklogs with a refreshed look */}
            <div className="bg-white p-6 rounded-2xl shadow-xl border border-gray-100">
                <h2 className="text-2xl font-semibold text-gray-700 mb-4">My Worklogs</h2>
                <div className="overflow-x-auto">
                    <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                            <tr>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Date</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Project</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Milestone</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Discipline</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Task</th>
                                <th className="px-4 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">Hours Worked</th>
                            </tr>
                        </thead>
                        <tbody className="bg-white divide-y divide-gray-200">
                            {isLoading ? (
                                <tr>
                                    <td colSpan="6" className="text-center py-6 text-gray-500">
                                        <FaHourglassHalf className="animate-spin inline-block mr-2" /> Loading worklogs...
                                    </td>
                                </tr>
                            ) : myWorklogs.length > 0 ? (
                                myWorklogs.map((log) => (
                                    <tr key={log.id} className="hover:bg-gray-50 transition-colors duration-200">
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.date}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm font-medium text-gray-900">{log.projectName}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.milestoneName || 'None'}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.disciplineName}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm text-gray-700">{log.task}</td>
                                        <td className="px-4 py-3 whitespace-nowrap text-sm font-bold text-gray-900">{log.hoursWorked ? log.hoursWorked.toFixed(2) : '0.00'}</td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="6" className="text-center py-6 text-gray-500">
                                        <FaTimesCircle className="inline-block text-red-400 mr-2" /> No worklogs found for this period.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default EmployeeDashboard;