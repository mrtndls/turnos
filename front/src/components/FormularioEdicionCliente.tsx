import React, { useState } from "react";
import { ClienteAdminDTO } from "../types/cliente";

interface Props {
  cliente: ClienteAdminDTO;
  onGuardar: (clienteEditado: ClienteAdminDTO) => void;
  onCancelar: () => void;
}

const FormularioEdicionCliente: React.FC<Props> = ({
  cliente,
  onGuardar,
  onCancelar,
}) => {
  const [formData, setFormData] = useState<ClienteAdminDTO>({ ...cliente });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onGuardar(formData);
  };

  return (
    <div className="modal">
      <form
        onSubmit={handleSubmit}
        className="p-4 bg-white rounded shadow-md max-w-md mx-auto"
      >
        <h2 className="text-xl mb-4">Editar Cliente</h2>

        <label>Email:</label>
        <input name="email" value={formData.email} onChange={handleChange} />

        <label>Nombre:</label>
        <input name="nombre" value={formData.nombre} onChange={handleChange} />

        <label>Apellido:</label>
        <input
          name="apellido"
          value={formData.apellido}
          onChange={handleChange}
        />

        <label>DNI:</label>
        <input name="dni" value={formData.dni} onChange={handleChange} />

        {/* mas atributos */}

        <div className="mt-4 flex justify-end space-x-2">
          <button
            type="button"
            onClick={onCancelar}
            className="bg-gray-400 px-4 py-2 rounded"
          >
            Cancelar
          </button>
          <button
            type="submit"
            className="bg-blue-600 text-white px-4 py-2 rounded"
          >
            Guardar
          </button>
        </div>
      </form>
    </div>
  );
};

export default FormularioEdicionCliente;
