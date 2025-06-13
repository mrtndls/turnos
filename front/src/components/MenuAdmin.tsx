// src/components/MenuAdmin.tsx
import React from "react";
import { useNavigate } from "react-router-dom";

export default function MenuAdmin() {
  const navigate = useNavigate();

  return (
    <div className="p-4 max-w-md mx-auto text-center">
      <h2 className="text-2xl font-bold mb-6">Menú Administrador</h2>
      <div className="space-y-4">
        <button
          onClick={() => navigate("/dashboard/admin/turnos")}
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
        >
          Ver todos los turnos
        </button>
      </div>
    </div>
  );
}
