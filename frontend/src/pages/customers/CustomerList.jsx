export default function CustomerList({ items, loading }) {
    function formatDate(value) {
        if (!value) return "-";
        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? "-" : date.toLocaleDateString("pt-BR");
    }

    return (
        <div className="card w-100 ui-card">
            <div className="card-body">
                <div className="d-flex flex-wrap gap-2 align-items-center justify-content-between">
                    <div>
                        <h5 className="card-title mb-0">Lista de clientes</h5>
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
                    <p className="text-muted mb-0">Nenhum cliente encontrado.</p>
                ) : (
                    <div className="list-scroll">
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead className="table-light position-sticky top-0">
                                <tr>
                                    <th style={{ width: 80 }}>ID</th>
                                    <th>Nome</th>
                                    <th>Email</th>
                                    <th style={{ width: 160 }}>Data de cadastro</th>
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
                    </div>
                )}
            </div>
        </div>
    );
}