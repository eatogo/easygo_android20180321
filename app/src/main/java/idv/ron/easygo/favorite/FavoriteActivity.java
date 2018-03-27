package idv.ron.easygo.favorite;


import android.app.Activity;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import idv.ron.easygo.R;
import idv.ron.easygo.main.Category;
import idv.ron.easygo.main.Common;
import idv.ron.easygo.order.Order;
import idv.ron.easygo.order.OrderProduct;
import idv.ron.easygo.product.Product;

import idv.ron.easygo.product.ProductGetImageTask;
import static idv.ron.easygo.main.Common.FAVORITE;
import static idv.ron.easygo.main.Common.CATEGORIES;


public class FavoriteActivity extends AppCompatActivity {
    private final static String TAG = "FavoriteActivity";
    private final static int REQUEST_LOGIN = 0;
    private RecyclerView rvItems;
    private LinearLayout layoutEmpty;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_activity);
        findViews();
    }

    private void findViews() {
        layoutEmpty = (LinearLayout) findViewById(R.id.layoutEmpty);
        rvItems = (RecyclerView) findViewById(R.id.rvItems);
        rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    String url = Common.URL + "favoriteSysyem";
                    SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                            MODE_PRIVATE);
                    String userId = pref.getString("userId", "");
                    Order order = null;
                    try {
                        order = new FavoriteInsertTask().execute(url, userId, FAVORITE).get();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }

                    if (order == null) {
                        Common.showToast(FavoriteActivity.this, R.string.msg_FailCreateOrder);
                    } else {

                    }
                }
                break;
        }
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
            if (FAVORITE.size() <= 0) {
                layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                layoutEmpty.setVisibility(View.GONE);
            }
            return orderProducts.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = layoutInflater.inflate(R.layout.favorite_recyclerview_item, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, int position) {
            final OrderProduct orderProduct = orderProducts.get(position);

            String url = Common.URL + "ProductServlet";
           int id = orderProduct.getFood_id();
            int imageSize = 250;
            new ProductGetImageTask(viewHolder.ivProduct).execute(url, id, imageSize);
            viewHolder.tvName.setText(orderProduct.getFood_name());
            viewHolder.tvPrice.setText(String.valueOf(orderProduct.getFood_price()));
            viewHolder.ivRemove.setImageResource(R.drawable.ic_favorite_black_24dp);
            viewHolder.ivRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewHolder.ivRemove.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(FavoriteActivity.this,""+orderProduct.
                                    getFood_name()+"was remove to favorites", Toast.LENGTH_SHORT).show();
                                            FAVORITE.remove(orderProduct);
                                            CartRecyclerViewAdapter.this
                                                    .notifyDataSetChanged();
                                        }
            });
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View itemView;
            ImageView ivProduct;
            ImageView ivRemove;
            TextView tvName;
            TextView tvPrice;
//            Spinner spQty;

            ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                ivProduct = (ImageView) itemView.findViewById(R.id.ivProduct);
                ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
//                spQty = (Spinner) itemView.findViewById(R.id.spQty);
            }
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
