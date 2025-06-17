import { useEffect, useState } from "react";
import { ServicioResponseDTO } from "../types/turno";
import { fetchServicios } from "../api/clienteApi";
import useDocumentTitle from "../hooks/useDocumentTitle";

interface Props {
  onSelectServicio: (servicio: ServicioResponseDTO) => void;
}

const ServiciosList = ({ onSelectServicio }: Props) => {
  useDocumentTitle("ServiciosList");

  const [servicios, setServicios] = useState<ServicioResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await fetchServicios();
        setServicios(data);
      } catch (err) {
        console.error("Error cargando servicios", err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <div>Cargando servicios...</div>;

  return (
    <div className="p-4 text-center">
      <h2 className="text-xl font-semibold mb-4">Servicios disponibles</h2>
      <ul className="space-y-2">
        {servicios.map((s) => (
          <li key={s.id}>
            <button
              onClick={() => onSelectServicio(s)}
              className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
            >
              {s.nombre}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ServiciosList;
