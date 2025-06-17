import React from "react";
import useDocumentTitle from "../hooks/useDocumentTitle";
import useHorariosDisponibles from "../hooks/useHorariosDisponibles";

interface Props {
  servicioId: number;
  fecha: string; // "yyyy-MM-dd"
  onSelectHora: (hora: string) => void;
}

function HorariosDisponiblesList({ servicioId, fecha, onSelectHora }: Props) {
  useDocumentTitle("HorariosDisponiblesList");

  const { horarios, loading, error } = useHorariosDisponibles(
    servicioId,
    fecha
  );

  const parseDateLocal = (dateString: string): Date => {
    const [year, month, day] = dateString.split("-").map(Number);
    return new Date(year, month - 1, day);
  };

  if (loading) return <div>Cargando horarios...</div>;
  if (error) return <div style={{ color: "red" }}>{error}</div>;
  if (horarios.length === 0) return <div>No hay horarios disponibles.</div>;

  return (
    <div>
      <h3>
        Horarios disponibles para {parseDateLocal(fecha).toLocaleDateString()}
      </h3>
      <ul>
        {horarios.map((hora) => (
          <li key={hora}>
            <button onClick={() => onSelectHora(hora)}>{hora}</button>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default HorariosDisponiblesList;
