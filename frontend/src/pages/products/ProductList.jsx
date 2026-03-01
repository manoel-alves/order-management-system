export default function ProductList({ items, loadingList, refreshAll }) {

    function formatDate(value) {
        if (!value) return "-";
        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? "-" : date.toLocaleDateString("pt-BR");
    }

    function formatPrice(value) {
        const valueAsNumber = Number(String(value ?? 0).replace(",", "."));
        if (Number.isNaN(valueAsNumber)) return "R$ 0,00";

        return new Intl.NumberFormat("pt-BR", {
            style: "currency",
            currency: "BRL",
        }).format(valueAsNumber);
    }

    return (
        <div className="card w-100 product-list-card">
            <div className="card-body d-flex flex-column">
                <div className="row g-3">
                    <h5 className="card-title col-sm-10 align-content-center justify-content-center">Lista de produtos</h5>

                    <div className="col-sm-2 mt-2 d-grid">
                        <button
                            className="btn btn-sm btn-outline-primary"
                            onClick={() => {
                                void refreshAll()}
                            }
                        >
                            Listar todos
                        </button>
                    </div>
                </div>

                <hr/>

                {loadingList ? (
                    <p className="text-muted">Carregando...</p>
                ) : items.length === 0 ? (
                    <p className="text-muted">Nenhum produto encontrado.</p>
                ) : (
                    <div className="list-scroll">
                        <table className="table table-hover mb-0">
                            <thead>
                            <tr>
                                <th style={{ width: 80 }}>ID</th>
                                <th>Descrição</th>
                                <th>Preço</th>
                                <th>Estoque</th>
                                <th>Data de cadastro</th>
                            </tr>
                            </thead>

                            <tbody>
                            {items.map((product) => (
                                <tr key={product.id}>
                                    <td>{product.id}</td>
                                    <td>{product.description}</td>
                                    <td>{formatPrice(product.price)}</td>
                                    <td>{product.stockQuantity}</td>
                                    <td>{formatDate(product.createdAt)}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}