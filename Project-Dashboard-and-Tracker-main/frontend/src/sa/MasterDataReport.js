import React, { useEffect, useState } from 'react';
import api from '../utils/api.js';

const MasterDataReport = () => {
    const [pivotData, setPivotData] = useState([]);
    const [projects, setProjects] = useState([]);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    // Sets the initial date range to the previous month
    useEffect(() => {
        const today = new Date();
        const firstDayOfLastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
        const lastDayOfLastMonth = new Date(today.getFullYear(), today.getMonth(), 0);
        setStartDate(firstDayOfLastMonth.toISOString().split('T')[0]);
        setEndDate(lastDayOfLastMonth.toISOString().split('T')[0]);
    }, []);

    // Fetches and processes data when the "Generate Report" button is clicked
    const handleGenerateReport = async () => {
        if (!startDate || !endDate) {
            alert("Please select both a start and end date.");
            return;
        }
        setIsLoading(true);

        try {
            const [employeeRes, projectRes, reportRes] = await Promise.all([
                api.get('/employees'),
                api.get('/projects'),
                api.get(`/reports/master?startDate=${startDate}&endDate=${endDate}`)
            ]);

            const allEmployees = employeeRes.data.filter(emp => emp.role === 'EMPLOYEE');
            const allProjects = projectRes.data || [];
            const flatReportData = reportRes.data || [];

            setProjects(allProjects); // Set projects for the table headers

            // --- Corrected data processing logic ---
            
            // 1. Create a map for quick lookups
            const reportMap = new Map();
            flatReportData.forEach(item => {
                if (!reportMap.has(item.rowHeader)) {
                    reportMap.set(item.rowHeader, new Map());
                }
                reportMap.get(item.rowHeader).set(item.columnHeader, item.totalHours);
            });

            // 2. Build the final data structure for the table
            const processedData = allEmployees.map(employee => {
                const employeeData = reportMap.get(employee.name);
                const projectHours = {};
                let totalHours = 0;

                if (employeeData) {
                    allProjects.forEach(project => {
                        const hours = employeeData.get(project.name) || 0;
                        projectHours[project.name] = hours;
                        totalHours += hours;
                    });
                } else {
                    allProjects.forEach(project => {
                        projectHours[project.name] = 0;
                    });
                }
                
                return {
                    employeeId: employee.employeeId,
                    employeeName: employee.name,
                    totalHours: totalHours,
                    projectHours: projectHours
                };
            });

            setPivotData(processedData);

        } catch (error) {
            console.error("Failed to generate master report:", error);
        }
        setIsLoading(false);
    };

    // Helper function to format hours
    const formatHours = (decimalHours) => {
        if (!decimalHours) return "0:00";
        const totalMinutes = Math.round(decimalHours * 60);
        const hours = Math.floor(totalMinutes / 60);
        const minutes = totalMinutes % 60;
        return `${hours}:${minutes.toString().padStart(2, '0')}`;
    };

    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold mb-4">Master Data Report</h1>
            <div className="flex items-center space-x-4 mb-6 bg-white p-4 rounded-lg shadow-md">
                <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} className="p-2 border rounded-md" />
                <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} className="p-2 border rounded-md" />
                <button onClick={handleGenerateReport} disabled={isLoading} className="bg-blue-600 text-white font-bold py-2 px-4 rounded-md hover:bg-blue-700 disabled:bg-gray-400">
                    {isLoading ? 'Generating...' : 'Generate Report'}
                </button>
            </div>

            {isLoading ? <p>Loading report...</p> : (
                <div className="overflow-x-auto bg-white rounded-lg shadow-md">
                    {/* --- FINAL, COMPLETE TABLE JSX with formatting --- */}
                    <table className="min-w-full table-fixed">
                        <thead className="bg-gray-100">
                            <tr>
                                <th className="w-64 px-3 py-3 text-left text-xs font-bold uppercase sticky left-0 bg-gray-100 z-10">Employee</th>
                                <th className="w-32 px-4 py-3 text-left text-xs font-bold uppercase">Total Hours</th>
                                {projects.map(p => (
                                    <th key={p.id} className="w-40 px-4 py-3 text-left text-xs font-bold uppercase whitespace-nowrap truncate relative group">
                                        <span className="truncate">{p.name}</span>
                                        <span className="absolute bottom-full left-1/2 -translate-x-1/2 hidden group-hover:block bg-gray-700 text-white text-xs rounded py-1 px-2 z-20 whitespace-normal w-max">{p.name}</span>
                                    </th>
                                ))}
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200">
                            {pivotData.length > 0 ? (
                                pivotData.map(empData => (
                                    <tr key={empData.employeeId}>
                                        <td className="w-64 px-3 py-4 text-sm font-medium sticky left-0 bg-white whitespace-nowrap">{empData.employeeName}</td>
                                        <td className="w-32 px-4 py-4 text-sm font-bold">{formatHours(empData.totalHours)}</td>
                                        {projects.map(p => (
                                            <td key={p.id} className="w-40 px-4 py-4 text-sm">
                                                {formatHours(empData.projectHours[p.name])}
                                            </td>
                                        ))}
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan={projects.length + 2} className="text-center py-4 text-gray-500">
                                        No data found for the selected period. Click "Generate Report" to load data.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default MasterDataReport;
