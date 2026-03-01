import { useProducts } from "./products/useProducts";
import ProductList from "./products/ProductList.jsx";
import ProductCreate from "./products/ProductCreate.jsx";
import ProductSearch from "./products/ProductSearch.jsx";

export default function Products() {
    const {
        items,
        loadingList,
        loadingCreate,
        error,
        success,
        refreshAll,
        searchByDescription,
        createOne,
    } = useProducts();

    return (
        <>
            <div className="mb-3">
                <h2 className="fw-semibold">Produtos</h2>

                {error && <div className="alert alert-danger mb-2">{error}</div>}
                {success && <div className="alert alert-success mb-0">{success}</div>}
            </div>

            <div className="row mb-3 g-3 align-items-stretch">
                <div className="col-md-6 d-flex">

                    <ProductCreate
                        loadingCreate={loadingCreate}
                        createOne={createOne}
                    />

                </div>

                <div className="col-md-6 d-flex">
                    <ProductSearch
                        loadingList={loadingList}
                        searchByDescription={searchByDescription}
                    />
                </div>
            </div>

            <div className="row g-3">
                <div className="col-12 d-flex">
                    <ProductList
                        items={items}
                        loadingList={loadingList}
                        refreshAll={refreshAll}
                    />
                </div>
            </div>
        </>
    );
}