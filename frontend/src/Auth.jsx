import { useState } from "react";
import { request } from "./api";

export default function Auth({ onLogin }) {
    const [mode, setMode] = useState("login");
    const [form, setForm] = useState({ name: "", email: "", password: "" });
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const submit = async (e) => {
        e.preventDefault();
        setError("");
        setLoading(true);
        try {
            const body =
                mode === "login" ? { email: form.email, password: form.password } : form;
            const data = await request(`/api/auth/${mode}`, {
                method: "POST",
                body: JSON.stringify(body),
            });
            localStorage.setItem("token", data.token);
            localStorage.setItem("name", data.name);
            onLogin(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="card">
            <h1>Expense Tracker</h1>
            <h2>{mode === "login" ? "Log in" : "Create account"}</h2>
            <form onSubmit={submit}>
                {mode === "signup" && (
                    <input name="name" placeholder="Name" value={form.name} onChange={change} required />
                )}
                <input name="email" type="email" placeholder="Email" value={form.email} onChange={change} required />
                <input name="password" type="password" placeholder="Password (min 8 characters)" value={form.password} onChange={change} required />
                {error && <p className="error">{error}</p>}
                <button disabled={loading}>
                    {loading ? "Please wait..." : mode === "login" ? "Log in" : "Sign up"}
                </button>
            </form>
            <p className="switch">
                {mode === "login" ? "New here? " : "Already have an account? "}
                <a href="#" onClick={(e) => { e.preventDefault(); setError(""); setMode(mode === "login" ? "signup" : "login"); }}>
                    {mode === "login" ? "Sign up" : "Log in"}
                </a>
            </p>
        </div>
    );
}