import React, { useEffect, useState } from "react";
import { TurnoResponseDTO } from "../types/turno";
import useDocumentTitle from "../hooks/useDocumentTitle";
import { fetchAllTurnos } from "../api/adminApi";
import TurnoAdminList from "../components/TurnoAdminList";

const TurnoAdminListWrapper = () => {
  useDocumentTitle("Turnos | Administración");

  const [turnos, setTurnos] = useState<TurnoResponseDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAllTurnos()
      .then(setTurnos)
      .catch((err) => console.error("Error al cargar turnos", err))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <p>Cargando turnos...</p>;

  return <TurnoAdminList turnos={turnos} />;
};

export default TurnoAdminListWrapper;
