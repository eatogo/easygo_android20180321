package idv.ron.easygo.cart;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import idv.ron.easygo.R;
import idv.ron.easygo.main.Category;
import idv.ron.easygo.main.Common;
import idv.ron.easygo.membership.MemberLoginActivity;
import idv.ron.easygo.order.Order;
import idv.ron.easygo.order.OrderActivity;
import idv.ron.easygo.order.OrderInsertTask;
import idv.ron.easygo.order.OrderProduct;
import idv.ron.easygo.product.ProductGetImageTask;

import static idv.ron.easygo.main.Common.CART;
import static idv.ron.easygo.main.Common.CATEGORIES;

public class CartActivity extends AppCompatActivity {
    private final static String TAG = "CartActivity";
    private final static int REQUEST_LOGIN = 0;
    private RecyclerView rvItems;
    private LinearLayout layoutEmpty;
    private TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);
        findViews();
    }

    private void findViews() {
        layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        showTotal(CART);
        rvItems.setAdapter(new CartRecyclerViewAdapter(this, CART));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    String url = Common.URL + "OrderServlet";
                    SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                            MODE_PRIVATE);
                    String userId = pref.getString("userId", "");
                    Order order = null;
                    try {
                        order = new OrderInsertTask().execute(url, userId, CART).get();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                    if (order == null) {
                        Common.showToast(CartActivity.this, R.string.msg_FailCreateOrder);
                    } else {
                        CART.clear();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("order", order);
                        Intent intentOrder = new Intent(CartActivity.this,
                                OrderActivity.class);
                        intentOrder.putExtras(bundle);
                        startActivity(intentOrder);
                    }
                }
                break;
        }
    }

    public void onCheckoutClick(View view) {
        if (CART == null || CART.size() <= 0) {
            Common.showToast(this, R.string.cartEmpty);
            return;
        }
        Intent loginIntent = new Intent(this, MemberLoginActivity.class);
        startActivityForResult(loginIntent, REQUEST_LOGIN);
    }

    public void onEmptyCartClick(View view) {
        if (CART == null || CART.size() <= 0) {
            Common.showToast(this, R.string.cartEmpty);
            return;
        }
        String message = getString(R.string.msg_ClearCart);
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.cart)
                .setTitle(R.string.btClearCart)
                .setMessage(message)
                .setPositiveButton(R.string.text_btConfirm,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                CART.clear();
                                showTotal(CART);
                                // notifyDataSetChanged()
                                // refresh data set
                                rvItems.getAdapter().notifyDataSetChanged();
                            }
                        })

                .setNegativeButton(R.string.text_btNo,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        }).setCancelable(false).show();
    }

    private class CartRecyclerViewAdapter extends RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder> {
        private Context context;
        private LayoutInflater layoutInflater;
        private List<OrderProduct> orderProducts;

        CartRecyclerViewAdapter(Context context, List<OrderProduct> orderProducts) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.orderProducts = orderProducts;
        }

        @Override
        public int getItemCount() {
            if (CART.size() <= 0) {
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
            return orderProducts.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.cart_recyclerview_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            final OrderProduct orderProduct = orderProducts.get(position);

            String url = Common.URL + "ProductServlet";
            int id = orderProduct.getFood_id();
            int imageSize = 250;
            new ProductGetImageTask(viewHolder.ivProduct).execute(url, id, imageSize);
            viewHolder.tvName.setText(orderProduct.getFood_name());
            viewHolder.tvPrice.setText(String.valueOf(orderProduct.getFood_price()));
            viewHolder.spQty.setSelection(orderProduct.getQuantity() - 1, true);
            viewHolder.spQty.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent,
                                           View view, int position, long id) {
                    int quantity = Integer.parseInt(parent
                            .getItemAtPosition(position).toString());
                    orderProduct.setQuantity(quantity);
                    showTotal(CART);
                    Common.showToast(context,
                            getString(R.string.msg_NewQty) + " " +
                                    orderProduct.getQuantity());
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
            viewHolder.ivRemove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = getString(R.string.cartRemove) + "「"
                            + orderProduct.getFood_name() + "」?";
                    new AlertDialog.Builder(context)
                            .setIcon(R.drawable.cart)
                            .setTitle(R.string.cartRemove)
                            .setMessage(message)
                            .setPositiveButton(R.string.text_btYes,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            CART.remove(orderProduct);
                                            showTotal(CART);
                                            CartRecyclerViewAdapter.this
                                                    .notifyDataSetChanged();
                                        }
                                    })
                            .setNegativeButton(R.string.text_btNo,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.cancel();
                                        }
                                    }).setCancelable(false).show();
                }
            });
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View itemView;
            ImageView ivProduct;
            ImageView ivRemove;
            TextView tvName;
            TextView tvPrice;
            Spinner spQty;

            ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                ivProduct = (ImageView) itemView.findViewById(R.id.ivProduct);
                ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
                spQty = (Spinner) itemView.findViewById(R.id.spQty);
            }
        }
    }


    private void showTotal(List<OrderProduct> orderProductList) {
        double total = 0;
        for (OrderProduct orderProduct : orderProductList) {
            total += orderProduct.getFood_price() * orderProduct.getQuantity();
        }
        String text = "Total: " + total;
        tvTotal.setText(text);
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
