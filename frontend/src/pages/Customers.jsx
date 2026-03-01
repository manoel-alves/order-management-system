import { useCustomers } from "./customers/useCustomers";
import CustomerCreate from "./customers/CustomerCreate.jsx";
import CustomerSearch from "./customers/CustomerSearch.jsx";
import CustomerList from "./customers/CustomerList.jsx";

export default function Customers() {
    const {
        items,
        loadingList,
        loadingCreate,
        error,
        success,
        refreshAll,
        searchByName,
        createOne
    } = useCustomers();

    return (
        <>
            <div className="mb-3">
                <h2 className="fw-semibold">Clientes</h2>

                {error && <div className="alert alert-danger">{error}</div>}
                {success && <div className="alert alert-success">{success}</div>}
            </div>

            <div className="row mb-3 g-3 align-items-stretch">
                <div className="col-md-6 d-flex">
                    <CustomerCreate
                        loadingCreate={loadingCreate}
                        createOne={createOne}
                    />
                </div>

                <div className="col-md-6 d-flex">
                    <CustomerSearch
                        loadingList={loadingList}
                        searchByName={searchByName}
                    />
                </div>
            </div>

            <div className="row g-3">
                <div className="col-12 d-flex">
                    <CustomerList
                        items={items}
                        loadingList={loadingList}
                        refreshAll={refreshAll}
                    />
                </div>
            </div>
        </>
    );
}