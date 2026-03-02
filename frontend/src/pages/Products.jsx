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
        selectById,
    } = useProducts();

    return (
        <>
            <div className="mb-3">
                <h2 className="fw-semibold mb-2">Produtos</h2>

                {error && <div className="alert alert-danger mb-2">{error}</div>}
                {success && <div className="alert alert-success mb-0">{success}</div>}
            </div>

            {/* Ação principal */}
            <div className="row g-3 mb-3">
                <div className="col-12 d-flex">
                    <ProductCreate loadingCreate={loadingCreate} createOne={createOne} />
                </div>
            </div>

            {/* Filtros */}
            <div className="row g-3 mb-3">
                <div className="col-12 d-flex">
                    <ProductSearch
                        loading={loadingList}
                        onListAll={refreshAll}
                        onFindById={selectById}
                        onSearchByDescription={searchByDescription}
                    />
                </div>
            </div>

            {/* Resultados */}
            <div className="row g-3">
                <div className="col-12 d-flex">
                    <ProductList items={items} loading={loadingList} />
                </div>
            </div>
        </>
    );
}