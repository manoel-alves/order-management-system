import { apiFetch } from "./http.js";

export function listCustomers() {
    return apiFetch("/api/customers");
}

export function searchCustomersByName(name) {
    const query = encodeURIComponent(name);
    return apiFetch(`/api/customers?name=${query}`);
}

export function getCustomerById(id) {
    return apiFetch(`/api/customers/${id}`);
}

export function createCustomer({ name, email }) {
    return apiFetch("/api/customers", {
        method: "POST",
        body: JSON.stringify({ name, email }),
    });
}