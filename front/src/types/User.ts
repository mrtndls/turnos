// Creamos una interfaz para el usuario autenticado que usaremos luego en el contexto de autenticación.
// Esto sirve para tipar bien nuestro contexto de autenticación y el objeto que retorna el login.

export interface User {
  email: string;
  token: string;
}
