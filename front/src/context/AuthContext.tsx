// AuthContext : memoria del navegador con logica para saber quien sos y que queres hacer
// contexto de autenticacion, permite manejar al usuario logeado desde cualquier parte del front
// son contextos globales

// createContext: crear un contexto de React
// useContext: se usa para consumir el contexto
// useEffect: ejcuta la logica cuando el componente se monta
// useState: para manejar "user" y "loading"
// User: tipo de TS q representa los datos del usuario
import React, {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from "react";
import { User } from "../types/User";
import { logoutUser } from "../api/AuthService";

// tipo de contexto
type AuthContextType = {
  user: User | null; // usuario actual o null
  login: (user: User) => void; // funcion que guarda el usuario en "estado" y "localStorage"
  logout: () => void; // borra sesion
  loading: boolean; // evita mostrar la app antes de verificar si hay sesion guardada
};

// creacion del contexto, inicia sin valor. luego sera provisto por "AuthProvider"
const AuthContext = createContext<AuthContextType | undefined>(undefined);

type Props = {
  children: ReactNode;
};

// componente que envuelve la app
export const AuthProvider = ({ children }: Props) => {
  // carga inicial
  const [user, setUser] = useState<User | null>(null); // guarda usuario en memoria
  const [loading, setLoading] = useState(true); // evita render mientras se busca sesion

  // useEffect: restaurar sesion al cargar la app
  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    const storedEmail = localStorage.getItem("email");
    const storedRol = localStorage.getItem("rol");

    // verifica si hay una sesion guardada
    if (storedToken && storedEmail && storedRol) {
      // si hay , restaura el usuario, limpia ROLE_ADMIN a ADMIN
      const cleanedRol = storedRol.replace("ROLE_", "") as "ADMIN" | "USER";

      setUser({
        email: storedEmail,
        token: storedToken,
        rol: cleanedRol,
      });
    }

    // loading pasa a false
    setLoading(false);
  }, []);

  // inicia sesion y persistir
  const login = (user: User) => {
    const cleanedRol = user.rol.replace("ROLE_", "");

    // guarda los datos del usuario en "localStorage"
    localStorage.setItem("token", user.token);
    localStorage.setItem("email", user.email);
    localStorage.setItem("rol", cleanedRol); // guardá "USER" o "ADMIN"

    // setUser: actaliza en memoria
    setUser({
      ...user,
      rol: cleanedRol as "ADMIN" | "USER",
    });
  };

  // cerrar sesion, elimina sesion tanto del ¨estado(memoria)¨ como del ¨localStorage¨
  const logout = () => {
    logoutUser(); // limpia localStorage desde el servicio
    setUser(null); // limpia el estado
  };

  return (
    // Provider + Hook personalizado
    // esto expone el context a toda la app
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

// useAuthContext(hook perso): puede usarse en cualquier componente para obtener "user","loghin","logout","loading"
export const useAuthContext = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuthContext debe usarse dentro de AuthProvider");
  }
  return context;
};
