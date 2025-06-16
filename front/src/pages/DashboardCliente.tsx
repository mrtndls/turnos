// src/pages/DashboardCliente.tsx
import React from "react";
import { Outlet, useNavigate } from "react-router-dom";
import { useAuth } from "../context/useAuth";

const DashboardCliente: React.FC = () => {
  const { email, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const handleHome = () => {
    navigate("/dashboard/cliente/menu");
  };

  return (
    <div className="p-4 max-w-4xl mx-auto">
      <header className="flex justify-between items-center mb-6">
        <h2 className="text-xl font-bold">Bienvenido, {email}</h2>
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 transition"
        >
          Cerrar sesión
        </button>
        <button
          onClick={handleHome}
          className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 transition"
        >
          Home
        </button>
      </header>

      {/* ✅ Acá se renderiza el contenido dinámico según la ruta */}
      <Outlet />
    </div>
  );
};

export default DashboardCliente;
