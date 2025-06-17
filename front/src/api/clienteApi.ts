import axios, { AxiosInstance, AxiosResponse } from "axios";
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

const axiosInstanceCliente: AxiosInstance = axios.create({
  baseURL: API_BASE_CLIENTE,
  timeout: 5000,
});

// Interceptor para agregar token a headers
axiosInstanceCliente.interceptors.request.use(
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

// Interceptor para manejar 401 globalmente
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

// para extraer data y manejar errores
async function extractData<T>(promise: Promise<AxiosResponse<T>>): Promise<T> {
  try {
    const response = await promise;
    return response.data;
  } catch (error) {
    throw error;
  }
}

// metodos cliente
export const anularTurno = (codigoAnulacion: string): Promise<string> =>
  extractData(
    axiosInstanceCliente.post("/anular", null, { params: { codigoAnulacion } })
  ).then((data) => data.mensaje);

export const confirmarDatosTurno = (
  turno: TurnoPreviewDTO
): Promise<TurnoResponseDTO> =>
  extractData(axiosInstanceCliente.post("/confirmar", turno));

export const fetchMisTurnos = (): Promise<TurnoResponseDTO[]> =>
  extractData(axiosInstanceCliente.get("/mis-turnos"));

export const fetchServicios = (): Promise<ServicioResponseDTO[]> =>
  extractData(axiosInstanceCliente.get("/servicios"));

export const fetchUbicaciones = (servicioId: number): Promise<UbicacionResponseDTO[]> =>
  extractData(axiosInstanceCliente.get(`/servicios/${servicioId}/ubicaciones`));

export const fetchDiasDisponibles = (servicioId: number): Promise<string[]> =>
  extractData(axiosInstanceCliente.get(`/servicios/${servicioId}/dias-disponibles`));

export const fetchHorariosDisponibles = (
  servicioId: number,
  fecha: string
): Promise<string[]> =>
  extractData(
    axiosInstanceCliente.get(`/servicios/${servicioId}/fechas/${fecha}/horarios`)
  );

export const fetchFechasHabilitadas = (
  servicioId: number,
  year: number,
  month: number
): Promise<string[]> =>
  extractData(
    axiosInstanceCliente.get(`/servicios/${servicioId}/fechas-habilitadas`, {
      params: { year, month },
    })
  );

export const crearTurno = (turno: TurnoRequestDTO): Promise<TurnoResponseDTO> =>
  extractData(axiosInstanceCliente.post("", turno));
