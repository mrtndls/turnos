import React, { useState, useEffect } from "react";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import useDocumentTitle from "../hooks/useDocumentTitle";
import useFechasYHorarios from "../hooks/useFechasYHorarios";

interface CalendarioTurnosProps {
  servicioId: number;
  onSelectFecha: (fecha: string) => void;
}

function CalendarioTurnos({
  servicioId,
  onSelectFecha,
}: CalendarioTurnosProps) {
  useDocumentTitle("CalendarioTurnos");

  const [fechaSeleccionada, setFechaSeleccionada] = useState<Date | null>(null);

  const {
    horarios,
    cargarFechasHabilitadas,
    cargarHorarios,
    isDateEnabled,
    toLocalDateString,
  } = useFechasYHorarios(servicioId);

  // 'hoy' para cargar inicialmente
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);

  useEffect(() => {
    cargarFechasHabilitadas(hoy);
  }, [cargarFechasHabilitadas]);

  const handleMonthChange = (date: Date) => {
    cargarFechasHabilitadas(date);
  };

  const handleChange = (date: Date | null) => {
    if (!date) return;
    setFechaSeleccionada(date);

    const fechaISO = toLocalDateString(date);
    onSelectFecha(fechaISO);
    cargarHorarios(fechaISO);
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
        placeholderText="Elige un dia"
        inline
      />

      {horarios.length > 0 && (
        <div>
          <h3>Horarios disponibles:</h3>
          <ul>
            {horarios.map((h, i) => (
              <li key={i}>{h}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default CalendarioTurnos;
