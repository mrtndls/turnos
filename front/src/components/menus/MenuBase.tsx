import { useNavigate } from "react-router-dom";


// interfaz de opciones de menu
interface OpcionMenu  {
  label: string;
  path: string;
  colorClass: string;
}

interface MenuBaseProps {
  titulo: string;
  opciones: OpcionMenu[];
}

// componente base para mostrar opciones de menu
function MenuBase({ titulo, opciones }: MenuBaseProps) {
  const navigate = useNavigate();

  return (
    <div className="p-4 max-w-md mx-auto text-center">
      <h2 className="text-2xl font-bold mb-6">{titulo}</h2>
      <div className="space-y-4">
        {opciones.map(({ label, path, colorClass }) => (
          <button
            key={path}
            onClick={() => navigate(path)}
            className={`w-full ${colorClass} text-white py-2 rounded transition`}
          >
            {label}
          </button>
        ))}
      </div>
    </div>
  );
}

export default MenuBase;
