// src/components/DiasDisponiblesList.tsx
import React, { useEffect, useState } from "react";
import { fetchDiasDisponibles } from "../api/clienteApi";
import useDocumentTitle from "../hooks/useDocumentTitle";

interface Props {
  servicioId: number;
  onSelectFecha: (fecha: string) => void;
}

const DiasDisponiblesList: React.FC<Props> = ({
  servicioId,
  onSelectFecha,
}) => {
  useDocumentTitle("DiasDisponiblesList");

  const [dias, setDias] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDias = async () => {
      try {
        const data = await fetchDiasDisponibles(servicioId);
        setDias(data);
      } catch (error) {
        console.error("Error cargando días disponibles", error);
      } finally {
        setLoading(false);
      }
    };
    loadDias();
  }, [servicioId]);

  if (loading) return <div>Cargando días disponibles...</div>;

  return (
    <div>
      <h3>Fechas disponibles</h3>
      <ul>
        {dias.map((dia, index) => (
          <li key={index}>
            <button onClick={() => onSelectFecha(dia)}>
              {new Date(dia).toLocaleDateString()}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default DiasDisponiblesList;
