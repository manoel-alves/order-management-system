function Header({ active, onNavigate }) {
    const items = [
        { key: "customers", label: "Clientes" },
        { key: "products", label: "Produtos" },
        { key: "orders", label: "Pedidos" },
    ];

    return (
        <nav className="navbar navbar-dark bg-dark fixed-top">
            <div className="container d-flex align-items-center gap-3">
                <span className="navbar-brand mb-0 fs-3 fw-bold">Gerenciador de pedidos</span>

                <div className="d-flex gap-2">
                    {items.map((item) => (
                        <button
                            key={item.key}
                            type="button"
                            className={`btn btn-sm ${
                                active === item.key ? "btn-light" : "btn-outline-light"
                            }`}
                            onClick={() => onNavigate(item.key)}
                        >
                            {item.label}
                        </button>
                    ))}
                </div>
            </div>
        </nav>
    );
}

export default Header;