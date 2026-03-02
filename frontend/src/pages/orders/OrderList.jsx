import { Fragment, useMemo, useState } from "react";

function formatDateTime(value) {
    if (!value) return "-";
    const d = new Date(value);
    return Number.isNaN(d.getTime()) ? "-" : d.toLocaleString("pt-BR");
}

function formatPrice(value) {
    const n = Number(String(value ?? 0).replace(",", "."));
    if (Number.isNaN(n)) return "R$ 0,00";
    return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(n);
}

export default function OrderList({ items, loading, customers = [], products = [] }) {
    const [expandedId, setExpandedId] = useState(null);

    const customerNameById = useMemo(() => {
        const m = new Map();
        customers.forEach((c) => m.set(Number(c.id), c.name));
        return m;
    }, [customers]);

    const productNameById = useMemo(() => {
        const m = new Map();
        products.forEach((p) => m.set(Number(p.id), p.description));
        return m;
    }, [products]);

    return (
        <div className="card w-100 ui-card">
            <div className="card-body">
                <div className="d-flex flex-wrap gap-2 align-items-center justify-content-between">
                    <div>
                        <h5 className="card-title mb-0">Lista de pedidos</h5>
                        <div className="small text-muted">Clique em um pedido para ver os itens.</div>
                    </div>
                    <div className="small text-muted">{items?.length ? <>{items.length} resultado(s)</> : ""}</div>
                </div>

                <hr className="my-3" />

                {loading ? (
                    <p className="text-muted mb-0">Carregando...</p>
                ) : !items || items.length === 0 ? (
                    <p className="text-muted mb-0">Nenhum pedido encontrado.</p>
                ) : (
                    <div className="list-scroll">
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead className="table-light position-sticky top-0">
                                <tr>
                                    <th style={{ width: 90 }}>ID</th>
                                    <th>Cliente</th>
                                    <th style={{ width: 190 }}>Data</th>
                                    <th style={{ width: 160 }} className="text-end">
                                        Total
                                    </th>
                                    <th style={{ width: 140 }} className="text-end">
                                        Itens
                                    </th>
                                </tr>
                                </thead>

                                <tbody>
                                {items.map((o) => {
                                    const isOpen = expandedId === o.id;
                                    const itemCount = (o.items ?? []).length;

                                    return (
                                        <Fragment key={o.id}>
                                            <tr
                                                className="ui-clickable-row"
                                                onClick={() => setExpandedId((prev) => (prev === o.id ? null : o.id))}
                                            >
                                                <td>
                                                    <strong>#{o.id}</strong>
                                                </td>
                                                <td>{customerNameById.get(Number(o.customerId)) ?? `#${o.customerId}`}</td>
                                                <td>{formatDateTime(o.orderDate)}</td>
                                                <td className="text-end fw-semibold">{formatPrice(o.totalAmount)}</td>
                                                <td className="text-end">
                                                    <span className="badge text-bg-secondary">{itemCount}</span>
                                                </td>
                                            </tr>

                                            {isOpen && (
                                                <tr className="table-active">
                                                    <td colSpan={5}>
                                                        <div className="p-2">
                                                            <div className="small text-muted mb-2">Itens do pedido</div>
                                                            <div className="table-responsive">
                                                                <table className="table table-sm mb-0">
                                                                    <thead>
                                                                    <tr>
                                                                        <th>Produto</th>
                                                                        <th style={{ width: 140 }} className="text-end">
                                                                            Unit.
                                                                        </th>
                                                                        <th style={{ width: 90 }} className="text-end">
                                                                            Qtd
                                                                        </th>
                                                                        <th style={{ width: 140 }} className="text-end">
                                                                            Desc.
                                                                        </th>
                                                                        <th style={{ width: 160 }} className="text-end">
                                                                            Total
                                                                        </th>
                                                                    </tr>
                                                                    </thead>
                                                                    <tbody>
                                                                    {(o.items ?? []).map((it, idx) => (
                                                                        <tr key={idx}>
                                                                            <td>{productNameById.get(Number(it.productId)) ?? `#${it.productId}`}</td>
                                                                            <td className="text-end">{formatPrice(it.unitPrice)}</td>
                                                                            <td className="text-end">{it.quantity}</td>
                                                                            <td className="text-end">{formatPrice(it.discount)}</td>
                                                                            <td className="text-end fw-semibold">{formatPrice(it.totalPrice)}</td>
                                                                        </tr>
                                                                    ))}
                                                                    </tbody>
                                                                </table>
                                                            </div>
                                                        </div>
                                                    </td>
                                                </tr>
                                            )}
                                        </Fragment>
                                    );
                                })}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}