// FormEvent y useState: para manejar formularios y estados locales
// useNavigate: hook de react router para redirect despues de login
// useAuthContext: accede al contexto de auth(usuario,login,logout)
// loginUser: funcion que hace el llamado al back para iniciar sesion
// useDoc: custom hook q cambia el titula de la pesta;a
import { FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthContext } from "../context/AuthContext";
import { loginUser } from "../api/AuthService";
import useDocumentTitle from "../hooks/useDocumentTitle";

// Login: componente funcionalF
const Login = () => {
  useDocumentTitle("Login - Sistema de Turnos");

  const [formData, setFormData] = useState({ email: "", password: "" }); // almacena los inputs del form
  const [error, setError] = useState(""); // msj de error si las credenciales son incorrectas
  const [loading, setLoading] = useState(false); // muestra "Ingresando.." mientras se hace la peticion

  const { login } = useAuthContext(); // funcion q guarda el usuario en el contexto(y locasStorage)
  const navigate = useNavigate(); // permite redirigir al usuario despues del login

  // funcion q actualiza los valores del form.por ej: cambia el email.. actualiza formDate.email
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // redirige al usuario a la ruta segun rol
  const redirectByRole = (rol: string) => {
    switch (rol) {
      case "ADMIN":
        navigate("/dashboard/admin");
        break;
      case "USER":
        navigate("/dashboard/cliente/menu");
        break;
      default:
        navigate("/unauthorized");
    }
  };

  // previene el comportamiento por defecto del form(e.preventDefault)
  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      // funcion q llama al back con email y pw
      const response = await loginUser(formData.email, formData.password);
      // si Response OK guarda datos en el conexteto login() y redirige
      login({
        email: response.email,
        token: response.token,
        rol: response.rol,
      });
      redirectByRole(response.rol);
    } catch (err: any) {
      setError("Credenciales incorrectas o error en la conexion.");
    } finally {
      setLoading(false);
    }
  };

  // jsx interfaz de usuario
  return (
    <div className="max-w-sm mx-auto mt-20 p-6 border rounded shadow">
      <h2 className="text-xl font-bold mb-4 text-center">Iniciar sesion</h2>

      <form onSubmit={handleSubmit} noValidate className="space-y-4">
        <input
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          required
          className="w-full p-2 border rounded"
        />

        <input
          type="password"
          name="password"
          placeholder="Contraseña"
          value={formData.password}
          onChange={handleChange}
          required
          className="w-full p-2 border rounded"
        />

        {/*error en rojo si algo sale mal*/}
        {error && <p className="text-red-600 text-sm">{error}</p>}

        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 transition"
        >
          {/*boton deshabilitado hasta que loading true*/}
          {loading ? "Ingresando..." : "Entrar"}
        </button>
      </form>
    </div>
  );
};

export default Login;
