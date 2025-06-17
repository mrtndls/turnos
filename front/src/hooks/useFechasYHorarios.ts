import { useState, useEffect, useCallback } from "react";

interface Horario {
  horaInicio: string;
  horaFin: string;
}

interface DisponibilidadResponse {
  fecha: string;
  activo: boolean;
  horarios: Horario[];
}

function useFechasYHorarios(servicioId: number) {
  const [fechasHabilitadas, setFechasHabilitadas] = useState<string[]>([]);
  const [horarios, setHorarios] = useState<string[]>([]);

  // 'hoy' a medianoche
  const hoy = new Date();
  hoy.setHours(0, 0, 0, 0);

  const toLocalDateString = (date: Date): string => {
    const offset = date.getTimezoneOffset();
    const localDate = new Date(date.getTime() - offset * 60 * 1000);
    return localDate.toISOString().split("T")[0];
  };

  const cargarFechasHabilitadas = useCallback(
    async (date: Date) => {
      const year = date.getFullYear();
      const month = date.getMonth() + 1;

      const token = localStorage.getItem("token") || "";

      try {
        const response = await fetch(
          `http://localhost:8080/api/cliente/servicios/${servicioId}/fechas-habilitadas?year=${year}&month=${month}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const data: string[] = await response.json();
        setFechasHabilitadas(data);
      } catch (error) {
        console.error("Error cargando fechas habilitadas:", error);
        setFechasHabilitadas([]);
      }
    },
    [servicioId]
  );

  const cargarHorarios = useCallback(
    async (fechaISO: string) => {
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

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);

        const data: DisponibilidadResponse = await response.json();

        if (data.activo) {
          const horariosFormateados = data.horarios.map(
            (h) => `${h.horaInicio} - ${h.horaFin}`
          );
          setHorarios(horariosFormateados);
        } else {
          alert("Este dia no tiene disponibilidad.");
          setHorarios([]);
        }
      } catch (error) {
        console.error("Error al obtener disponibilidad:", error);
        setHorarios([]);
      }
    },
    [servicioId]
  );

  const isDateEnabled = (date: Date) => {
    const isoDate = toLocalDateString(date);
    return fechasHabilitadas.includes(isoDate) && date.getTime() >= hoy.getTime();
  };

  return {
    fechasHabilitadas,
    horarios,
    cargarFechasHabilitadas,
    cargarHorarios,
    isDateEnabled,
    toLocalDateString,
  };
}

export default useFechasYHorarios;
