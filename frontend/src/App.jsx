import { useState } from "react";
import Auth from "./Auth";

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

  return (
    <div className="card">
      <h1>Welcome, {name}</h1>
      <p>Expenses will appear here in the next step.</p>
      <button onClick={logout}>Log out</button>
    </div>
  );
}