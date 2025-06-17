import React, { useState } from "react";
import useDocumentTitle from "../hooks/useDocumentTitle";
import useAnularTurno from "../hooks/useAnularTurno";

export default function AnularTurno() {
  useDocumentTitle("Anular Turno");

  const [codigo, setCodigo] = useState("");
  const { mensaje, error, loading, anular } = useAnularTurno();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await anular(codigo);
    if (!error) setCodigo("");
  };

  return (
    <div>
      <h2>Anular turno</h2>
      <form onSubmit={handleSubmit}>
        <label htmlFor="codigo">Codigo de anulacion:</label>
        <input
          id="codigo"
          type="text"
          value={codigo}
          onChange={(e) => setCodigo(e.target.value)}
          required
          disabled={loading}
          className="border p-1 ml-2"
        />
        <button
          type="submit"
          disabled={loading}
          className="ml-2 px-3 py-1 bg-blue-600 text-white rounded"
        >
          {loading ? "Anulando..." : "Anular"}
        </button>
      </form>
      {mensaje && <p className="text-green-600 mt-2">{mensaje}</p>}
      {error && <p className="text-red-600 mt-2">{error}</p>}
    </div>
  );
}
