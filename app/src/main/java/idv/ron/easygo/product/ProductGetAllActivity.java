package idv.ron.easygo.product;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;
import idv.ron.easygo.R;
import idv.ron.easygo.favorite.FavoriteInsertTask;
import idv.ron.easygo.main.Category;
import idv.ron.easygo.main.Common;
import idv.ron.easygo.order.Order;
import idv.ron.easygo.order.OrderProduct;
import static idv.ron.easygo.main.Common.CATEGORIES;
import static idv.ron.easygo.main.Common.FAVORITE;

public class ProductGetAllActivity extends AppCompatActivity {
    private static final String TAG = "ProductGetAllActivity";
    private RecyclerView rvProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.productlist_activity);
        rvProducts = (RecyclerView) findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Common.networkConnected(this)) {
            String url = Common.URL + "ProductServlet";
            List<Product> products = null;
            try {
                products = new ProductGetAllTask().execute(url).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (products == null || products.isEmpty()) {
                Common.showToast(ProductGetAllActivity.this, R.string.msg_NoProductsFound);
            } else {
                rvProducts.setAdapter(new ProductsRecyclerViewAdapter(ProductGetAllActivity.this, products));
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    private class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {
        private Context context;
        private LayoutInflater layoutInflater;
        private List<Product> products;

        ProductsRecyclerViewAdapter(Context context, List<Product> products) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
            this.products = products;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivProduct, ivAdd;
            TextView tvName, tvPrice;

            ViewHolder(View itemView) {
                super(itemView);
                ivProduct = (ImageView) itemView.findViewById(R.id.ivProduct);
                ivAdd = (ImageView) itemView.findViewById(R.id.ivAdd);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            }
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.product_recyclerview_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {

            final Product product = products.get(position);
            String url = Common.URL + "ProductServlet";
            int id = product.getFood_id();
            int imageSize = 250;
            new ProductGetImageTask(viewHolder.ivProduct).execute(url, id, imageSize);
            viewHolder.tvName.setText(product.getFood_name());
            viewHolder.tvPrice.setText(String.valueOf(product.getFood_price()));
            viewHolder.ivAdd.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            viewHolder.ivAdd.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    OrderProduct orderProduct = new OrderProduct(product, 1);
                    int index = FAVORITE.indexOf(orderProduct);
                    if (index == -1) {
                        FAVORITE.add(orderProduct);
                        String url = Common.URL + "favoriteSysyem";
                        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                                MODE_PRIVATE);
                        String userId = pref.getString("user_cellphone", "");
                        for(OrderProduct product:FAVORITE) {
                            Integer food_id = product.getFood_id();
                            Order order = null;
                            try {
                                order = new FavoriteInsertTask().execute(url, userId, food_id).get();
                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }

                            if (order == null) {
                                Common.showToast(ProductGetAllActivity.this, R.string.msg_FailCreateOrder);
                            } else {
                                FAVORITE.clear();
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("order", order);
//                        Intent intentOrder = new Intent(FavoriteActivity.this,
//                                OrderActivity.class);
//                        intentOrder.putExtras(bundle);
//                        startActivity(intentOrder);
                            }
                        }

                    } else {
                        FAVORITE.get(index).setQuantity(orderProduct.getQuantity() + 1);
                    }
//                    StringBuilder sb = new StringBuilder();
//                    for (OrderProduct oProduct : FAVORITE) {
//                        String text = "\n-" + oProduct.getName() + " x "
//                                + oProduct.getQuantity();
//                        sb.append(text);
//                    }
//                    String message = getString(R.string.cartContents) + " " + sb.toString();
//                    new AlertDialog.Builder(context)
//                            .setIcon(R.drawable.cart)
//                            .setTitle(
//                                    getString(R.string.cartAdd) + "\n「"
//                                            + product.getName() + "」")
//                            .setMessage(message)
//                            .setNeutralButton(R.string.text_btConfirm,
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(
//                                                DialogInterface dialog,
//                                                int which) {
//                                            dialog.cancel();
//                                        }
//                                    }).show();

                    viewHolder.ivAdd.setImageResource(R.drawable.ic_favorite_black_24dp);
                    Toast.makeText(ProductGetAllActivity.this,""+product.getFood_name()+"was added to favorites", Toast.LENGTH_SHORT).show();

                }
            });
            viewHolder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProductGetAllActivity.this,
                            ProductDetailActivity.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                }
            });
        }


    }


    // 依據CATEGORIES產生對應MenuItem
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
