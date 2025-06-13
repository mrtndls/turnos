// Aquí vamos a crear el AuthContext, el AuthProvider y un hook useAuth para acceder fácilmente al contexto.

//Con esto centralizamos el manejo de sesión. Podés envolver tu app con AuthProvider y acceder al usuario o cerrar sesión desde cualquier componente con useAuth().

// Se agregó el estado loading para evitar redirecciones prematuras.

import React, { createContext, useContext, useEffect, useState } from "react";
import { User } from "../types/User";

type AuthContextType = {
  user: User | null;
  login: (user: User) => void;
  logout: () => void;
  loading: boolean;
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    const storedEmail = localStorage.getItem("email");
    const storedRol = localStorage.getItem("rol");

    if (storedToken && storedEmail && storedRol) {
      const cleanedRol = storedRol.replace("ROLE_", "") as "ADMIN" | "USER";

      setUser({
        email: storedEmail,
        token: storedToken,
        rol: cleanedRol,
      });

      console.log("AuthProvider useEffect - restored user", {
        token: storedToken,
        email: storedEmail,
        rol: cleanedRol,
      });
    }

    setLoading(false);
  }, []);

  const login = (user: User) => {
    const cleanedRol = user.rol.replace("ROLE_", "");

    localStorage.setItem("token", user.token);
    localStorage.setItem("email", user.email);
    localStorage.setItem("rol", cleanedRol); // guardá "USER" o "ADMIN"

    setUser({
      ...user,
      rol: cleanedRol as "ADMIN" | "USER",
    });
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    localStorage.removeItem("rol");
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuthContext = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuthContext debe usarse dentro de AuthProvider");
  }
  return context;
};
