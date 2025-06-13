// src/components/HorariosDisponiblesList.tsx
import React, { useEffect, useState } from "react";
import { fetchHorariosDisponibles } from "../api/turnoApi";

interface Props {
  servicioId: number;
  fecha: string;
  onSelectHora: (hora: string) => void;
}

const HorariosDisponiblesList: React.FC<Props> = ({ servicioId, fecha, onSelectHora }) => {
  const [horarios, setHorarios] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

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
      <h3>Horarios disponibles para {new Date(fecha).toLocaleDateString()}</h3>
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