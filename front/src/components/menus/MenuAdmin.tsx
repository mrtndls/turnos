import React from "react";
import MenuBase from "./MenuBase";
import useDocumentTitle from "../../hooks/useDocumentTitle";

function MenuAdmin() {
  useDocumentTitle("MenuAdmin");

  const options = [
    {
      label: "Ver todos los turnos",
      path: "/dashboard/admin/turnos",
      colorClass: "bg-blue-600 hover:bg-blue-700",
    },
    {
      label: "Ver todos los clientes",
      path: "/dashboard/admin/clientes",
      colorClass: "bg-green-600 hover:bg-green-700",
    },
  ];

  return <MenuBase title="Menu Administrador" options={options} />;
}

export default MenuAdmin;
