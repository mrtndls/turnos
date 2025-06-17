import { useState, useEffect } from "react";
import { fetchHorariosDisponibles } from "../api/clienteApi";

export default function useHorariosDisponibles(servicioId: number, fecha: string) {
  const [horarios, setHorarios] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!servicioId || !fecha) {
      setHorarios([]);
      setLoading(false);
      return;
    }

    setLoading(true);
    setError(null);

    const loadHorarios = async () => {
      try {
        const data = await fetchHorariosDisponibles(servicioId, fecha);
        setHorarios(data);
      } catch (err: any) {
        setError(err.message || "Error cargando horarios disponibles");
      } finally {
        setLoading(false);
      }
    };

    loadHorarios();
  }, [servicioId, fecha]);

  return { horarios, loading, error };
}
