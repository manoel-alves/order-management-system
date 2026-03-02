import { useEffect, useState } from "react";
import { listCustomers } from "../../api/customers.js";
import { listProducts } from "../../api/products.js";

export function useOrderOptions() {
    const [customers, setCustomers] = useState([]);
    const [products, setProducts] = useState([]);
    const [loadingOptions, setLoadingOptions] = useState(false);
    const [optionsError, setOptionsError] = useState("");

    useEffect(() => {
        let alive = true;

        (async () => {
            setLoadingOptions(true);
            setOptionsError("");

            try {
                const [cs, ps] = await Promise.all([listCustomers(), listProducts()]);
                if (!alive) return;

                setCustomers(Array.isArray(cs) ? cs : []);
                setProducts(Array.isArray(ps) ? ps : []);
            } catch (e) {
                if (!alive) return;
                setOptionsError(e?.message ?? "Falha ao carregar clientes/produtos.");
            } finally {
                if (alive) setLoadingOptions(false);
            }
        })();

        return () => {
            alive = false;
        };
    }, []);

    return { customers, products, loadingOptions, optionsError };
}