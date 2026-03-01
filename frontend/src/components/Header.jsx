function Header({ active, onNavigate }) {
    const items = [
        { key: 'customers', label: 'Clientes' },
        { key: 'products', label: 'Produtos' },
        { key: 'orders', label: 'Pedidos' },
    ];

    return (
        <nav className="navbar navbar-expand-lg navbar-dark bg-dark py-3">
            <div className="container">
                <span className="navbar-brand fs-4 fw-bold me-5">
                    Order Management
                </span>

                <div className="collapse navbar-collapse show">
                    <ul className="navbar-nav gap-4">
                        {items.map((item) => (
                            <li className="nav-item fs-5" key={item.key}>
                                <button
                                    type="button"
                                    className={`nav-link ${
                                        active === item.key ? 'active' : ''
                                    }`}
                                    onClick={() => onNavigate(item.key)}
                                >
                                    {item.label}
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

export default Header;