import React from "react";
import { ServicioResponseDTO, UbicacionResponseDTO } from "../types/turno";
import useDocumentTitle from "../hooks/useDocumentTitle";
import useCrearTurno from "../hooks/useCrearTurno";

interface Props {
  servicio: ServicioResponseDTO;
  ubicacion: UbicacionResponseDTO;
  fecha: string;
  hora: string;
  onVolver: () => void;
  onConfirmar: () => void;
}

function ConfirmarTurno({
  servicio,
  ubicacion,
  fecha,
  hora,
  onVolver,
  onConfirmar,
}: Props) {
  useDocumentTitle("ConfirmarTurno");
  const { crearTurno, loading, error } = useCrearTurno();

  const handleConfirmar = async () => {
    try {
      const turno = await crearTurno({
        idServicio: servicio.id,
        idUbicacion: ubicacion.id,
        fecha,
        hora,
      });
      alert(`Turno reservado con codigo: ${turno.codigoAnulacion}`);
      onConfirmar();
    } catch {
      alert(error || "No se pudo reservar el turno");
    }
  };

  return (
    <div>
      <h3>Confirmar turno</h3>
      <p>Servicio: {servicio.nombre}</p>
      <p>Ubicacion: {ubicacion.direccion}</p>
      <p>Fecha: {fecha.split("-").reverse().join("/")}</p>
      <p>Hora: {hora}</p>
      <button onClick={handleConfirmar} disabled={loading}>
        {loading ? "Confirmando..." : "Confirmar"}
      </button>
      <button onClick={onVolver} disabled={loading}>
        Volver
      </button>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}

export default ConfirmarTurno;
