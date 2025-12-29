// src/layout/AdminLayout.jsx
import React from "react";
import Sidebar from "../components/Sidebar.jsx";
import { Outlet } from "react-router-dom"; // Adjust the path if needed
import { useState } from "react";


export default function AdminLayout({ children }) {
  const [isCollapsed, setIsCollapsed] = useState(false);
  const sidebarWidthClass = isCollapsed ? 'ml-60' : 'ml-90';
  return (
    <div className="flex min-h-screen">
            <Sidebar isCollapsed={isCollapsed} setIsCollapsed={setIsCollapsed} />
            <main className={`flex-1 transition-all duration-300 ${sidebarWidthClass} p-8`}>

      <div className="flex-1 flex flex-col">
      <Outlet/>
      </div>
      </main>
    </div>
  );
}
