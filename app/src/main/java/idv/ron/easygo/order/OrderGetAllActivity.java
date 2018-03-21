package idv.ron.easygo.order;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import idv.ron.easygo.R;
import idv.ron.easygo.main.Common;

public class OrderGetAllActivity extends AppCompatActivity {
    private final static String TAG = "OrderGetAllActivity";
    private String userId;
    private RadioGroup rgSearch;
    private LinearLayout layoutDate;
    private ListView lvOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_getall_activity);
        userId = getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE).getString("userId", "");
        if (userId.isEmpty()) {
            finish();
        }
        lvOrders = (ListView) findViewById(R.id.lvOrders);
        layoutDate = (LinearLayout) findViewById(R.id.layoutDate);
//        rgSearch = (RadioGroup) findViewById(R.id.rgSearch);
//        rgSearch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // if rbByDate is checked, show date picker dialog
//                if (checkedId == R.id.rbByDate) {
//                    layoutDate.setVisibility(View.VISIBLE);
//                    showNow();
//                } else {
//                    layoutDate.setVisibility(View.GONE);
//                    showOrders("", "");
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showOrders("", "");
    }

    // click Search by Date radio button
    public void onByDateClick(View view) {
        layoutDate.setVisibility(View.VISIBLE);
    }

    // click Start Date or End Date button
    public void onDateClick(View view) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("resId", view.getId());
        datePickerFragment.setArguments(bundle);
        FragmentManager fm = getSupportFragmentManager();
        datePickerFragment.show(fm, "datePicker");
    }

    // click Search button
    public void onSearchClick(View view) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Button btStartDate = (Button) findViewById(R.id.btStartDate);
        Button btEndDate = (Button) findViewById(R.id.btEndDate);
        String start = btStartDate.getText().toString();
        String end = btEndDate.getText().toString();
        try {
            Date startDate = simpleDateFormat.parse(start);
            Date endDate = simpleDateFormat.parse(end);
            // end date should not be less than start date
            if (startDate.after(endDate)) {
                Toast.makeText(
                        this,
                        R.string.msg_EndDateNotLessThanStartDate,
                        Toast.LENGTH_SHORT)
                        .show();
                return;
            }
        } catch (ParseException e) {
            Log.e(TAG, e.toString());
        }
        showOrders(start, end);
    }

    private void showOrders(String start, String end) {
        if (Common.networkConnected(this)) {
            String url = Common.URL + "OrderServlet";
            List<Order> orders = null;
            try {
                orders = new OrderGetAllTask().execute(url, userId, start, end).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (orders == null || orders.isEmpty()) {
                orders = new ArrayList<>();
                lvOrders.setAdapter(new OrdersAdapter(this, orders));
                Common.showToast(this, R.string.msg_NoOrderFound);
            } else {
                lvOrders.setAdapter(new OrdersAdapter(this, orders));
                lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Order order = (Order) parent.getItemAtPosition(position);
                        Intent intent = new Intent(OrderGetAllActivity.this, OrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("order", order);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int resId = getArguments().getInt("resId");
            Button button = (Button) getActivity().findViewById(resId);

            // DatePickerDialog will show the date on the clicked button without parse exception
            int year, month, day;
            Calendar calendar = Calendar.getInstance();
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = simpleDateFormat.parse(button.getText().toString());
                calendar.setTime(date);
            } catch (ParseException e) {
                Log.e(TAG, e.toString());
            } finally {
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
            }
            return new DatePickerDialog(
                    getActivity(), this, year, month, day);
        }

        @Override
        // display the date on the clicked button
        public void onDateSet(DatePicker view, int year, int month, int day) {
            int resId = getArguments().getInt("resId");
            updateDisplay(getActivity(), resId, year, month, day);
        }
    }

    private void showNow() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        updateDisplay(this, R.id.btStartDate, year, month, day);
        updateDisplay(this, R.id.btEndDate, year, month, day);
    }

    /**
     * update the date information on the clicked button
     *
     * @param activity current activity
     * @param resId    resource ID of the clicked button
     */
    private static void updateDisplay(Activity activity, int resId, int year, int month, int day) {
        Button button = (Button) activity.findViewById(resId);
        button.setText(new StringBuilder().append(year).append("-")
                .append(pad(month + 1)).append("-").append(pad(day)));
    }

    private static String pad(int number) {
        if (number >= 10)
            return String.valueOf(number);
        else
            return "0" + String.valueOf(number);
    }

    private class OrdersAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Order> orders;

        public OrdersAdapter(Context context, List<Order> orders) {
            this.layoutInflater = LayoutInflater.from(context);
            this.orders = orders;
        }

        @Override
        public int getCount() {
            if (orders == null || orders.size() <= 0) {
                return 0;
            }
            return orders.size();
        }

        @Override
        public Object getItem(int position) {
            return orders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return orders.get(position).getOrder_id();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Order order = orders.get(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.order_master_listview_item,
                        parent, false);
            }
            TextView tvOrderId = (TextView) convertView.findViewById(R.id.tvOrderId);
            tvOrderId.setText(String.valueOf(order.getOrder_id()));

            TextView tvCustomer = (TextView) convertView.findViewById(R.id.tvCustomer);
            tvCustomer.setText(order.getOrder_user());

            List<OrderProduct> orderProductList = order.getOrderProductList();
            double sum = 0;
            for (OrderProduct orderProduct : orderProductList) {
                sum += orderProduct.getFood_price() * orderProduct.getQuantity();
            }
            TextView tvSum = (TextView) convertView.findViewById(R.id.tvSum);
            tvSum.setText(String.valueOf(sum));

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            tvDate.setText(simpleDateFormat.format(order.getOrder_reserve_date()));
            return convertView;
        }
    }
}
