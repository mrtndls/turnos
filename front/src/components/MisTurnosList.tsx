// components/MisTurnosList.tsx
import React, { useEffect, useState } from "react";
import { fetchMisTurnos } from "../api/turnoApi";
import { TurnoResponseDTO } from "../types/turno";

export default function MisTurnosList() {
  const [turnos, setTurnos] = useState<TurnoResponseDTO[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchMisTurnos()
      .then(setTurnos)
      .catch((err) => setError(err.message));
  }, []);

  if (error) return <div className="text-red-500">Error: {error}</div>;

  return (
    <div className="p-4">
      <h2 className="text-xl font-bold mb-2">Mis turnos reservados</h2>
      {turnos.length === 0 ? (
        <p>No tenés turnos reservados.</p>
      ) : (
        <ul className="space-y-4">
          {turnos.map((turno) => (
            <li key={turno.id} className="border p-4 rounded shadow">
              <p>
                <strong>Fecha:</strong> {turno.fecha}
              </p>
              <p>
                <strong>Hora:</strong> {turno.hora}
              </p>
              <p>
                <strong>Servicio:</strong>{" "}
                {turno.nombreServicio || "Sin servicio"}
              </p>
              <p>
                <strong>Ubicación:</strong>{" "}
                {turno.ubicacionDescripcion || "Sin ubicación"}
              </p>
              <p>
                <strong>Estado:</strong> {turno.estado}
              </p>
              {turno.codigoAnulacion && (
                <p>
                  <strong>Código Anulación:</strong> {turno.codigoAnulacion}
                </p>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
