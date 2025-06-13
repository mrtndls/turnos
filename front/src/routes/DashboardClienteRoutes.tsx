// src/routes/DashboardClienteRoutes.tsx
import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import DashboardCliente from "../pages/DashboardCliente";
import ReservaTurno from "../components/ReservaTurno";
import MisTurnosList from "../components/MisTurnosList";
import AnularTurno from "../components/AnularTurno";
import MenuCliente from "../components/MenuCliente";

const DashboardClienteRoutes: React.FC = () => {
  return (
    <Routes>
      <Route path="/" element={<DashboardCliente />}>
        <Route index element={<Navigate to="menu" replace />} />
        <Route path="menu" element={<MenuCliente />} />
        <Route path="reservar" element={<ReservaTurno />} />
        <Route path="mis-turnos" element={<MisTurnosList />} />
        <Route path="anular" element={<AnularTurno />} />
        <Route path="*" element={<Navigate to="menu" replace />} />
      </Route>
    </Routes>
  );
};

export default DashboardClienteRoutes;
