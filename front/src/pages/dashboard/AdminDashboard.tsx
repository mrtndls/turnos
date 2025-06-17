// src/pages/DashboardAdmin.tsx
import React from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import useDocumentTitle from "../../hooks/useDocumentTitle";

const DashboardAdmin = () => {
  useDocumentTitle("Panel de Admin");

  const { user, logout } = useAuthContext();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const handleHome = () => {
    navigate("/dashboard/admin/menu");
  };

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <header className="flex justify-between items-center mb-6 border-b pb-4">
        <h1 className="text-2xl font-bold">
          Bienvenido, <span className="text-blue-600">{user?.email}</span>
        </h1>
        <div className="space-x-2">
          <button
            onClick={handleHome}
            className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
          >
            Inicio
          </button>
          <button
            onClick={handleLogout}
            className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 transition"
          >
            Cerrar sesión
          </button>
        </div>
      </header>

      {/* Sección donde se renderizan las subrutas como /dashboard/admin/menu */}
      <main>
        <Outlet />
      </main>
    </div>
  );
};

export default DashboardAdmin;
