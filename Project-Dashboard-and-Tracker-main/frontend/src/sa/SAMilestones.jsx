import React, { useEffect, useState } from 'react';
import api from '../utils/api';
import { useAuth } from "../context/AuthContext";

const SAMilestones = () => {
    const [milestones, setMilestones] = useState([]);
    const [projects, setProjects] = useState([]); // For the Add/Edit modal dropdowns
    const [isLoading, setIsLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [currentMilestone, setCurrentMilestone] = useState(null);

    const { activeUser } = useAuth();
    const role = activeUser?.role || "";

    // Fetch all necessary data when the component first loads
    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setIsLoading(true);
        try {
            // Fetch milestones with hours and the simple project list concurrently
            const projectsRes = (role === "PROJECT_MANAGER")
                ? await api.get('/projects/getAllByUser', { withCredentials: true })
                : await api.get('/projects/list');

            setProjects(projectsRes.data);

            // ✅ FIX IS RIGHT HERE
            const projectIds = projectsRes.data.map(project => project.id);

            const milestonesRes = (role === "PROJECT_MANAGER")
                ? await api.post('/milestones/getUserMilestones', projectIds)
                : await api.get('/milestones');


            // const [milestonesRes, projectsRes] = await Promise.all([
            //     api.get(milestoneUrl),
            //     api.get(projectUrl)
            // ]);
            setMilestones(milestonesRes.data);

        } catch (err) {
            console.error('Failed to fetch data:', err);
        }
        setIsLoading(false);
    };

    // --- MODAL AND FORM HANDLING ---

    const handleOpenModal = (milestone = null) => {
        if (milestone) { // Edit Mode
            setIsEditMode(true);
            setCurrentMilestone({
                milestoneId: milestone.milestoneId,
                name: milestone.milestoneName,
                dueDate: milestone.dueDate || '',
                projectId: projects.find(p => p.name === milestone.projectName)?.id || ''
            });
        } else { // Add Mode
            setIsEditMode(false);
            setCurrentMilestone({ name: '', dueDate: '', projectId: '' });
        }
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        setCurrentMilestone(null);
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setCurrentMilestone(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const milestoneData = {
            name: currentMilestone.name,
            dueDate: currentMilestone.dueDate,
            projectId: currentMilestone.projectId,
        };

        try {
            if (isEditMode) {
                await api.put(`/milestones/${currentMilestone.milestoneId}`, milestoneData);
            } else {
                await api.post('/milestones', milestoneData);
            }
            fetchData(); // Refresh the list with new data
            handleCloseModal();
        } catch (error) {
            console.error("Failed to save milestone:", error);
            alert("Could not save milestone. Please check the data.");
        }
    };

    const handleDelete = async (milestoneId) => {
        if (!window.confirm("Are you sure you want to delete this milestone?")) return;
        try {
            await api.delete(`/milestones/${milestoneId}`);
            // Optimistically update the UI by removing the item from state
            setMilestones(prev => prev.filter(m => m.milestoneId !== milestoneId));
        } catch (err) {
            console.error('Failed to delete milestone:', err);
            alert("Could not delete milestone.");
        }
    };

    // --- HELPER FUNCTIONS ---

    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        return new Date(dateString).toLocaleDateString('en-GB'); // Format: DD/MM/YYYY
    };

    const formatHours = (decimalHours) => {
        if (!decimalHours) return "0:00";
        const totalMinutes = Math.round(decimalHours * 60);
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        return `${hours}:${minutes.toString().padStart(2, '0')}`;
    };

    if (isLoading) return <div className="p-6">Loading milestones...</div>;

    return (
        <div className="p-6 bg-gray-50 min-h-screen">
            <div className="flex justify-between items-center mb-8">
                <h2 className="text-3xl font-bold text-gray-800">Project Milestones</h2>
                {(role != "PROJECT_MANAGER") &&
                    <button
                        onClick={() => handleOpenModal()}
                        className="bg-green-600 hover:bg-green-700 text-white font-semibold px-5 py-2 rounded-lg shadow-md"
                    >
                        + Add Milestone
                    </button>
                }
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                {milestones.map(milestone => (
                    <div key={milestone.milestoneId} className="bg-white rounded-xl shadow-lg p-6 border-l-4 border-indigo-500 flex flex-col">
                        <div className="flex-grow">
                            <h3 className="text-lg font-bold text-gray-800 truncate mb-4" title={milestone.milestoneName}>
                                {milestone.milestoneName}
                            </h3>
                            <div className="text-sm text-gray-600 space-y-2">
                                <p><strong className="w-24 inline-block">Project:</strong> {milestone.projectName || "N/A"}</p>
                                <p><strong className="w-24 inline-block">Due Date:</strong> {formatDate(milestone.dueDate)}</p>
                                <p className="font-bold text-gray-800">
                                    <strong className="w-24 inline-block font-semibold text-gray-600">Hours Used:</strong>
                                    {formatHours(milestone.hoursConsumed)}
                                </p>
                            </div>
                        </div>
                        {(role != "PROJECT_MANAGER") &&
                            <div className="mt-6 flex justify-end gap-2">
                                <button onClick={() => handleOpenModal(milestone)} className="bg-yellow-500 hover:bg-yellow-600 text-white text-xs font-medium px-4 py-1.5 rounded-lg">Edit</button>
                                <button onClick={() => handleDelete(milestone.milestoneId)} className="bg-red-600 hover:bg-red-700 text-white text-xs font-medium px-4 py-1.5 rounded-lg">Delete</button>
                            </div>
                        }
                    </div>
                ))}
            </div>

            {/* Add/Edit Modal */}
            {isModalOpen && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
                    <div className="bg-white p-8 rounded-lg w-full max-w-md">
                        <h3 className="text-2xl font-bold mb-6">{isEditMode ? 'Edit Milestone' : 'Add New Milestone'}</h3>
                        <form onSubmit={handleSubmit} className="space-y-4">
                            <select name="projectId" value={currentMilestone?.projectId || ''} onChange={handleInputChange} className="w-full p-2 border rounded" required>
                                <option value="">Select a Project</option>
                                {projects.map(p => (<option key={p.id} value={p.id}>{p.name}</option>))}
                            </select>
                            <input type="text" name="name" value={currentMilestone?.name || ''} onChange={handleInputChange} placeholder="Milestone Name" className="w-full p-2 border rounded" required />
                            <div>
                                <label className="text-sm text-gray-600">Due Date</label>
                                <input type="date" name="dueDate" value={currentMilestone?.dueDate || ''} onChange={handleInputChange} className="w-full p-2 border rounded" />
                            </div>
                            <div className="flex justify-end gap-2 pt-4">
                                <button type="button" onClick={handleCloseModal} className="bg-gray-500 text-white px-4 py-2 rounded">Cancel</button>
                                <button type="submit" className="bg-blue-600 text-white px-4 py-2 rounded">{isEditMode ? 'Save Changes' : 'Save'}</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SAMilestones;