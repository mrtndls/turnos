import { useState } from "react";
import { crearTurno as apiCrearTurno } from "../api/clienteApi";

interface CrearTurnoParams {
  idServicio: number;
  idUbicacion: number;
  fecha: string;
  hora: string;
}

export default function useCrearTurno() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const crearTurno = async (params: CrearTurnoParams) => {
    setLoading(true);
    setError(null);

    try {
      const turno = await apiCrearTurno(params);
      setLoading(false);
      return turno;
    } catch (err: any) {
      setLoading(false);
      setError(err.message || "Error al crear turno");
      throw err;
    }
  };

  return { crearTurno, loading, error };
}
