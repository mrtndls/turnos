// src/components/ReservaTurno.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ServicioResponseDTO, UbicacionResponseDTO } from "../types/turno";
import ServiciosList from "./ServiciosList";
import UbicacionesList from "./UbicacionesList";
import CalendarioTurnos from "./CalendarioTurnos"; // calendario nuevo
import HorariosDisponiblesList from "./HorariosDisponiblesList";
import ConfirmarTurno from "./ConfirmarTurno";
import useDocumentTitle from "../hooks/useDocumentTitle";

const ReservaTurno: React.FC = () => {
  useDocumentTitle("ReservaTurno");

  const [servicio, setServicio] = useState<ServicioResponseDTO | null>(null);
  const [ubicacion, setUbicacion] = useState<UbicacionResponseDTO | null>(null);
  const [fecha, setFecha] = useState<string | null>(null);
  const [hora, setHora] = useState<string | null>(null);
  const navigate = useNavigate();

  return (
    <>
      {!servicio ? (
        <ServiciosList onSelectServicio={setServicio} />
      ) : !ubicacion ? (
        <UbicacionesList
          servicioId={servicio.id}
          onSelectUbicacion={setUbicacion}
        />
      ) : !fecha ? (
        <CalendarioTurnos servicioId={servicio.id} onSelectFecha={setFecha} />
      ) : !hora ? (
        <HorariosDisponiblesList
          servicioId={servicio.id}
          fecha={fecha}
          onSelectHora={setHora}
        />
      ) : (
        <ConfirmarTurno
          servicio={servicio}
          ubicacion={ubicacion}
          fecha={fecha}
          hora={hora}
          onVolver={() => setHora(null)}
          onConfirmar={() => {
            setServicio(null);
            setUbicacion(null);
            setFecha(null);
            setHora(null);
            navigate("/dashboard/cliente/menu"); // 🔁 Redirige al menú
          }}
        />
      )}
    </>
  );
};

export default ReservaTurno;
