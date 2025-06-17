import React, { useState } from "react";
import ClienteAdminList from "../components/ClienteAdminList";
import { useClientes } from "../hooks/useClientes";
import { ClienteAdminDTO } from "../types/cliente";
import FormularioEdicionCliente from "../components/FormularioEdicionCliente";

const ClienteAdminListWrapper = () => {
  const {
    clientes,
    loadingClientes,
    handleDarDeBaja,
    handleDarDeAlta,
    handleEditarCliente,
  } = useClientes();

  const [clienteEditando, setClienteEditando] =
    useState<ClienteAdminDTO | null>(null);

  if (loadingClientes) return <p>Cargando clientes...</p>;

  const onEditar = (cliente: ClienteAdminDTO) => {
    setClienteEditando(cliente); // abrir formulario edición
  };

  const onGuardarEdicion = async (clienteEditado: ClienteAdminDTO) => {
    await handleEditarCliente(clienteEditado);
    setClienteEditando(null); // cerrar formulario
  };

  const onCancelarEdicion = () => {
    setClienteEditando(null);
  };

  return (
    <>
      <ClienteAdminList
        activos={clientes.activos}
        dadosDeBaja={clientes.dadosDeBaja}
        onDarDeBaja={handleDarDeBaja}
        onDarDeAlta={handleDarDeAlta}
        onEditar={onEditar}
      />

      {clienteEditando && (
        <FormularioEdicionCliente
          cliente={clienteEditando}
          onGuardar={onGuardarEdicion}
          onCancelar={onCancelarEdicion}
        />
      )}
    </>
  );
};

export default ClienteAdminListWrapper;
