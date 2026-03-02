import {useCustomers} from "./customers/useCustomers";
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
        createOne,
        selectById,
    } = useCustomers();

    return (
        <>
            <div className="mb-3">
                <h2 className="fw-semibold mb-2">Clientes</h2>

                {error && <div className="alert alert-danger mb-2">{error}</div>}
                {success && <div className="alert alert-success mb-0">{success}</div>}
            </div>

            {/* Ação principal */}
            <div className="row g-3 mb-3">
                <div className="col-12 d-flex">
                    <CustomerCreate loadingCreate={loadingCreate} createOne={createOne}/>
                </div>
            </div>

            {/* Filtros */}
            <div className="row g-3 mb-3">
                <div className="col-12 d-flex">
                    <CustomerSearch
                        loading={loadingList}
                        onListAll={refreshAll}
                        onFindById={selectById}
                        onSearchByName={searchByName}
                    />
                </div>
            </div>

            {/* Resultados */}
            <div className="row g-3">
                <div className="col-12 d-flex">
                    <CustomerList items={items} loading={loadingList}/>
                </div>
            </div>
        </>
    );
}