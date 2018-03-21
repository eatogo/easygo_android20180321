package idv.ron.easygo.membership;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import idv.ron.easygo.R;
import idv.ron.easygo.main.Common;
import idv.ron.easygo.main.MainActivity;

public class MemberActivity extends AppCompatActivity {
    private final static String TAG = "MemberActivity";
    private static final int REQUEST_LOGIN = 1;
    private TextView tvUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_activity);
        onLogin();
        findView();
//        Bundle bundle =this.getIntent().getExtras();
//        String Name = bundle.getString("name");
//        TextView tvUserId = (TextView)findViewById(R.id.tvUserId);
//        tvUserId.setText(Name);
    }
    private void findView() {
        tvUserName = (TextView) findViewById(R.id.tvUserId);
//        Bundle bundle = (Bundle) this.getIntent().getExtras();
//        String name = (String) bundle.get("name");
//        tvUserName.setText(name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOGIN:
                    SharedPreferences preferences =
                            getSharedPreferences(Common.PREF_FILE, MODE_PRIVATE);
                    boolean login = preferences.getBoolean("login", false);
                    if (!login) {
                        Common.showToast(MemberActivity.this, "login failed");
                        onLogin();
                    }
            }

        }

    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        fillProfile();
//    }
//
//    private void fillProfile() {
//        SharedPreferences preferences = getSharedPreferences(
//                Common.PREF_FILE, MODE_PRIVATE);
//        final String userId = preferences.getString("name", "");
//        // without user ID, the user cannot update profile, so just finish this activity
//        if (userId.isEmpty()) {
//            Common.showToast(this, R.string.msg_NoProfileFound);
//            finish();
//        }
//        if (Common.networkConnected(this)) {
//            String url = Common.URL + "MemberServlet";
//            Member member = null;
//            try {
//                member = new MemberFindByIdTask().execute(url, userId).get();
//            } catch (Exception e) {
//                Log.e(TAG, e.toString());
//            }
//            if (member == null) {
//                Common.showToast(MemberActivity.this, R.string.msg_NoProfileFound);
//                finish();
//            } else {
//                tvUserName.setText(member.getUserId());
//
//            }
//        } else {
//            Common.showToast(this, R.string.msg_NoNetwork);
//        }
//    }

    /**
     * check if the user login or not
     */
    private void onLogin() {
        Intent loginIntent = new Intent(this, MemberLoginActivity.class);
        startActivityForResult(loginIntent, REQUEST_LOGIN);
    }




    public void onMemberUpdateClick(View view) {
        Intent intent = new Intent(this, MemberUpdateActivity.class);
        startActivity(intent);
    }

    public void onLogoutClick(View view) {
        SharedPreferences pref = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        pref.edit().putBoolean("login", false).apply();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
