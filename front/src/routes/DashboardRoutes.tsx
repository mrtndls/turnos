// src/routes/DashBoardRoutes.tsx
import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import DashboardCliente from "../pages/DashboardCliente";
import ReservaTurno from "../components/ReservaTurno";
import MisTurnosList from "../components/MisTurnosList";
import AnularTurno from "../components/AnularTurno";
import MenuDashboard from "../components/MenuDashboard";

const DashBoardRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<DashboardCliente />}>
        <Route index element={<Navigate to="menu" replace />} />
        <Route path="menu" element={<MenuDashboard />} />
        <Route path="reservar" element={<ReservaTurno />} />
        <Route path="mis-turnos" element={<MisTurnosList />} />
        <Route path="anular" element={<AnularTurno />} />
        <Route path="*" element={<Navigate to="menu" replace />} />
      </Route>
    </Routes>
  );
};

export default DashBoardRoutes;
