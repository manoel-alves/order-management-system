export default function ProductList({ items, loading }) {
    function formatDate(value) {
        if (!value) return "-";
        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? "-" : date.toLocaleDateString("pt-BR");
    }

    function formatPrice(value) {
        const valueAsNumber = Number(String(value ?? 0).replace(",", "."));
        if (Number.isNaN(valueAsNumber)) return "R$ 0,00";
        return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valueAsNumber);
    }

    return (
        <div className="card w-100 ui-card">
            <div className="card-body">
                <div className="d-flex flex-wrap gap-2 align-items-center justify-content-between">
                    <div>
                        <h5 className="card-title mb-0">Lista de produtos</h5>
                        <div className="small text-muted">Resultados atuais.</div>
                    </div>
                    <div className="small text-muted">
                        {items?.length ? <>{items.length} resultado(s)</> : ""}
                    </div>
                </div>

                <hr className="my-3" />

                {loading ? (
                    <p className="text-muted mb-0">Carregando...</p>
                ) : !items || items.length === 0 ? (
                    <p className="text-muted mb-0">Nenhum produto encontrado.</p>
                ) : (
                    <div className="list-scroll">
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead className="table-light position-sticky top-0">
                                <tr>
                                    <th style={{ width: 80 }}>ID</th>
                                    <th>Descrição</th>
                                    <th style={{ width: 140 }} className="text-end">Preço</th>
                                    <th style={{ width: 110 }} className="text-end">Estoque</th>
                                    <th style={{ width: 160 }}>Data de cadastro</th>
                                </tr>
                                </thead>
                                <tbody>
                                {items.map((product) => (
                                    <tr key={product.id}>
                                        <td>{product.id}</td>
                                        <td>{product.description}</td>
                                        <td className="text-end">{formatPrice(product.price)}</td>
                                        <td className="text-end">{product.stockQuantity}</td>
                                        <td>{formatDate(product.createdAt)}</td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}