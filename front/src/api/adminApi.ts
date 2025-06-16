import axios from "axios";
import { ClienteAdminDTO, ClientesAdminResponse } from "../types/cliente";
import { TurnoResponseDTO } from "../types/turno";

const API_BASE = "http://localhost:8080/api/admin";

const axiosInstance = axios.create({
  baseURL: API_BASE,
  timeout: 5000,
});

axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers = config.headers || {};
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// obtener clientes activos y de baja logica
export async function fetchClientes(): Promise<ClientesAdminResponse> {
  const { data } = await axiosInstance.get("/clientes");
  return data;
}

// dar de baja (soft delete)
export const darDeBajaCliente = async (id: number): Promise<void> => {
  await axiosInstance.put(`/${id}/baja`);
};

// dar de alta (reactivar)
export const darDeAltaCliente = async (id: number): Promise<void> => {
  await axiosInstance.put(`/${id}/alta`);
};

// editar cliente
export const editarCliente = async (id: number, cliente: ClienteAdminDTO): Promise<ClienteAdminDTO> => {
  const { data } = await axiosInstance.put(`/${id}`, cliente);
  return data;
};

// obtener todos los turnos
export const fetchAllTurnos = async (): Promise<TurnoResponseDTO[]> => {
  const { data } = await axiosInstance.get("/turnos");
  return data;
};
