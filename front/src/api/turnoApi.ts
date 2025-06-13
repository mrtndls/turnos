// src/api/turnoApi.ts
import axios from "axios";
import {
  ServicioResponseDTO,
  UbicacionResponseDTO,
  TurnoRequestDTO,
  TurnoResponseDTO,
} from "../types/turno";

const API_BASE = "http://localhost:8080/api/turnos";

const axiosInstance = axios.create({
  baseURL: API_BASE,
});

// Interceptor para agregar el token en cada petición
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

// Interceptor para manejar errores 401 globalmente
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

// Métodos exportados
export const anularTurno = async (codigoAnulacion: string): Promise<string> => {
  const { data } = await axiosInstance.post("/anular", null, {
    params: { codigoAnulacion },
  });
  return data.mensaje; // el backend devuelve { "mensaje": "Turno anulado con éxito" }
};


export const fetchMisTurnos = async (): Promise<TurnoResponseDTO[]> => {
  const { data } = await axiosInstance.get("/mis-turnos");
  return data;
};

export const fetchServicios = async (): Promise<ServicioResponseDTO[]> => {
  const { data } = await axiosInstance.get("/servicios");
  return data;
};

export const fetchUbicaciones = async (
  servicioId: number
): Promise<UbicacionResponseDTO[]> => {
  const { data } = await axiosInstance.get(`/servicios/${servicioId}/ubicaciones`);
  return data;
};

export const fetchDiasDisponibles = async (
  servicioId: number
): Promise<string[]> => {
  const { data } = await axiosInstance.get(`/servicios/${servicioId}/dias-disponibles`);
  return data;
};

export const fetchHorariosDisponibles = async (
  servicioId: number,
  fecha: string
): Promise<string[]> => {
  const { data } = await axiosInstance.get(`/servicios/${servicioId}/fechas/${fecha}/horarios`);
  return data;
};

export const crearTurno = async (
  turno: TurnoRequestDTO
): Promise<TurnoResponseDTO> => {
  const { data } = await axiosInstance.post("", turno);
  return data;
};
