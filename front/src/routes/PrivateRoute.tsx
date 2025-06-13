// src/routes/PrivateRoute.tsx
import React from "react";
import { Navigate } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";

type PrivateRouteProps = {
  children: React.ReactNode;
  allowedRoles: ("ADMIN" | "USER")[]; // Asegúrate de que los roles sean de tipo "ADMIN" o "CLIENTE"
};

const PrivateRoute: React.FC<PrivateRouteProps> = ({
  children,
  allowedRoles,
}) => {
  const { user, loading } = useAuthContext();

  console.log("PrivateRoute - user:", user);
  console.log("PrivateRoute - loading:", loading);

  if (loading) {
    return <div className="text-center mt-10">Cargando sesión...</div>; // o spinner
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // Verificamos si el rol del usuario está en la lista de roles permitidos
  if (!allowedRoles.includes(user.rol)) {
    console.log("PrivateRoute - user:", user)
    return <Navigate to="/unauthorized" replace />; // Redirigir a página de no autorizado
  }

  return <>{children}</>;
};

export default PrivateRoute;
