// src/App.tsx
// define la estructura principal de rutasde la app con React Router y un context de Autenticacion
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./pages/Login";
import PrivateRoute from "./routes/PrivateRoute";
import DashboardAdminRoutes from "./routes/dashboard/AdminDashboardRoutes";
import DashboardClienteRoutes from "./routes/dashboard/ClienteDashboardRoutes";

function App() {
  return (
    /*AuthProvider: permite acceder al estado de autenticacion desde cualquier parte del front, user token rol */
    <AuthProvider>
      {/*BrowserRouter: habilita el sistema de rutas con react-router en una SPA */}
      <BrowserRouter>
        {/*Routes: agrupa todas las rutas*/}
        <Routes>
          {/*Route: define ruta especifica. ruta publica para login*/}
          <Route path="/login" element={<Login />} />
          {/* Ruta para ADMIN */}
          <Route
            path="/dashboard/admin/*"
            element={
              /*PrivateRoute: componente pers q protege rutas segun userlogeado y su rol*/
              /*Ruta protegida solos para rol admin*/
              <PrivateRoute allowedRoles={["ADMIN"]}>
                {/*DashboardAdminRoutes: conjunto de rutas internas especificas por rol usuario*/}
                <DashboardAdminRoutes />
              </PrivateRoute>
            }
          />

          {/* Ruta para CLIENTE */}
          <Route
            path="/dashboard/cliente/*"
            element={
              <PrivateRoute allowedRoles={["USER"]}>
                <DashboardClienteRoutes />
              </PrivateRoute>
            }
          />

          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
