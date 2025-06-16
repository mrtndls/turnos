/**
 * ✅ Usa el nuevo useAuthContext

✅ Redirige a /dashboard/reservar (primera ruta cliente)

✅ Agrega un estado loading para evitar múltiples envíos

✅ Mejora el diseño con clases Tailwind (podés ajustar si no usás Tailwind)
 */

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import { loginUser } from "../api/authService";
import useDocumentTitle from "../hooks/useDocumentTitle";

const Login: React.FC = () => {
  useDocumentTitle("Login - Sistema de Turnos");

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { login } = useAuthContext();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const response = await loginUser(email, password);
      login({
        email: response.email,
        token: response.token,
        rol: response.rol,
      });
      //navigate("/dashboard/menu"); // redirect del login
      if (response.rol === "ADMIN") {
        navigate("/dashboard/admin");
      } else {
        navigate("/dashboard/cliente/menu");
      }
    } catch {
      setError("Credenciales incorrectas o error en la conexión.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-sm mx-auto mt-20 p-6 border rounded shadow">
      <h2 className="text-xl font-bold mb-4 text-center">Iniciar sesión</h2>

      <form onSubmit={handleSubmit} noValidate className="space-y-4">
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />

        <input
          type="password"
          placeholder="Contraseña"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          className="w-full p-2 border rounded"
        />

        {error && <p className="text-red-600 text-sm">{error}</p>}

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
        >
          {loading ? "Ingresando..." : "Entrar"}
        </button>
      </form>
    </div>
  );
};

export default Login;
