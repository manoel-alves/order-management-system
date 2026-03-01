export default function CustomerList({ items, loadingList, refreshAll }) {

    function formatDate(value) {
        if (!value) return "-";
        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? "-" : date.toLocaleDateString("pt-BR");
    }

    return (
        <div className="card w-100 product-list-card">
            <div className="card-body d-flex flex-column">
                <div className="row g-3">
                    <h5 className="card-title col-sm-10 align-content-center justify-content-center">Lista de clientes</h5>

                    <div className="col-sm-2 mt-2 d-grid">
                        <button
                            className="btn btn-sm btn-outline-primary"
                            onClick={() => void refreshAll()}
                        >
                            Listar todos
                        </button>
                    </div>
                </div>

                <hr/>

                {loadingList ? (
                    <p className="text-muted">Carregando...</p>
                ) : items.length === 0 ? (
                    <p className="text-muted">Nenhum cliente encontrado.</p>
                ) : (
                    <div className="list-scroll">
                        <table className="table table-hover  mb-0">
                            <thead>
                            <tr>
                                <th style={{ width: 80 }}>ID</th>
                                <th>Nome</th>
                                <th>Email</th>
                                <th>Data de cadastro</th>
                            </tr>
                            </thead>

                            <tbody>
                            {items.map((customer) => (
                                <tr key={customer.id}>
                                    <td>{customer.id}</td>
                                    <td>{customer.name}</td>
                                    <td>{customer.email}</td>
                                    <td>{formatDate(customer.createdAt)}</td>
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