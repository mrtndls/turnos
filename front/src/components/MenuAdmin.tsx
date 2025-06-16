// src/components/MenuAdmin.tsx
import React from "react";
import { useNavigate } from "react-router-dom";
import useDocumentTitle from "../hooks/useDocumentTitle";

export default function MenuAdmin() {
  useDocumentTitle("MenuAdmin");

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
        <button
          onClick={() => navigate("/dashboard/admin/clientes")}
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
        >
          Ver todos los clientes
        </button>
      </div>
    </div>
  );
}
