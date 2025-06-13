import React, { useEffect, useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import DashboardAdmin from "../pages/DashboardAdmin";
import MenuAdmin from "../components/MenuAdmin";
import TurnoAdminList from "../components/TurnoAdminList";
import { TurnoResponseDTO } from "../types/turno";
import { fetchAllTurnos } from "../api/turnoApi"; // 👈 usa tu helper

const DashboardAdminRoutes: React.FC = () => {
  const [turnos, setTurnos] = useState<TurnoResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAllTurnos()
      .then(setTurnos)
      .catch((err) => {
        console.error("Error al cargar turnos admin", err);
      })
      .finally(() => setLoading(false));
  }, []);

  return (
    <Routes>
      <Route path="/" element={<DashboardAdmin />}>
        <Route index element={<Navigate to="menu" replace />} />
        <Route path="menu" element={<MenuAdmin />} />
        <Route
          path="turnos"
          element={
            loading ? <p>Cargando...</p> : <TurnoAdminList turnos={turnos} />
          }
        />
        <Route path="*" element={<Navigate to="menu" replace />} />
      </Route>
    </Routes>
  );
};

export default DashboardAdminRoutes;
