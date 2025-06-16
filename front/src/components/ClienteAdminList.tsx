// src/components/ClienteAdminList.tsx
import React from "react";
import { ClienteAdminDTO } from "../types/cliente";
import useDocumentTitle from "../hooks/useDocumentTitle";

interface Props {
  activos: ClienteAdminDTO[];
  dadosDeBaja: ClienteAdminDTO[];
  onDarDeBaja: (id: number) => void;
  onDarDeAlta: (id: number) => void;
  onEditar: (cliente: ClienteAdminDTO) => void;
}

const ClienteAdminList: React.FC<Props> = ({
  activos,
  dadosDeBaja,
  onDarDeBaja,
  onDarDeAlta,
  onEditar,
}) => {
  useDocumentTitle("ClienteAdminList");

  const renderTabla = (clientes: ClienteAdminDTO[], esActivo: boolean) => (
    <table
      style={{
        width: "100%",
        borderCollapse: "collapse",
        minWidth: "700px",
        marginTop: "1rem",
      }}
    >
      <thead>
        <tr style={{ backgroundColor: "#f3f4f6", textAlign: "center" }}>
          <th style={headerCellStyle}>ID</th>
          <th style={headerCellStyle}>Email</th>
          <th style={headerCellStyle}>Baja Logica</th>
          <th style={headerCellStyle}>Turno Activo</th>
          <th style={headerCellStyle}>CreatedAt</th>
          <th style={headerCellStyle}>UpdatedAt</th>
          <th style={headerCellStyle}>Nombre</th>
          <th style={headerCellStyle}>Apellido</th>
          <th style={headerCellStyle}>Dni</th>
          <th style={headerCellStyle}>Acciones</th>
        </tr>
      </thead>
      <tbody>
        {clientes.map((cliente, index) => (
          <tr
            key={cliente.id}
            style={{ backgroundColor: index % 2 === 0 ? "#fff" : "#f9fafb" }}
          >
            <td style={bodyCellStyle}>{cliente.id}</td>
            <td style={bodyCellStyle}>{cliente.email}</td>
            <td style={bodyCellStyle}>{cliente.clienteActivo ? "No" : "Si"}</td>
            <td style={bodyCellStyle}>
              {cliente.tieneTurnosActivos ? "Sí" : "No"}
            </td>
            <td style={bodyCellStyle}>{formatFecha(cliente.createdAt)}</td>
            <td style={bodyCellStyle}>{formatFecha(cliente.updatedAt)}</td>
            <td style={bodyCellStyle}>{cliente.nombre}</td>
            <td style={bodyCellStyle}>{cliente.apellido}</td>
            <td style={bodyCellStyle}>{cliente.dni}</td>
            <td style={bodyCellStyle}>
              {esActivo ? (
                <>
                  <button
                    onClick={() => onDarDeBaja(cliente.id)}
                    className="bg-yellow-500 text-white px-2 py-1 rounded hover:bg-yellow-600 mb-1"
                  >
                    Dar de baja
                  </button>
                  <button
                    onClick={() => onEditar(cliente)}
                    className="bg-blue-600 text-white px-2 py-1 rounded hover:bg-blue-700 mb-1 ml-2"
                  >
                    Editar
                  </button>
                </>
              ) : (
                <button
                  onClick={() => onDarDeAlta(cliente.id)}
                  className="bg-green-600 text-white px-2 py-1 rounded hover:bg-green-700 mb-1"
                >
                  Dar de alta
                </button>
              )}
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );

  return (
    <div style={{ overflowX: "auto", marginTop: "1rem" }}>
      <h2 style={{ fontWeight: "bold", fontSize: "1.25rem" }}>
        Clientes activos
      </h2>
      {activos.length > 0 ? (
        renderTabla(activos, true)
      ) : (
        <p>No hay clientes activos.</p>
      )}

      <h2
        style={{ fontWeight: "bold", fontSize: "1.25rem", marginTop: "2rem" }}
      >
        Clientes dados de baja
      </h2>
      {dadosDeBaja.length > 0 ? (
        renderTabla(dadosDeBaja, false)
      ) : (
        <p>No hay clientes dados de baja.</p>
      )}
    </div>
  );
};

const formatFecha = (fecha?: string) => {
  if (!fecha) return "-";

  try {
    const date = new Date(fecha);
    return date.toLocaleString("es-AR", {
      timeZone: "America/Argentina/Buenos_Aires",
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false,
    });
  } catch {
    return fecha;
  }
};

const headerCellStyle: React.CSSProperties = {
  padding: "10px 15px",
  border: "1px solid #d1d5db",
  fontWeight: "bold",
};

const bodyCellStyle: React.CSSProperties = {
  padding: "10px 15px",
  border: "1px solid #e5e7eb",
  textAlign: "center",
};

export default ClienteAdminList;
