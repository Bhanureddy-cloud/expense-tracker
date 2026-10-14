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

    if (res.status === 401 && !path.startsWith("/api/auth")) {
        localStorage.clear();
        window.location.reload();
        return;
    }

    if (res.status === 204) return null;
    const data = await res.json().catch(() => null);

    if (!res.ok) {
        let message = data?.message;
        if (!message && data && typeof data === "object" && !data.status) {
            message = Object.values(data).join(", "); // validation errors: {field: message}
        }
        if (!message) {
            message =
                res.status === 409 ? "That email is already registered"
                    : res.status === 401 ? "Invalid email or password"
                        : `Request failed (${res.status})`;
        }
        throw new Error(message);
    }
    return data;
}