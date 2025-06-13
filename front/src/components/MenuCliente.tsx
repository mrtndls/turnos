// src/components/MenuCliente.tsx
import React from "react";
import { useNavigate } from "react-router-dom";

export default function MenuCliente() {
  const navigate = useNavigate();

  return (
    <div className="p-4 max-w-md mx-auto text-center">
      <h2 className="text-2xl font-bold mb-6">Seleccione una opción</h2>
      <div className="space-y-4">
        <button
          onClick={() => navigate("/dashboard/cliente/reservar")}
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
        >
          Reservar turno
        </button>
        <button
          onClick={() => navigate("/dashboard/cliente/mis-turnos")}
          className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 transition"
        >
          Mis turnos
        </button>
        <button
          onClick={() => navigate("/dashboard/cliente/anular")}
          className="w-full bg-yellow-600 text-white py-2 rounded hover:bg-yellow-700 transition"
        >
          Anular turno
        </button>
      </div>
    </div>
  );
}
