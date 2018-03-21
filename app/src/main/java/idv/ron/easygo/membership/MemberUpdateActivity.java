package idv.ron.easygo.membership;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Timestamp;

import idv.ron.easygo.R;
import idv.ron.easygo.main.Common;

public class MemberUpdateActivity extends AppCompatActivity {
    private final static String TAG = "UpdateProfileActivity";
    private TextView user_cellphone;
    private EditText user_password;
    private EditText etConfirmPassword;
    private EditText user_name;
    private EditText user_email;
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_update_activity);
        findViews();

    }

    private void findViews() {
        user_cellphone = (TextView) findViewById(R.id.tvUserId);
        user_password = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        user_name = (EditText) findViewById(R.id.etName);
        user_email = (EditText) findViewById(R.id.etEmail);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

//        etName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MemberUpdateActivity.this,MemberActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("name", String.valueOf(etName));
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fillProfile();
    }

    private void fillProfile() {
        SharedPreferences preferences = getSharedPreferences(
                Common.PREF_FILE, MODE_PRIVATE);
        final String userId = preferences.getString("userId", "");
        // without user ID, the user cannot update profile, so just finish this activity
        if (userId.isEmpty()) {
            Common.showToast(this, R.string.msg_NoProfileFound);
            finish();
        }
        if (Common.networkConnected(this)) {
            String url = Common.URL + "MemberServlet";
            Member member = null;
            try {
                member = new MemberFindByIdTask().execute(url, userId).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (member == null) {
                Common.showToast(MemberUpdateActivity.this, R.string.msg_NoProfileFound);
                finish();
            } else {
                user_cellphone.setText(member.getUser_cellphone());
                user_password.setText(member.getUser_password());
                etConfirmPassword.setText(member.getUser_password());
                user_name.setText(member.getUser_name());
                user_email.setText(member.getUser_email());

            }
        } else {
            Common.showToast(this, R.string.msg_NoNetwork);
        }
    }

    public void onSubmitClick(View view) {
        String cellphone = user_cellphone.getText().toString().trim();
        String password = user_password.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String name = user_name.getText().toString().trim();
        String email = user_email.getText().toString().trim();

        String message = "";
        boolean isInputValid = true;
        if (cellphone.isEmpty()) {
            message += getString(R.string.text_UserId) + " "
                    + getString(R.string.msg_InputEmpty) + "\n";
            isInputValid = false;
        }
        if (password.isEmpty()) {
            message += getString(R.string.hint_etPassword) + " "
                    + getString(R.string.msg_InputEmpty) + "\n";
            isInputValid = false;
        }
        if (!confirmPassword.equals(password)) {
            message += getString(R.string.msg_ConfirmPasswordNotSameAsPassword);
            isInputValid = false;
        }
        if (name.isEmpty()) {
            message += getString(R.string.text_etName) + " "
                    + getString(R.string.msg_InputEmpty) + "\n";
            isInputValid = false;
        }
        if (email.isEmpty()) {
            message += getString(R.string.text_etEmail) + " "
                    + getString(R.string.msg_InputEmpty) + "\n";
            isInputValid = false;
        }

        tvMessage.setText(message);
        if (isInputValid) {
            String url = Common.URL + "MemberServlet";
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            Member member = new Member(null, cellphone, password, name, email,
                    null, ts, "normal", "consumer", 1);
            int count = 0;
            try {
                count = new MemberUpdateTask().execute(url, "update", member).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                tvMessage.setText(R.string.msg_FailUpdateProfile);
            } else {
                // user ID and password will be saved in the preferences file
                SharedPreferences preferences = getSharedPreferences(
                        Common.PREF_FILE, MODE_PRIVATE);
                preferences.edit().putBoolean("login", true)
                        .putString("cellphone", cellphone)
                        .putString("password", password).apply();
                Common.showToast(this, R.string.msg_SuccessfullyUpdateProfile);
                finish();
            }
        }
    }
}
