import React from "react";
import { crearTurno } from "../api/turnoApi";
import { ServicioResponseDTO, UbicacionResponseDTO } from "../types/turno";

interface Props {
  servicio: ServicioResponseDTO;
  ubicacion: UbicacionResponseDTO;
  fecha: string;
  hora: string;
  onVolver: () => void;
  onConfirmar: () => void;
}

const ConfirmarTurno: React.FC<Props> = ({
  servicio,
  ubicacion,
  fecha,
  hora,
  onVolver,
  onConfirmar,
}) => {
  const handleConfirmar = async () => {
    try {
      const turno = await crearTurno({
        idServicio: servicio.id,
        idUbicacion: ubicacion.id,
        fecha,
        hora,
      });

      alert(`Turno reservado con código: ${turno.codigoAnulacion}`);
      onConfirmar(); // 🔁 Resetear flujo en DashboardCliente
    } catch (error) {
      console.error("Error al reservar turno", error);
      alert("No se pudo reservar el turno");
    }
  };

  return (
    <div>
      <h3>Confirmar turno</h3>
      <p>Servicio: {servicio.nombre}</p>
      <p>Ubicación: {ubicacion.direccion}</p>
      <p>Fecha: {new Date(fecha).toLocaleDateString()}</p>
      <p>Hora: {hora}</p>
      <button onClick={handleConfirmar}>Confirmar</button>
      <button onClick={onVolver}>Volver</button>
    </div>
  );
};

export default ConfirmarTurno;
