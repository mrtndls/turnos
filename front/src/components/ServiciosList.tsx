// src/components/ServiciosList.tsx
import React, { useEffect, useState } from "react";
import { ServicioResponseDTO } from "../types/turno";
import { fetchServicios } from "../api/turnoApi";

interface Props {
  onSelectServicio: (servicio: ServicioResponseDTO) => void;
}

const ServiciosList: React.FC<Props> = ({ onSelectServicio }) => {
  const [servicios, setServicios] = useState<ServicioResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadServicios = async () => {
      try {
        const data = await fetchServicios();
        setServicios(data);
      } catch (error) {
        console.error("Error cargando servicios", error);
      } finally {
        setLoading(false);
      }
    };
    loadServicios();
  }, []);

  if (loading) return <div>Cargando servicios...</div>;

  return (
    <div>
      <h2>Servicios disponibles</h2>
      <ul>
        {servicios.map((s) => (
          <li key={s.id}>
            <button onClick={() => onSelectServicio(s)}>{s.nombre}</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ServiciosList;
