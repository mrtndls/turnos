import { useEffect, useState } from "react";
import { UbicacionResponseDTO } from "../types/turno";
import { fetchUbicaciones } from "../api/clienteApi";
import useDocumentTitle from "../hooks/useDocumentTitle";

interface Props {
  servicioId: number;
  onSelectUbicacion: (ubicacion: UbicacionResponseDTO) => void;
}

const UbicacionesList = ({ servicioId, onSelectUbicacion }: Props) => {
  useDocumentTitle("UbicacionesList");

  const [ubicaciones, setUbicaciones] = useState<UbicacionResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await fetchUbicaciones(servicioId);
        setUbicaciones(data);
      } catch (err) {
        console.error("Error cargando ubicaciones", err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [servicioId]);

  if (loading) return <div>Cargando ubicaciones...</div>;

  return (
    <div className="p-4 text-center">
      <h3 className="text-xl font-semibold mb-4">Ubicaciones disponibles</h3>
      <ul className="space-y-2">
        {ubicaciones.map((u) => (
          <li key={u.id}>
            <button
              onClick={() => onSelectUbicacion(u)}
              className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
            >
              {u.direccion}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default UbicacionesList;
