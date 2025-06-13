// src/types/turno.ts

// dto
// Esto es fundamental para que TypeScript controle tipos y evitar errores.

export interface ServicioResponseDTO {
  id: number;
  nombre: string;
  descripcion?: string;
}

export interface UbicacionResponseDTO {
  id: number;
  nombre: string;
  direccion?: string;
}

export interface TurnoRequestDTO {
  //idCliente: number;
  idServicio: number;
  idUbicacion: number;
  fecha: string;   // ISO string: "2025-06-12"
  hora: string;    // Ej: "09:00"
}

// types/turno.ts
export interface TurnoResponseDTO {
  id: number;
  codigoAnulacion: string;
  fecha: string;
  hora: string;
  nombreServicio: string;
  ubicacionDescripcion: string;
  estado: string;
}
