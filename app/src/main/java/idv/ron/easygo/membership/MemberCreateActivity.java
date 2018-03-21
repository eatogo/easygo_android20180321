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

public class MemberCreateActivity extends AppCompatActivity {
    private static final String TAG = "MemberCreateActivity";
    private EditText user_cellphone;
    private EditText user_password;
    private EditText etConfirmPassword;
    private EditText user_name;
    private EditText user_email;
    private TextView tvMessage;
    private boolean memberIdExist = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_create_activity);
        findViews();
    }

    private void findViews() {
        user_cellphone = (EditText) findViewById(R.id.tvUserId);
        user_password = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        user_name = (EditText) findViewById(R.id.etName);
        user_email = (EditText) findViewById(R.id.etEmail);
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        // check if the id is been used
        user_cellphone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String url = Common.URL + "MemberServlet";
                    try {
                        memberIdExist = new MemberIdExistTask().execute(url, user_cellphone.getText().toString()).get();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                    // show an error message if the id exists;
                    // otherwise, the error message should be clear
                    if (memberIdExist) {
                        tvMessage.setText(R.string.msg_UserIdExist);
                    } else {
                        tvMessage.setText(null);
                    }
                }
            }
        });
    }

    public void onSubmitClick(View view) {
        String cellphone = user_cellphone.getText().toString().trim();
        String password = user_password.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String name = user_name.getText().toString().trim();
        String email = user_email.getText().toString().trim();
        String message = "";
        boolean isInputValid = true;
        if (memberIdExist) {
            message += getString(R.string.msg_UserIdExist) + "\n";
            isInputValid = false;
        }
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
                count = new MemberUpdateTask().execute(url, "insert", member).get();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            if (count == 0) {
                tvMessage.setText(R.string.msg_FailCreateAccount);
            } else {
                // user ID and password will be saved in the preferences file
                // and starts MembershipActivity
                // while the user account is created successfully
                SharedPreferences preferences = getSharedPreferences(
                        Common.PREF_FILE, MODE_PRIVATE);
                preferences.edit().putBoolean("login", true)
                        .putString("cellphone", cellphone)
                        .putString("password", password).apply();
                Common.showToast(this, R.string.msg_SuccessfullyCreateAccount);
                Intent intent = new Intent(this, CreateCheckActivity.class);
                startActivity(intent);
            }
        }

    }
}
