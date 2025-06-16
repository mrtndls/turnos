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
  idCliente?: number;         // opcional, si se usa en backend
  idServicio: number;
  idUbicacion: number;
  fecha: string;             // formato ISO "YYYY-MM-DD"
  hora: string;              // ejemplo "09:00"
  observaciones?: string;    // opcional
}


export interface TurnoResponseDTO {
  id: number;
  fecha: string;                 // Ej: "2025-06-13"
  hora: string;                  // Ej: "09:00"
  nombreServicio: string | null;
  ubicacionDescripcion: string;
  nombreEmpleado: string | null; // puede ser null o "A confirmar"
  nombreCliente: string | null;
  observaciones?: string;
  codigoAnulacion: string;
}
