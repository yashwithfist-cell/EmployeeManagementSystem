import React, { useState, useEffect } from 'react';
import api from '../utils/api';

const MgrProjects = () => {
    const [projects, setProjects] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        setIsLoading(true);
        try {
            const projectsRes = await api.get('/projects/getAllByUser', {
                withCredentials: true
            });
            setProjects(projectsRes.data);
        } catch (error) {
            console.error("Failed to fetch data:", error);
        }
        setIsLoading(false);
    };

    const formatHours = (decimalHours) => {
        if (!decimalHours) return "0:00";
        const totalMinutes = Math.round(decimalHours * 60);
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        return `${hours}:${minutes.toString().padStart(2, '0')}`;
    };

    if (isLoading) return <div className="p-8">Loading projects...</div>;

    return (
        <div className="p-8">
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold">All Projects</h1>
            </div>

            <div className="bg-white rounded-lg shadow-md overflow-hidden">
                <table className="min-w-full divide-y divide-gray-200">
                    <thead className="bg-gray-50">
                        <tr>
                            <th className="px-6 py-3 text-left text-xs font-bold uppercase">Project Name</th>
                            <th className="px-6 py-3 text-left text-xs font-bold uppercase">Client Name</th>
                            <th className="px-6 py-3 text-left text-xs font-bold uppercase">Hours Consumed</th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {projects.map((project) => (
                            <tr key={project.id}>
                                <td className="px-6 py-4">{project.name}</td>
                                <td className="px-6 py-4">{project.clientName || 'N/A'}</td>
                                <td className="px-6 py-4 font-bold">{formatHours(project.hoursConsumed)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

        </div>
    );
};

export default MgrProjects;