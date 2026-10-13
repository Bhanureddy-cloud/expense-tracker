import { useEffect, useState, useCallback } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";
import { request } from "./api";

const CATEGORIES = ["Food", "Transport", "Rent", "Shopping", "Bills", "Health", "Other"];
const today = () => new Date().toLocaleDateString("en-CA"); // YYYY-MM-DD in local time
const emptyForm = () => ({ amount: "", category: "Food", expenseDate: today(), note: "" });

export default function Dashboard({ name, onLogout }) {
    const [month, setMonth] = useState(today().slice(0, 7));
    const [expenses, setExpenses] = useState([]);
    const [summary, setSummary] = useState([]);
    const [form, setForm] = useState(emptyForm());
    const [editingId, setEditingId] = useState(null);
    const [error, setError] = useState("");

    const load = useCallback(async () => {
        try {
            const [list, sum] = await Promise.all([
                request(`/api/expenses?month=${month}`),
                request(`/api/expenses/summary?month=${month}`),
            ]);
            setExpenses(list || []);
            setSummary((sum || []).map((s) => ({ category: s.category, total: Number(s.total) })));
            setError("");
        } catch (err) {
            setError(err.message);
        }
    }, [month]);

    useEffect(() => {
        load();
    }, [load]);

    const change = (e) => setForm({ ...form, [e.target.name]: e.target.value });

    const submit = async (e) => {
        e.preventDefault();
        try {
            const body = JSON.stringify({ ...form, amount: Number(form.amount) });
            if (editingId) {
                await request(`/api/expenses/${editingId}`, { method: "PUT", body });
            } else {
                await request("/api/expenses", { method: "POST", body });
            }
            setForm(emptyForm());
            setEditingId(null);
            await load();
        } catch (err) {
            setError(err.message);
        }
    };

    const startEdit = (ex) => {
        setEditingId(ex.id);
        setForm({
            amount: ex.amount,
            category: ex.category,
            expenseDate: ex.expenseDate,
            note: ex.note || "",
        });
    };

    const cancelEdit = () => {
        setEditingId(null);
        setForm(emptyForm());
    };

    const remove = async (id) => {
        if (!window.confirm("Delete this expense?")) return;
        try {
            await request(`/api/expenses/${id}`, { method: "DELETE" });
            await load();
        } catch (err) {
            setError(err.message);
        }
    };

    const total = expenses.reduce((sum, e) => sum + Number(e.amount), 0);

    return (
        <div className="card wide">
            <div className="topbar">
                <h1>Hi, {name}</h1>
                <button className="secondary" onClick={onLogout}>Log out</button>
            </div>

            <div className="row">
                <label>
                    Month{" "}
                    <input type="month" value={month} onChange={(e) => setMonth(e.target.value)} />
                </label>
                <strong>Total: ₹{total.toFixed(2)}</strong>
            </div>

            {error && <p className="error">{error}</p>}

            <h2>{editingId ? "Edit expense" : "Add expense"}</h2>
            <form className="expense-form" onSubmit={submit}>
                <input name="amount" type="number" step="0.01" min="0.01" placeholder="Amount"
                    value={form.amount} onChange={change} required />
                <select name="category" value={form.category} onChange={change}>
                    {CATEGORIES.map((c) => <option key={c}>{c}</option>)}
                </select>
                <input name="expenseDate" type="date" value={form.expenseDate} onChange={change} required />
                <input name="note" placeholder="Note (optional)" value={form.note} onChange={change} />
                <button>{editingId ? "Update" : "Add"}</button>
                {editingId && <button type="button" className="secondary" onClick={cancelEdit}>Cancel</button>}
            </form>

            <h2>Spending by category</h2>
            {summary.length === 0 ? (
                <p className="muted">No data for this month.</p>
            ) : (
                <div style={{ width: "100%", height: 240 }}>
                    <ResponsiveContainer>
                        <BarChart data={summary}>
                            <XAxis dataKey="category" />
                            <YAxis />
                            <Tooltip />
                            <Bar dataKey="total" fill="#2563eb" />
                        </BarChart>
                    </ResponsiveContainer>
                </div>
            )}

            <h2>Expenses</h2>
            {expenses.length === 0 ? (
                <p className="muted">No expenses this month.</p>
            ) : (
                <table>
                    <thead>
                        <tr><th>Date</th><th>Category</th><th>Note</th><th>Amount</th><th></th></tr>
                    </thead>
                    <tbody>
                        {expenses.map((ex) => (
                            <tr key={ex.id}>
                                <td>{ex.expenseDate}</td>
                                <td>{ex.category}</td>
                                <td>{ex.note}</td>
                                <td>₹{Number(ex.amount).toFixed(2)}</td>
                                <td className="actions">
                                    <button className="link" onClick={() => startEdit(ex)}>Edit</button>
                                    <button className="link danger" onClick={() => remove(ex.id)}>Delete</button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}