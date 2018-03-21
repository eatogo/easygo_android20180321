package idv.ron.easygo.membership;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import idv.ron.easygo.R;
import idv.ron.easygo.main.Common;

public class MemberLoginActivity extends AppCompatActivity {
    private TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_login_activity);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences preferences = getSharedPreferences(Common.PREF_FILE,
                MODE_PRIVATE);
        boolean login = preferences.getBoolean("login", false);
        if (login) {
            String cellphone = preferences.getString("userId", "");
            String password = preferences.getString("password", "");
            if (isMember(cellphone, password)) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void showMessage(int msgResId) {
        tvMessage.setText(msgResId);
    }

    private boolean isMember(final String cellphone, final String password) {
        boolean isMember;
        String url = Common.URL + "MemberServlet";
        try {
            isMember = new MemberExistTask().execute(url, cellphone, password).get();
        } catch (Exception e) {
            isMember = false;
        }
        return isMember;
    }

    public void clickLogin(View view) {
        EditText user_cellphone = (EditText) findViewById(R.id.etUserId);
        EditText etPassword = (EditText) findViewById(R.id.etPassword);
        String cellphone = user_cellphone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (cellphone.length() <= 0 || password.length() <= 0) {
            showMessage(R.string.msg_InvalidUserOrPassword);
            return;
        }

        if (isMember(cellphone, password)) {
            SharedPreferences preferences = getSharedPreferences(
                    Common.PREF_FILE, MODE_PRIVATE);
            preferences.edit().putBoolean("login", true)
                    .putString("user_cellphone", cellphone)
                    .putString("password", password).apply();
            setResult(RESULT_OK);
            finish();
        } else {
            showMessage(R.string.msg_InvalidUserOrPassword);
        }
    }

    public void onNewAccountClick(View view) {
        Intent intent = new Intent(this, MemberCreateActivity.class);
        startActivity(intent);
        finish();

    }

}
