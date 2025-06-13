import axios from "axios";
import { User } from "../types/User";

const API_BASE_URL = "http://localhost:8080/api/auth";

export const loginUser = async (email: string, password: string): Promise<User> => {
  try {
    const response = await axios.post(`${API_BASE_URL}/login`, { email, password });
    const { token, email: userEmail, rol } = response.data;

    if (!token || !userEmail || !rol) {
      throw new Error("Respuesta inválida del servidor");
    }

    // Guardar datos en localStorage para persistencia
    localStorage.setItem("token", token);
    localStorage.setItem("email", userEmail);
    localStorage.setItem("rol", rol);

    return { email: userEmail, token, rol };
  } catch (error) {
    throw new Error("Error en autenticación");
  }
};
