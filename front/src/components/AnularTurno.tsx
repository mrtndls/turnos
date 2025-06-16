import React, { useState } from "react";
import { anularTurno } from "../api/clienteApi";
import useDocumentTitle from "../hooks/useDocumentTitle";

export default function AnularTurno() {
  useDocumentTitle("AnularTurno");

  const [codigo, setCodigo] = useState("");
  const [mensaje, setMensaje] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje("");
    setError("");

    try {
      await anularTurno(codigo);
      setMensaje("Turno anulado con éxito.");
      setCodigo("");
    } catch (err: any) {
      setError(err.message || "Error al anular turno");
    }
  };

  return (
    <div>
      <h2>Anular turno</h2>
      <form onSubmit={handleSubmit}>
        <label>
          Código de anulación:
          <input
            type="text"
            value={codigo}
            onChange={(e) => setCodigo(e.target.value)}
            required
          />
        </label>
        <button type="submit">Anular</button>
      </form>
      {mensaje && <p className="text-green-600">{mensaje}</p>}
      {error && <p className="text-red-600">{error}</p>}
    </div>
  );
}
