import { useOrders } from "./orders/useOrders";
import { useOrderOptions } from "./orders/useOrderOptions";
import OrderCreate from "./orders/OrderCreate";
import OrderSearch from "./orders/OrderSearch";
import OrderList from "./orders/OrderList";

export default function Orders() {
    const orders = useOrders();
    const { customers, products, loadingOptions, optionsError } = useOrderOptions();

    const pageError = optionsError || orders.error;

    return (
        <>
            <div className="mb-3">
                <h2 className="fw-semibold mb-2">Pedidos</h2>

                {pageError && <div className="alert alert-danger mb-2">{pageError}</div>}
                {orders.success && <div className="alert alert-success mb-0">{orders.success}</div>}
            </div>

            <div className="row g-3 mb-3">
                <div className="col-12 d-flex">
                    <OrderCreate
                        loadingCreate={orders.loadingCreate}
                        onCreate={orders.createOne}
                        customers={customers}
                        products={products}
                        loadingOptions={loadingOptions}
                    />
                </div>
            </div>

            <div className="row g-3 mb-3">
                <div className="col-12 d-flex">
                    <OrderSearch
                        loading={orders.loadingList}
                        onListAll={orders.refreshAll}
                        onFindById={orders.findById}
                        onByCustomer={orders.filterByCustomer}
                        onByProduct={orders.filterByProduct}
                        onByPeriod={orders.filterByPeriod}
                        onTotalByCustomer={orders.fetchTotalByCustomer}
                        totalByCustomer={orders.totalByCustomer}
                        customers={customers}
                        products={products}
                        loadingOptions={loadingOptions}
                    />
                </div>
            </div>

            <div className="row g-3">
                <div className="col-12 d-flex">
                    <OrderList
                        items={orders.items}
                        loading={orders.loadingList}
                        customers={customers}
                        products={products}
                    />
                </div>
            </div>
        </>
    );
}