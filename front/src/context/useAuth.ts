// Así accedes a user.email y user.token desde el objeto user.

import { useAuthContext } from "./AuthContext";

export const useAuth = () => {
  const { user, login, logout } = useAuthContext();

  return {
    isAuthenticated: Boolean(user?.token),
    email: user?.email || null,
    token: user?.token || null,
    login,
    logout,
  };
};
