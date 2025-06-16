import axios from "axios";
import {
  ServicioResponseDTO,
  UbicacionResponseDTO,
  TurnoRequestDTO,
  TurnoResponseDTO,
} from "../types/turno";

export interface TurnoPreviewDTO {
  idServicio: number;
  idUbicacion: number;
  fecha: string;
  hora: string;
}

const API_BASE_CLIENTE = "http://localhost:8080/api/cliente";

const axiosInstanceCliente = axios.create({
  baseURL: API_BASE_CLIENTE,
  timeout: 5000,
});

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

// Métodos cliente

export const anularTurno = async (codigoAnulacion: string): Promise<string> => {
  const { data } = await axiosInstanceCliente.post("/anular", null, {
    params: { codigoAnulacion },
  });
  return data.mensaje;
};

export const confirmarDatosTurno = async (
  turno: TurnoPreviewDTO
): Promise<any> => {
  const { data } = await axiosInstanceCliente.post("/confirmar", turno);
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

export const fetchFechasHabilitadas = async (
  servicioId: number,
  year: number,
  month: number
): Promise<string[]> => {
  const { data } = await axiosInstanceCliente.get(`/servicios/${servicioId}/fechas-habilitadas`, {
    params: { year, month },
  });
  return data;
};

export const crearTurno = async (
  turno: TurnoRequestDTO
): Promise<TurnoResponseDTO> => {
  const { data } = await axiosInstanceCliente.post("", turno);
  return data;
};
