// src/pages/DashboardAdmin.tsx
import React from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";

const DashboardAdmin: React.FC = () => {
  const { email, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const handleHome = () => {
    navigate("/dashboard/admin/menu");
  };

  return (
    <div className="p-4 max-w-4xl mx-auto">
      <header className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-bold">Bienvenido, {email}</h2>
        <div className="space-x-2">
          <button
            onClick={handleHome}
            className="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 transition"
          >
            Home
          </button>
          <button
            onClick={handleLogout}
            className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 transition"
          >
            Cerrar sesión
          </button>
        </div>
      </header>

      {/* Renderizado dinámico de rutas hijas */}
      <Outlet />
    </div>
  );
};

export default DashboardAdmin;
