import { useState } from "react";
import Auth from "./Auth";
import Dashboard from "./Dashboard";

export default function App() {
  const [name, setName] = useState(localStorage.getItem("name"));
  const loggedIn = !!localStorage.getItem("token");

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("name");
    setName(null);
  };

  if (!loggedIn || !name) {
    return <Auth onLogin={(data) => setName(data.name)} />;
  }
  return <Dashboard name={name} onLogout={logout} />;
}