import axios from "axios";
import {
  ServicioResponseDTO,
  UbicacionResponseDTO,
  TurnoRequestDTO,
  TurnoResponseDTO,
} from "../types/turno";

const API_BASE_CLIENTE = "http://localhost:8080/api/cliente/turnos";
const API_BASE_ADMIN = "http://localhost:8080/api/admin/turnos";

const axiosInstanceCliente = axios.create({
  baseURL: API_BASE_CLIENTE,
  timeout: 5000,
});

const axiosInstanceAdmin = axios.create({
  baseURL: API_BASE_ADMIN,
  timeout: 5000,
});

// Interceptores para cliente
axiosInstanceCliente.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = config.headers || {};
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

axiosInstanceCliente.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// Interceptores para admin (igual que cliente)
axiosInstanceAdmin.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers = config.headers || {};
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
}, (error) => Promise.reject(error));

axiosInstanceAdmin.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// Métodos para cliente (sin cambios)
export const anularTurno = async (codigoAnulacion: string): Promise<string> => {
  const { data } = await axiosInstanceCliente.post("/anular", null, {
    params: { codigoAnulacion },
  });
  return data.mensaje;
};

export const confirmarDatosTurno = async (turno: TurnoRequestDTO): Promise<any> => {
  const { data } = await axiosInstanceCliente.post("/confirmar", turno);
  return data;
};

export const fetchAllTurnosCliente = async (): Promise<TurnoResponseDTO[]> => {
  const { data } = await axiosInstanceCliente.get("/");
  return data;
};

export const fetchMisTurnos = async (): Promise<TurnoResponseDTO[]> => {
  const { data } = await axiosInstanceCliente.get("/mis-turnos");
  return data;
};

export const fetchServicios = async (): Promise<ServicioResponseDTO[]> => {
  const { data } = await axiosInstanceCliente.get("/servicios");
  return data;
};

export const fetchUbicaciones = async (
  servicioId: number
): Promise<UbicacionResponseDTO[]> => {
  const { data } = await axiosInstanceCliente.get(`/servicios/${servicioId}/ubicaciones`);
  return data;
};

export const fetchDiasDisponibles = async (
  servicioId: number
): Promise<string[]> => {
  const { data } = await axiosInstanceCliente.get(`/servicios/${servicioId}/dias-disponibles`);
  return data;
};

export const fetchHorariosDisponibles = async (
  servicioId: number,
  fecha: string
): Promise<string[]> => {
  const { data } = await axiosInstanceCliente.get(`/servicios/${servicioId}/fechas/${fecha}/horarios`);
  return data;
};

export const crearTurno = async (
  turno: TurnoRequestDTO
): Promise<TurnoResponseDTO> => {
  const { data } = await axiosInstanceCliente.post("", turno);
  return data;
};

// Métodos para admin
export const fetchAllTurnos = async (): Promise<TurnoResponseDTO[]> => {
  const { data } = await axiosInstanceAdmin.get("/");
  return data;
};
