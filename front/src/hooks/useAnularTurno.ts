import { useState } from "react";
import { anularTurno } from "../api/clienteApi";

export default function useAnularTurno() {
  const [mensaje, setMensaje] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const anular = async (codigo: string) => {
    setMensaje("");
    setError("");
    setLoading(true);

    try {
      await anularTurno(codigo);
      setMensaje("Turno anulado con exito.");
    } catch (err: any) {
      setError(err.message || "Error al anular turno");
    } finally {
      setLoading(false);
    }
  };

  return { mensaje, error, loading, anular };
}
