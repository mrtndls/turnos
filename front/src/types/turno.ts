// src/types/turno.ts

// ---------------------
// DTOs para Servicios
// ---------------------

export interface ServicioResponseDTO {
  id: number;
  nombre: string;
  descripcion?: string;
}

// ---------------------
// DTOs para Ubicaciones
// ---------------------

export interface UbicacionResponseDTO {
  id: number;
  nombre: string;
  direccion?: string;
}

// ---------------------
// DTO para Crear Turno
// ---------------------

export interface TurnoRequestDTO {
  idServicio: number;
  idUbicacion: number;
  fecha: string; // ISO format: "2025-06-12"
  hora: string;  // Ej: "09:00"
}

// ---------------------
// DTO para Confirmar Turno (Preview)
// ---------------------

export interface TurnoPreviewDTO {
  idServicio: number;
  idUbicacion: number;
  fecha: string;
  hora: string;
}

// ---------------------
// DTO para Respuesta de Turno
// ---------------------

export interface TurnoResponseDTO {
  id: number;
  fecha: string;                // Ej: "2025-06-13"
  hora: string;                 // Ej: "09:00"
  nombreCliente: string | null; // Opcionalmente null
  nombreEmpleado: string | null;
  nombreServicio: string | null;
  ubicacionDescripcion: string;
  observaciones: string;
  codigoAnulacion: string;
}