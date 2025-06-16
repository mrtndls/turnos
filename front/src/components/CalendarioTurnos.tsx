// src/components/CalendarioTurnos.tsx
import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import useDocumentTitle from "../hooks/useDocumentTitle";

interface CalendarioTurnosProps {
  servicioId: number;
  onSelectFecha: (fecha: string) => void; // fecha en formato "yyyy-MM-dd"
}

const CalendarioTurnos: React.FC<CalendarioTurnosProps> = ({
  servicioId,
  onSelectFecha,
}) => {
  useDocumentTitle("CalendarioTurnos");

  const [fechaSeleccionada, setFechaSeleccionada] = useState<Date | null>(null);
  const [fechasHabilitadas, setFechasHabilitadas] = useState<string[]>([]);
  const [horarios, setHorarios] = useState<string[]>([]);

  // 'hoy' siempre a medianoche para comparaciones consistentes
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);

  // Convierte un Date a "yyyy-MM-dd" en zona local para evitar desfases por zona horaria
  const toLocalDateString = (date: Date): string => {
    const offset = date.getTimezoneOffset();
    const localDate = new Date(date.getTime() - offset * 60 * 1000);
    return localDate.toISOString().split("T")[0];
  };

  // Carga las fechas habilitadas para el mes y año del parámetro date
  const cargarFechasHabilitadas = async (date: Date) => {
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // Enero = 0 en JS, por eso sumamos 1

    const token = localStorage.getItem("token") || "";

    try {
      const response = await fetch(
        `http://localhost:8080/api/cliente/servicios/${servicioId}/fechas-habilitadas?year=${year}&month=${month}`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Esperamos un array de strings "yyyy-MM-dd"
      const data: string[] = await response.json();
      setFechasHabilitadas(data);
    } catch (error) {
      console.error("Error cargando fechas habilitadas:", error);
      setFechasHabilitadas([]);
    }
  };

  // Cada vez que cambia el servicio, o el mes visible, recargamos fechas habilitadas
  useEffect(() => {
    cargarFechasHabilitadas(hoy);
  }, [servicioId]);

  // Cuando cambia el mes visible en el calendario, recargamos las fechas habilitadas para ese mes
  const handleMonthChange = (date: Date) => {
    cargarFechasHabilitadas(date);
  };

  // Habilitamos sólo fechas que estén en fechasHabilitadas y no sean anteriores a hoy
  const isDateEnabled = (date: Date) => {
    const isoDate = toLocalDateString(date);
    const esHabilitada = fechasHabilitadas.includes(isoDate);
    const esHoyOPosterior = date.getTime() >= hoy.getTime();
    return esHabilitada && esHoyOPosterior;
  };

  // Al seleccionar una fecha, la seteamos y solicitamos horarios disponibles
  const handleChange = async (date: Date | null) => {
    if (!date) return;
    setFechaSeleccionada(date);

    const fechaISO = toLocalDateString(date);
    onSelectFecha(fechaISO);

    const token = localStorage.getItem("token") || "";

    try {
      const response = await fetch(
        `http://localhost:8080/api/cliente/servicio/${servicioId}/disponibilidad/${fechaISO}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // El backend devuelve un DTO: { fecha: "2025-06-16", activo: true/false, horarios: [ {horaInicio: "08:00", horaFin: "08:30"}, ... ] }
      const data = await response.json();

      if (data.activo) {
        // Mapeamos los horarios a strings legibles, ej: "08:00 - 08:30"
        const horariosFormateados = data.horarios.map(
          (h: { horaInicio: string; horaFin: string }) =>
            `${h.horaInicio} - ${h.horaFin}`
        );
        setHorarios(horariosFormateados);
      } else {
        alert("Este día no tiene disponibilidad.");
        setHorarios([]);
      }
    } catch (error) {
      console.error("Error al obtener disponibilidad:", error);
      setHorarios([]);
    }
  };

  return (
    <div>
      <h2>Selecciona una fecha</h2>
      <DatePicker
        selected={fechaSeleccionada}
        onChange={handleChange}
        minDate={hoy}
        filterDate={isDateEnabled}
        onMonthChange={handleMonthChange}
        dateFormat="yyyy-MM-dd"
        placeholderText="Elige un día"
        inline
      />

      {horarios.length > 0 && (
        <div>
          <h3>Horarios disponibles:</h3>
          <ul>
            {horarios.map((h, index) => (
              <li key={index}>{h}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default CalendarioTurnos;
