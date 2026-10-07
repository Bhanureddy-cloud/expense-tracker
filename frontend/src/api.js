const API = import.meta.env.VITE_API_URL || "http://localhost:8080";

export async function request(path, options = {}) {
    const token = localStorage.getItem("token");
    const res = await fetch(`${API}${path}`, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
    });

    if (res.status === 204) return null;
    const data = await res.json().catch(() => null);

    if (!res.ok) {
        const message =
            data?.message ||
            (data && typeof data === "object" ? Object.values(data).join(", ") : null) ||
            "Request failed";
        throw new Error(message);
    }
    return data;
}