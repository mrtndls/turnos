// src/components/HorariosDisponiblesList.tsx
import React, { useEffect, useState } from "react";
import { fetchHorariosDisponibles } from "../api/clienteApi";
import useDocumentTitle from "../hooks/useDocumentTitle";

interface Props {
  servicioId: number;
  fecha: string; // "yyyy-MM-dd"
  onSelectHora: (hora: string) => void;
}

const HorariosDisponiblesList: React.FC<Props> = ({
  servicioId,
  fecha,
  onSelectHora,
}) => {
  useDocumentTitle("HorariosDisponiblesList");

  const [horarios, setHorarios] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  // Convierte "yyyy-MM-dd" a Date en zona local
  const parseDateLocal = (dateString: string): Date => {
    const [year, month, day] = dateString.split("-").map(Number);
    return new Date(year, month - 1, day);
  };

  useEffect(() => {
    const loadHorarios = async () => {
      try {
        const data = await fetchHorariosDisponibles(servicioId, fecha);
        setHorarios(data);
      } catch (error) {
        console.error("Error cargando horarios disponibles", error);
      } finally {
        setLoading(false);
      }
    };
    loadHorarios();
  }, [servicioId, fecha]);

  if (loading) return <div>Cargando horarios...</div>;

  return (
    <div>
      <h3>
        Horarios disponibles para {parseDateLocal(fecha).toLocaleDateString()}
      </h3>
      <ul>
        {horarios.map((hora, index) => (
          <li key={index}>
            <button onClick={() => onSelectHora(hora)}>{hora}</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default HorariosDisponiblesList;
