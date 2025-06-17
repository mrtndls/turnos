import { useState, useEffect } from "react";
import { fetchDiasDisponibles } from "../api/clienteApi";

export default function useDiasDisponibles(servicioId: number) {
  const [dias, setDias] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);

    const loadDias = async () => {
      try {
        const data = await fetchDiasDisponibles(servicioId);
        setDias(data);
      } catch (err: any) {
        setError(err.message || "Error cargando dias disponibles");
      } finally {
        setLoading(false);
      }
    };

    if (servicioId) {
      loadDias();
    } else {
      setDias([]);
      setLoading(false);
    }
  }, [servicioId]);

  return { dias, loading, error };
}
