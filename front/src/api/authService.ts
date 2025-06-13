import axios from "axios";
import { User } from "../types/User";

const API_BASE_URL = "http://localhost:8080/api/auth";

export const loginUser = async (email: string, password: string): Promise<User> => {
  try {
    const response = await axios.post(`${API_BASE_URL}/login`, { email, password });
    const { token, email: userEmail } = response.data;

    if (!token || !userEmail) {
      throw new Error("Respuesta inválida del servidor");
    }

    // Guardar token en localStorage para persistencia
    localStorage.setItem("token", token);
    localStorage.setItem("email", userEmail);

    return { email: userEmail, token };
  } catch (error) {
    // Puedes manejar o loguear el error aquí si querés
    throw new Error("Error en autenticación");
  }
};
