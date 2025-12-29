
import React, { useState, useEffect } from 'react';
import api from '../utils/api.js';

const DetailedReport = () => {
    const [pivotData, setPivotData] = useState([]);
    const [tableColumns, setTableColumns] = useState([]); // Will hold the disciplines for the header
    
    // Filters
    const [projects, setProjects] = useState([]);
    const [milestones, setMilestones] = useState([]);
    const [selectedProject, setSelectedProject] = useState('');
    const [selectedMilestone, setSelectedMilestone] = useState('');
    
    const [isLoading, setIsLoading] = useState(false);

    // Fetch projects for the first dropdown
    useEffect(() => {
        api.get('/projects/list').then(res => setProjects(res.data)).catch(err => console.error("Failed to fetch projects", err));
    }, []);

    // Fetch milestones when a project is selected
    useEffect(() => {
        if (selectedProject) {
            api.get(`/projects/${selectedProject}/milestones`)
                .then(res => setMilestones(res.data))
                .catch(err => {
                    console.error("Failed to fetch milestones", err);
                    setMilestones([]);
                });
        } else {
            setMilestones([]);
        }
        // Reset selections and data when project changes
        setSelectedMilestone('');
        setPivotData([]);
        setTableColumns([]);
    }, [selectedProject]);

    const generateReport = async () => {
        if (!selectedProject) {
            alert("Please select a project.");
            return;
        }
        setIsLoading(true);
        try {
            let reportUrl = `/reports/detailed?projectId=${selectedProject}`;
            if (selectedMilestone) {
                reportUrl += `&milestoneId=${selectedMilestone}`;
            }

            const [employeesRes, disciplinesRes, reportRes] = await Promise.all([
                api.get('/employees'),
                api.get(`/projects/${selectedProject}/disciplines`),
                api.get(reportUrl)
            ]);

            const allEmployees = employeesRes.data.filter(emp => emp.role === 'EMPLOYEE');
            const projectDisciplines = disciplinesRes.data || [];
            const flatReportData = reportRes.data || [];

            // Set the columns for the table header
            setTableColumns(projectDisciplines);

            // --- THIS IS THE CORRECTED DATA PROCESSING LOGIC ---

            // 1. Create a simple lookup map: { "EmployeeName": { "DisciplineName": hours } }
            const reportMap = {};
            flatReportData.forEach(item => {
                const { employeeName, disciplineName, totalHours } = item;
                if (!reportMap[employeeName]) {
                    reportMap[employeeName] = {};
                }
                reportMap[employeeName][disciplineName] = totalHours;
            });

            // 2. Build the final data structure row by row
            const processedData = allEmployees.map(employee => {
                const employeeHours = reportMap[employee.name] || {};
                let totalRowHours = 0;
                
                // Calculate the total for the "Total Hours" column
                Object.values(employeeHours).forEach(hours => {
                    totalRowHours += hours;
                });
                
                return {
                    employeeId: employee.employeeId,
                    employeeName: employee.name,
                    totalHours: totalRowHours,
                    // The 'disciplineHours' object contains the hours for each specific discipline
                    disciplineHours: employeeHours 
                };
            });

            setPivotData(processedData);

        } catch (error) {
            console.error("Failed to generate detailed report:", error);
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

    // In src/reports/DetailedReport.js

    // ... (All your state, useEffect, and handler functions go here and should be correct)

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold mb-4">Detailed Report</h1>
            
            {/* --- THIS IS THE MISSING FILTER SECTION --- */}
            <div className="flex items-center space-x-4 mb-6 bg-white p-4 rounded-lg shadow-md">
                <select 
                    value={selectedProject} 
                    onChange={e => setSelectedProject(e.target.value)} 
                    className="p-2 border rounded-md w-64"
                >
                    <option value="">Select a Project</option>
                    {projects.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                </select>
                
                <select 
                    value={selectedMilestone} 
                    onChange={e => setSelectedMilestone(e.target.value)} 
                    disabled={!selectedProject} 
                    className="p-2 border rounded-md w-64 disabled:bg-gray-200"
                >
                    <option value="">All Milestones</option>
                    {milestones.map(m => <option key={m.id} value={m.id}>{m.name}</option>)}
                </select>
                
                <button 
                    onClick={generateReport} 
                    disabled={!selectedProject || isLoading} 
                    className="bg-blue-600 text-white font-bold py-2 px-4 rounded-md hover:bg-blue-700 disabled:bg-gray-400"
                >
                    {isLoading ? 'Generating...' : 'Generate Report'}
                </button>
            </div>
            {/* --- END OF FILTER SECTION --- */}


            {/* The table rendering part */}
            {isLoading ? <p>Loading report...</p> : (
                 <div className="overflow-x-auto bg-white rounded-lg shadow-md">
                    <table className="min-w-full">
                        <thead className="bg-gray-100">
                            <tr>
                                <th className="px-3 py-3 text-left text-xs font-bold uppercase sticky left-0 bg-gray-100">Employee</th>
                                <th className="px-4 py-3 text-left text-xs font-bold uppercase">Total Hours</th>
                                {tableColumns.map(d => <th key={d.id} className="px-4 py-3 text-left text-xs font-bold uppercase">{d.name}</th>)}
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {pivotData.map(empData => (
                                <tr key={empData.employeeId}>
                                    <td className="px-3 py-4 text-sm font-medium sticky left-0 bg-white">{empData.employeeName}</td>
                                    <td className="px-4 py-4 text-sm font-bold">{formatHours(empData.totalHours)}</td>
                                    {tableColumns.map(d => (
                                        <td key={d.id} className="px-4 py-4 text-sm">
                                            {formatHours(empData.disciplineHours[d.name])}
                                        </td>
                                    ))}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default DetailedReport;
  