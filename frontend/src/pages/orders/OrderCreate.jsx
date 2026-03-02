import { useState } from "react";

function toInt(value) {
    const n = Number(value);
    return Number.isInteger(n) ? n : null;
}

function toDecimal(value) {
    const n = Number(String(value ?? "").replace(",", "."));
    return Number.isNaN(n) ? null : n;
}

export default function OrderCreate({ loadingCreate, onCreate, customers, products, loadingOptions }) {
    const [customerId, setCustomerId] = useState("");
    const [items, setItems] = useState([{ productId: "", quantity: "1", discount: "0.00" }]);

    const addItem = () => setItems((p) => [...p, { productId: "", quantity: "1", discount: "0.00" }]);
    const removeItem = (index) => setItems((p) => p.filter((_, i) => i !== index));

    const updateItem = (index, patch) => {
        setItems((p) => p.map((it, i) => (i === index ? { ...it, ...patch } : it)));
    };

    const submit = async (e) => {
        e.preventDefault();

        const cid = toInt(customerId);
        if (!cid) return;

        const mapped = items
            .map((it) => ({
                productId: toInt(it.productId),
                quantity: toInt(it.quantity),
                discount: toDecimal(it.discount) ?? 0,
            }))
            .filter((it) => it.productId && it.quantity);

        if (mapped.length === 0) return;

        const ok = await onCreate({ customerId: cid, items: mapped });

        if (ok) {
            setCustomerId("");
            setItems([{ productId: "", quantity: "1", discount: "0.00" }]);
        }
    };

    return (
        <div className="card w-100 ui-card">
            <div className="card-body d-flex flex-column gap-3">
                <h5 className="card-title">Criar pedido</h5>

                <form onSubmit={submit} className="d-flex flex-column gap-3">
                    <div>
                        <label className="form-label">Cliente</label>
                        <select
                            className="form-select"
                            value={customerId}
                            onChange={(e) => setCustomerId(e.target.value)}
                            required
                            disabled={loadingOptions}
                        >
                            <option value="">{loadingOptions ? "Carregando..." : "Selecione um cliente"}</option>
                            {customers.map((c) => (
                                <option key={c.id} value={c.id}>
                                    {c.name} (#{c.id})
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="d-flex justify-content-between align-items-center">
                        <strong>Itens</strong>
                        <button type="button" className="btn btn-sm btn-outline-secondary" onClick={addItem}>
                            Adicionar item
                        </button>
                    </div>

                    <div className="d-flex flex-column gap-2">
                        {items.map((it, idx) => (
                            <div key={idx} className="border rounded p-2">
                                <div className="row g-2 align-items-end">
                                    <div className="col-md-5">
                                        <label className="form-label mb-1">Produto</label>
                                        <select
                                            className="form-select"
                                            value={it.productId}
                                            onChange={(e) => updateItem(idx, { productId: e.target.value })}
                                            required
                                            disabled={loadingOptions}
                                        >
                                            <option value="">{loadingOptions ? "Carregando..." : "Selecione um produto"}</option>
                                            {products.map((p) => (
                                                <option key={p.id} value={p.id}>
                                                    {p.description} (#{p.id})
                                                </option>
                                            ))}
                                        </select>
                                    </div>

                                    <div className="col-md-3">
                                        <label className="form-label mb-1">Quantidade</label>
                                        <input
                                            className="form-control"
                                            type="number"
                                            min="1"
                                            value={it.quantity}
                                            onChange={(e) => updateItem(idx, { quantity: e.target.value })}
                                            required
                                        />
                                    </div>

                                    <div className="col-md-3">
                                        <label className="form-label mb-1">Desconto</label>
                                        <input
                                            className="form-control"
                                            value={it.discount}
                                            onChange={(e) => updateItem(idx, { discount: e.target.value })}
                                            placeholder="0.00"
                                        />
                                    </div>

                                    <div className="col-md-1 d-grid">
                                        <button
                                            type="button"
                                            className="btn btn-outline-danger"
                                            onClick={() => removeItem(idx)}
                                            disabled={items.length === 1}
                                            title="Remover item"
                                        >
                                            X
                                        </button>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>

                    <button className="btn btn-primary" type="submit" disabled={loadingCreate || loadingOptions}>
                        {loadingCreate ? "Salvando..." : "Criar pedido"}
                    </button>
                </form>
            </div>
        </div>
    );
}