import axios, { AxiosInstance } from "axios";
import { ClienteAdminDTO, ClientesAdminResponse } from "../types/cliente";
import { TurnoResponseDTO } from "../types/turno";

const API_BASE_ADMIN = "http://localhost:8080/api/admin";

// configuración de instancia de Axios
const axiosInstance: AxiosInstance = axios.create({
  baseURL: API_BASE_ADMIN,
  timeout: 5000,
});

// interceptor para token JWT
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token && config.headers) {
      config.headers.set?.("Authorization", `Bearer ${token}`);
    }
    return config;
  },
  (error) => Promise.reject(error)
);


// obtener clientes activos e inactivos
export const fetchClientes = async (): Promise<ClientesAdminResponse> => {
  const response = await axiosInstance.get("/clientes");
  return response.data;
};

// dar de baja lógica
export const darDeBajaCliente = async (id: number): Promise<void> => {
  await axiosInstance.put(`/${id}/baja`);
};

// dar de alta lógica
export const darDeAltaCliente = async (id: number): Promise<void> => {
  await axiosInstance.put(`/${id}/alta`);
};

// editar cliente
export const editarCliente = async (
  id: number,
  cliente: ClienteAdminDTO
): Promise<ClienteAdminDTO> => {
  const response = await axiosInstance.put(`/${id}`, cliente);
  return response.data;
};

// obtener todos los turnos
export const fetchAllTurnos = async (): Promise<TurnoResponseDTO[]> => {
  const response = await axiosInstance.get("/turnos");
  return response.data;
};
