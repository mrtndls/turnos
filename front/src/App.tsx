// src/App.tsx
import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import Login from "./pages/Login";
import PrivateRoute from "./routes/PrivateRoute";
import DashboardAdminRoutes from "./routes/DashboardAdminRoutes";
import DashboardClienteRoutes from "./routes/DashboardClienteRoutes";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          {/* Ruta para ADMIN */}
          <Route
            path="/dashboard/admin/*"
            element={
              <PrivateRoute allowedRoles={["ADMIN"]}>
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
