import React from "react";
import { NavLink } from "react-router-dom";
import { FaBars, FaTimes, FaClipboardList, FaCalendarAlt, FaClock, FaFileInvoice, FaChartLine,FaBell,FaTasks} from "react-icons/fa";
import { useAuth } from "../context/AuthContext";
import { useEffect } from "react";

const navItems = [
  { label: "Worklog", path: "/employeedashboard", icon: <FaClipboardList /> },
  { label: "Leaves", path: "/employeeleave", icon: <FaCalendarAlt /> },
  { label: "Attendance Log", path: "/empattendancelog", icon: <FaClock /> },
  { label: "Salary Slip", path: "/empsalaryslip", icon: <FaFileInvoice /> },
  { label: "SystemLog", path: "/empsystemlog", icon: <FaChartLine className="text-green-600 hover:scale-110 duration-200" /> },
  { label: "Notifications", path: "/empnotification", icon: <FaBell className="text-yellow-500 hover:scale-110 duration-200" /> },
  { label: "Tasks", path: "/empprojassignment", icon: <FaTasks className="text-blue-600 hover:scale-110 duration-200" /> }
];

export default function Sidebar({ isCollapsed, setIsCollapsed }) {
  const toggleSidebar = () => setIsCollapsed(!isCollapsed);
  const { logout } = useAuth();

  // Auto collapse on small screens
useEffect(() => {
  const handleResize = () => {
    if (window.innerWidth < 768) {
      setIsCollapsed(true);
    }
  };
  handleResize();
  window.addEventListener("resize", handleResize);
  return () => window.removeEventListener("resize", handleResize);
}, []);


  return (
    <>
  <aside
  className={`bg-gray-900 text-white flex flex-col p-6 shadow-2xl z-50 transition-all duration-300 overflow-y-auto
    h-screen fixed md:static
    ${isCollapsed ? "w-20 -left-44 md:left-0" : "w-64 left-0 md:left-0"}
  `}
>



      {/* Sidebar Toggle Button */}
      <button
        onClick={toggleSidebar}
        className="text-white text-xl p-2 rounded-lg hover:bg-gray-800 self-end mb-4 transition-all duration-200"
        aria-label="Toggle Sidebar"
      >
        {isCollapsed ? <FaBars /> : <FaTimes />}
      </button>

      {/* Logo & Header */}
      <div className={`flex flex-col items-center mb-8 transition-all duration-300 ${isCollapsed ? "hidden" : ""}`}>
        <div className="relative w-24 h-24 mb-4">
          <img
            src="/FIST.jpg"
            alt="FIST Logo"
            className="w-24 h-24 rounded-full shadow-2xl border-4 border-white object-cover"
          />
          <span className="absolute -bottom-2 right-0 bg-green-500 w-4 h-4 rounded-full border-2 border-white"></span>
        </div>
        <h1 className="text-2xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-purple-600 to-indigo-500 tracking-wide text-center">
          Employee Dashboard
        </h1>
       
      </div>

      {/* Navigation Links */}
      <nav className="flex flex-col gap-3 flex-grow">
        {navItems.map(item => (
          <NavLink
            key={item.label}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-lg text-lg font-medium transition-all duration-300
              ${isActive 
                ? "bg-gradient-to-r from-purple-600 to-indigo-600 text-white shadow-lg scale-105" 
                : "text-gray-300 hover:bg-gray-800 hover:text-white hover:scale-105"}`
            }
          >
            <span className="text-xl">{item.icon}</span>
            {!isCollapsed && <span>{item.label}</span>}
          </NavLink>
        ))}
      </nav>
    </aside>
    
      {!isCollapsed && (
        <div
          className="fixed inset-0 bg-black bg-opacity-40 md:hidden z-40"
          onClick={toggleSidebar}
        />
      )}
    </>
  );
}
