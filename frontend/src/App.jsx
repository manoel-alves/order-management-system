import {useState} from "react";
import AppLayout from "./layout/AppLayout.jsx";
import Customers from "./pages/Customers.jsx";
import Products from "./pages/Products.jsx";
import Orders from "./pages/Orders.jsx";

function App() {
    const [page, setPage] = useState('orders');

    const renderPage = () => {
        switch (page) {
            case "orders":
                return <Orders />;
            case "customers":
                return <Customers/>
            case "products":
                return <Products/>;
            default:
                return null;
        }
    };

    return (
        <AppLayout active={page} onNavigate={setPage}>
            {renderPage()}
        </AppLayout>
    );
}

export default App;
