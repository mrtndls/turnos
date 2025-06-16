// src/components/UbicacionesList.tsx
import React, { useEffect, useState } from "react";
import { UbicacionResponseDTO } from "../types/turno";
import { fetchUbicaciones } from "../api/clienteApi";
import useDocumentTitle from "../hooks/useDocumentTitle";

interface Props {
  servicioId: number;
  onSelectUbicacion: (ubicacion: UbicacionResponseDTO) => void;
}

const UbicacionesList: React.FC<Props> = ({
  servicioId,
  onSelectUbicacion,
}) => {
  useDocumentTitle("UbicacionesList");

  const [ubicaciones, setUbicaciones] = useState<UbicacionResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadUbicaciones = async () => {
      try {
        const data = await fetchUbicaciones(servicioId);
        setUbicaciones(data);
      } catch (error) {
        console.error("Error cargando ubicaciones", error);
      } finally {
        setLoading(false);
      }
    };
    loadUbicaciones();
  }, [servicioId]);

  if (loading) return <div>Cargando ubicaciones...</div>;

  return (
    <div>
      <h3>Ubicaciones disponibles</h3>
      <ul>
        {ubicaciones.map((u) => (
          <li key={u.id}>
            <button onClick={() => onSelectUbicacion(u)}>{u.direccion}</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default UbicacionesList;
