import { useCallback, useEffect, useRef, useState } from "react";
import {
    createCustomer,
    getCustomerById,
    listCustomers,
    searchCustomersByName,
} from "../../api/customers.js";

export function useCustomers() {
    const [items, setItems] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState(null);

    const [loadingList, setLoadingList] = useState(false);
    const [loadingCreate, setLoadingCreate] = useState(false);

    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");

    const requestIdRef = useRef(0);

    const clearMessages = useCallback(() => {
        setError("");
        setSuccess("");
    }, []);

    const refreshAll = useCallback(async () => {
        const requestId = ++requestIdRef.current;

        setLoadingList(true);

        try {
            const data = await listCustomers();
            if (requestId !== requestIdRef.current) {
                return;
            }
            setItems(Array.isArray(data) ? data : []);
        } catch (e) {
            if (requestId !== requestIdRef.current) {
                return;
            }
            setItems([]);
            setError(e?.message ?? "Falha ao listar clientes.");
        } finally {
            if (requestId === requestIdRef.current) {
                setLoadingList(false);
            }
        }
    }, [clearMessages]);

    const searchByName = useCallback(async (name) => {
        const requestId = ++requestIdRef.current;

        clearMessages();

        const trimmed = (name ?? "").trim();
        if (trimmed.length === 0) {
            setItems([]);
            setSelectedCustomer(null);
            return;
        }

        setLoadingList(true);

        try {
            const data = await searchCustomersByName(trimmed);

            if (requestId !== requestIdRef.current) {
                return;
            }

            setItems(Array.isArray(data) ? data : []);
            setSelectedCustomer(null);
        } catch (e) {
            if (requestId !== requestIdRef.current) {
                return;
            }

            setItems([]);
            setSelectedCustomer(null);
            setError(e?.message ?? "Falha ao buscar clientes.");
        } finally {
            if (requestId === requestIdRef.current) {
                setLoadingList(false);
            }
        }
    }, [clearMessages]);

    const createOne = useCallback(async ({ name, email }) => {
        clearMessages();
        setLoadingCreate(true);

        try {
            const created = await createCustomer({ name, email });
            setSuccess("Cliente criado com sucesso.");
            setSelectedCustomer(created ?? null);
            await refreshAll();
        } catch (e) {
            setError(e?.message ?? "Falha ao criar cliente.");
        } finally {
            setLoadingCreate(false);
        }
    }, [clearMessages, refreshAll]);

    const selectById = useCallback(async (id) => {
        const requestId = ++requestIdRef.current;

        clearMessages();

        try {
            const data = await getCustomerById(id);

            if (data) setItems([data]);
            else setItems([]);
            setSelectedCustomer(data ?? null);

            if (requestId !== requestIdRef.current) {
                return;
            }
            setSelectedCustomer(data ?? null);
        } catch (e) {
            if (requestId !== requestIdRef.current) {
                return;
            }
            setItems([]);
            setSelectedCustomer(null);
            setError(e?.message ?? "Falha ao carregar detalhes do cliente.");
        }
    }, [clearMessages]);

    const clearDetails = useCallback(() => {
        setSelectedCustomer(null);
    }, []);

    useEffect(() => {
        void refreshAll();
    }, [refreshAll]);

    return {
        items,
        selectedCustomer,
        loadingList,
        loadingCreate,
        error,
        success,
        refreshAll,
        searchByName,
        createOne,
        selectById,
        clearDetails,
    };
}