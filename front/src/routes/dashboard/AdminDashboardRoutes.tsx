import { Routes, Route, Navigate } from "react-router-dom";

import DashboardAdmin from "../../pages/dashboard/AdminDashboard";
import MenuAdmin from "../../components/menus/MenuAdmin";
import TurnoAdminListWrapper from "../../wrappers/TurnoAdminListWrapper";
import ClienteAdminListWrapper from "../../wrappers/ClienteAdminListWrapper";

const DashboardAdminRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<DashboardAdmin />}>
        <Route index element={<Navigate to="menu" replace />} />
        <Route path="menu" element={<MenuAdmin />} />
        <Route path="turnos" element={<TurnoAdminListWrapper />} />
        <Route path="clientes" element={<ClienteAdminListWrapper />} />
        <Route path="*" element={<Navigate to="menu" replace />} />
      </Route>
    </Routes>
  );
};

export default DashboardAdminRoutes;
