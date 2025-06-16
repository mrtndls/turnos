// src/routes/DashboardAdminRoutes.tsx

import React, { useEffect, useState } from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import DashboardAdmin from "../pages/DashboardAdmin";
import MenuAdmin from "../components/MenuAdmin";
import TurnoAdminList from "../components/TurnoAdminList";
import ClienteAdminList from "../components/ClienteAdminList";

import { TurnoResponseDTO } from "../types/turno";
import { ClienteAdminDTO, ClientesAdminResponse } from "../types/cliente";

import {
  fetchAllTurnos,
  fetchClientes,
  darDeBajaCliente,
  darDeAltaCliente,
  editarCliente,
} from "../api/adminApi";

import Modal from "react-modal";

const DashboardAdminRoutes: React.FC = () => {
  // Estados para turnos y clientes
  const [turnos, setTurnos] = useState<TurnoResponseDTO[]>([]);
  const [loadingTurnos, setLoadingTurnos] = useState(true);

  const [clientes, setClientes] = useState<{
    activos: ClienteAdminDTO[];
    dadosDeBaja: ClienteAdminDTO[];
  }>({ activos: [], dadosDeBaja: [] });
  const [loadingClientes, setLoadingClientes] = useState(true);

  // Estado para cliente que está siendo editado (modal)
  const [clienteParaEditar, setClienteParaEditar] =
    useState<ClienteAdminDTO | null>(null);

  // Carga inicial de turnos y clientes
  useEffect(() => {
    fetchAllTurnos()
      .then(setTurnos)
      .catch((err) => console.error("Error al cargar turnos", err))
      .finally(() => setLoadingTurnos(false));

    fetchClientes()
      .then((res: ClientesAdminResponse) =>
        setClientes({
          activos: res.clientesActivos,
          dadosDeBaja: res.clientesBajaLogica,
        })
      )
      .catch((err) => console.error("Error al cargar clientes", err))
      .finally(() => setLoadingClientes(false));
  }, []);

  // Funciones para cambiar estado cliente
  const handleDarDeBaja = async (id: number) => {
    try {
      await darDeBajaCliente(id);
      const cliente = clientes.activos.find((c) => c.id === id);
      if (!cliente) return;
      setClientes((prev) => ({
        activos: prev.activos.filter((c) => c.id !== id),
        dadosDeBaja: [
          ...prev.dadosDeBaja,
          { ...cliente, clienteActivo: false },
        ],
      }));
    } catch (error) {
      console.error("Error al dar de baja", error);
      alert("No se pudo dar de baja el cliente");
    }
  };

  const handleDarDeAlta = async (id: number) => {
    try {
      await darDeAltaCliente(id);
      const cliente = clientes.dadosDeBaja.find((c) => c.id === id);
      if (!cliente) return;
      setClientes((prev) => ({
        activos: [...prev.activos, { ...cliente, clienteActivo: true }],
        dadosDeBaja: prev.dadosDeBaja.filter((c) => c.id !== id),
      }));
    } catch (error) {
      console.error("Error al dar de alta", error);
      alert("No se pudo dar de alta el cliente");
    }
  };

  // Editar cliente y actualizar estado
  const handleEditarCliente = async (clienteEditado: ClienteAdminDTO) => {
    try {
      const actualizado = await editarCliente(
        clienteEditado.id,
        clienteEditado
      );

      setClientes((prev) => ({
        activos: prev.activos.map((c) =>
          c.id === actualizado.id ? actualizado : c
        ),
        dadosDeBaja: prev.dadosDeBaja.map((c) =>
          c.id === actualizado.id ? actualizado : c
        ),
      }));

      setClienteParaEditar(null); // cerrar modal después de guardar
    } catch (error) {
      console.error("Error al editar cliente", error);
      alert("No se pudo editar el cliente");
    }
  };

  return (
    <>
      <Routes>
        <Route path="/" element={<DashboardAdmin />}>
          <Route index element={<Navigate to="menu" replace />} />
          <Route path="menu" element={<MenuAdmin />} />
          <Route
            path="turnos"
            element={
              loadingTurnos ? (
                <p>Cargando turnos...</p>
              ) : (
                <TurnoAdminList turnos={turnos} />
              )
            }
          />
          <Route
            path="clientes"
            element={
              loadingClientes ? (
                <p>Cargando clientes...</p>
              ) : (
                <ClienteAdminList
                  activos={clientes.activos}
                  dadosDeBaja={clientes.dadosDeBaja}
                  onDarDeBaja={handleDarDeBaja}
                  onDarDeAlta={handleDarDeAlta}
                  onEditar={(cliente) => setClienteParaEditar(cliente)} // Abre modal
                />
              )
            }
          />
          <Route path="*" element={<Navigate to="menu" replace />} />
        </Route>
      </Routes>

      {/* Modal para editar cliente */}
      {clienteParaEditar && (
        <Modal
          isOpen={true}
          onRequestClose={() => setClienteParaEditar(null)}
          contentLabel="Editar Cliente"
          ariaHideApp={false}
          style={{
            content: {
              maxWidth: "400px",
              margin: "auto",
              inset: "40px",
              padding: "20px",
            },
          }}
        >
          <h2>Editar Cliente</h2>
          <form
            onSubmit={(e) => {
              e.preventDefault();
              if (clienteParaEditar) {
                handleEditarCliente(clienteParaEditar);
              }
            }}
          >
            <label>
              Nombre:
              <input
                type="text"
                value={clienteParaEditar.nombre}
                onChange={(e) =>
                  setClienteParaEditar({
                    ...clienteParaEditar,
                    nombre: e.target.value,
                  })
                }
                required
              />
            </label>
            <br />
            <label>
              Apellido:
              <input
                type="text"
                value={clienteParaEditar.apellido}
                onChange={(e) =>
                  setClienteParaEditar({
                    ...clienteParaEditar,
                    apellido: e.target.value,
                  })
                }
                required
              />
            </label>
            <br />
            <label>
              Email:
              <input
                type="email"
                value={clienteParaEditar.email}
                onChange={(e) =>
                  setClienteParaEditar({
                    ...clienteParaEditar,
                    email: e.target.value,
                  })
                }
                required
              />
            </label>
            <br />
            {/* Podés agregar más campos según modelo */}
            <button type="submit" style={{ marginRight: "10px" }}>
              Guardar
            </button>
            <button type="button" onClick={() => setClienteParaEditar(null)}>
              Cancelar
            </button>
          </form>
        </Modal>
      )}
    </>
  );
};

export default DashboardAdminRoutes;
