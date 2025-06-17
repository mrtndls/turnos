// src/routes/PrivateRoute.tsx
// protege las rutas de la app segun su auth y rol
import { Navigate } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import { ReactNode } from "react";

type PrivateRouteProps = {
  children: ReactNode; // children: representa cualquier cosa que react pueda renderizar. componente o grupo de comp q se quiere renderizar si se permite el acceso
  allowedRoles: ("ADMIN" | "USER")[]; // array de roles q pueden acceer a las rutas
};

const PrivateRoute = ({ children, allowedRoles }: PrivateRouteProps) => {
  // useAuthContext para acceder a user y loading(boolean q indica si todavia se esta cargando la sess,token)
  const { user, loading } = useAuthContext();

  // mientras loading true muestra un mensaje, evita renderizar cosas mientras no se sabe si user ya logeo
  if (loading) {
    return <div className="text-center mt-10">Cargando sesion...</div>; // o spinner
  }

  // si no hay usuarui redirect al login, replace: evita q la ruta anterior quede en el historial del nav
  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // verifica si rol de user esta en lista de permitidos ALLOWEDROLES, sino redirect a /unauth
  if (!allowedRoles.includes(user.rol)) {
    return <Navigate to="/unauthorized" replace />;
  }

  // autenticado con permisos renderiza lo que esta dentro de PrivateRoutes en App.tsx (algun dashboard)
  return <>{children}</>;
};

export default PrivateRoute;
