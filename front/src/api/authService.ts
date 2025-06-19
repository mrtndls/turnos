// AuthService : en el back = logica de negocio y validacion del lado del sv
// son servicios para consumir el back

import axios from "axios";
import { User } from "../types/User";

// URL base para autenticacion
const API_BASE_URL = "http://localhost:8080/api/auth";

// separar almacenamiento local en funciones reutilizables
const saveUserToLocalStorage = (user: User) => {
  localStorage.setItem("token", user.token);
  localStorage.setItem("email", user.email);
  localStorage.setItem("rol", user.rol);
};

// limpiar almacenamiento local
const clearUserFromLocalStorage = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("email");
  localStorage.removeItem("rol");
};

// login con mejor manejo de errores
export const loginUser = async (email: string, password: string): Promise<User> => {
  try {
    const { data } = await axios.post(`${API_BASE_URL}/login`, { email, password });

    const { token, email: userEmail, rol } = data;

    if (!token || !userEmail || !rol) {
      throw new Error("Respuesta inválida del servidor");
    }

    const cleanedRol = rol.replace("ROLE_", "") as "ADMIN" | "USER";
    const user: User = { email: userEmail, token, rol: cleanedRol };

    saveUserToLocalStorage(user);

    return user;
  } catch (error: any) {
    console.error("Error en login:", error.response?.data || error.message);
    throw new Error("Credenciales incorrectas o error en el servidor.");
  }
};

// logout para el service
export const logoutUser = () => {
  clearUserFromLocalStorage();
};
