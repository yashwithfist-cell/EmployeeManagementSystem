import React from "react";
import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext.js";

// Import Pages
import Login from "./Login/Login.js";
import Home from "./pages/Home.js";

import SALayout from "./layout/SALayout.js";
import SADashboard from "./sa/SADashboard.jsx";
import MilestoneDataReport from "./sa/MilestoneDataReport.js";
import SAProjects from "./sa/SAProjects.jsx";
import SAMilestones from "./sa/SAMilestones.jsx";
import MasterDataReport from "./sa/MasterDataReport.js";
import SAEmployees from "./sa/SAEmployees.jsx";

import EmployeeLayout from "./layout/EmployeeLayout.jsx";
import EmployeeDashboard from "./employee/EmployeeDashboard.jsx";
import EmployeeLeave from "./pages/EmployeeLeave.jsx";
import SALeaveApproval from "./pages/SALeaveApproval.jsx";
import SAAttendanceLog from "./sa/SAAttendanceLog.jsx";
import EmpAttendanceLog from "./employee/EmpAttendanceLog.jsx";
import EmpSalarySlip from "./employee/EmpSalarySlip.jsx";
import SystemLog from "./employee/SystemLog.jsx";
import SASystemLog from "./sa/SASystemLog.jsx";
import EmpNotification from "./employee/EmpNotification.jsx";
import MgrProjAssignment from "./projectmanager/MgrProjAssignment.jsx";
import EmpProjAssignment from "./employee/EmpProjAssignment.jsx";
import MgrProjects from "./projectmanager/MgrProjects.jsx";


// 🔒 NEW — Protected Route for Multi-User
function RequireAuth({ children }) {
  const { activeUser } = useAuth();

  if (!activeUser) {
    return <Navigate to="/" replace />;
  }

  return children;
}


// APP CONTENT
function AppContent() {
  const { activeUser } = useAuth();

  return (
    <Router>
      <Routes>
        {/* LOGIN ROUTE */}
        <Route
          path="/"
          element={activeUser ? <Navigate to="/home" replace /> : <Login />}
        />

        {/* HOME REDIRECTOR */}
        <Route
          path="/home"
          element={
            <RequireAuth>
              <Home />
            </RequireAuth>
          }
        />

        {/* SYSTEM ADMIN ROUTES */}
        <Route
          element={
            <RequireAuth>
              <SALayout />
            </RequireAuth>
          }
        >
          <Route path="/sadashboard" element={<SADashboard />} />
          <Route path="/saprojects" element={<SAProjects />} />
          <Route path="/samilestones" element={<SAMilestones />} />
          <Route path="/saemployees" element={<SAEmployees />} />
          <Route path="/masterdatareport" element={<MasterDataReport />} />
          <Route path="/milestonedatareport" element={<MilestoneDataReport />} />
          <Route path="/saleaveapproval" element={<SALeaveApproval />} />
          <Route path="/saattendancelog" element={<SAAttendanceLog />} />
          <Route path="/sasystemlog" element={<SASystemLog />} />
          <Route path="/sanotification" element={<EmpNotification />} />
          <Route path="/pmnotification" element={<EmpNotification />} />
          <Route path="/tlnotification" element={<EmpNotification />} />
          <Route path="/mgrprojassignment" element={<MgrProjAssignment />} />
          <Route path="/mgrprojects" element={<MgrProjects />} />
          <Route path="/hremployees" element={<SAEmployees />} />
          <Route path="/hrdashboard" element={<SADashboard />} />
          <Route path="/hrleaveapproval" element={<SALeaveApproval />} />
          <Route path="/hrattendancelog" element={<SAAttendanceLog />} />
          <Route path="/hrsystemlog" element={<SASystemLog />} />
          <Route path="/hrnotification" element={<EmpNotification />} />
        </Route>

        {/* EMPLOYEE ROUTES */}
        <Route
          element={
            <RequireAuth>
              <EmployeeLayout />
            </RequireAuth>
          }
        >
          <Route path="/employeedashboard" element={<EmployeeDashboard />} />
          <Route path="/employeeleave" element={<EmployeeLeave />} />
          <Route path="/empattendancelog" element={<EmpAttendanceLog />} />
          <Route path="/empsalaryslip" element={<EmpSalarySlip />} />
          <Route path="/empsystemlog" element={<SystemLog />} />
          <Route path="/empnotification" element={<EmpNotification />} />
          <Route path="/empprojassignment" element={<EmpProjAssignment />} />
        </Route>
      </Routes>
    </Router>
  );
}


// WRAPPER
function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;
