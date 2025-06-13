// Este componente se asegura de que solo los usuarios autenticados puedan ver rutas protegidas, redirigiendo al login si no lo están.

// src/routes/PrivateRoute.tsx
import React from "react";
import { Navigate } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";

type PrivateRouteProps = {
  children: React.ReactNode;
};

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { user, loading } = useAuthContext();

  if (loading) {
    return <div className="text-center mt-10">Cargando sesión...</div>; // o spinner
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

export default PrivateRoute;
