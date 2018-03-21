package idv.ron.easygo.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import idv.ron.easygo.R;
import idv.ron.easygo.main.Category;
import idv.ron.easygo.product.Product;
import idv.ron.easygo.product.ProductDetailActivity;

import static idv.ron.easygo.main.Common.CATEGORIES;

public class OrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        setViews();
    }

    private void setViews() {
        TextView tvOrder = (TextView) findViewById(R.id.tvOrder);
        ListView listView = (ListView) findViewById(R.id.listView);

        Order order = (Order) getIntent().getSerializableExtra("order");

        int orderId = order.getOrder_id();
        String user = order.getOrder_user();
        DateFormat df_default = DateFormat.getDateInstance(DateFormat.MEDIUM,
                Locale.getDefault());
        String date = df_default.format(new Date(order.getOrder_reserve_date()));
        double sum = 0;
        for (OrderProduct orderProduct : order.getOrderProductList()) {
            sum += orderProduct.getFood_price() * orderProduct.getQuantity();
        }
        String orderStr = getString(R.string.col_OrderId) + ": " + orderId + "\n"
                + getString(R.string.col_OrderCustomer) + ": " + user + "\n"
                + getString(R.string.col_OrderDate) + ": " + date + "\n"
                + getString(R.string.col_OrderSum) + ": " + Double.toString(sum);
        tvOrder.setText(orderStr);

        listView.setAdapter(new ProductListAdapter(this, order.getOrderProductList()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = (Product) parent.getItemAtPosition(position);
                Intent intent = new Intent(OrderActivity.this, ProductDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private class ProductListAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<OrderProduct> orderProductList;

        public ProductListAdapter(Context context, List<OrderProduct> orderProductList) {
            layoutInflater = LayoutInflater.from(context);
            this.orderProductList = orderProductList;
        }

        @Override
        public int getCount() {
            return orderProductList.size();
        }

        @Override
        public Object getItem(int position) {
            return orderProductList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return orderProductList.get(position).getFood_id();
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.product_orderitem, parent, false);
            }

            OrderProduct orderProduct = orderProductList.get(position);

            TextView tvOrderProductName = (TextView) convertView
                    .findViewById(R.id.tvOrderProductName);
            TextView tvOrderProductPrice = (TextView) convertView
                    .findViewById(R.id.tvOrderProductPrice);
            TextView tvOrderProductQuantity = (TextView) convertView
                    .findViewById(R.id.tvOrderProductQuantity);
            tvOrderProductName.setText(orderProduct.getFood_name());
            tvOrderProductPrice.setText(getString(R.string.col_Price) + ": "
                    + Double.toString(orderProduct.getFood_price()));
            tvOrderProductQuantity.setText("x " + orderProduct.getQuantity());
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (Category category : CATEGORIES) {
            int id = category.getId();
            String title = category.getTitle();
            menu.add(0, id, id, title);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (Category category : CATEGORIES) {
            if (item.getItemId() == category.getId()) {
                Intent intent = new Intent(this, category.getFirstActivity());
                startActivity(intent);
                return true;
            }
        }
        return false;
    }
}
