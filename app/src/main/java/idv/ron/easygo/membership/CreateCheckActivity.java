package idv.ron.easygo.membership;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import idv.ron.easygo.R;

public class CreateCheckActivity extends Activity implements OnClickListener {

    private ImageView image;
    private EditText et;
    private Button btn,submit;
    private String codeStr;
    private CodeUtils codeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        initView();

    }

    private void initView() {
        image = (ImageView) findViewById(R.id.image);
        et = (EditText) findViewById(R.id.et);
        btn = (Button) findViewById(R.id.btn);
        submit = (Button) findViewById(R.id.btn_submit);
        btn.setOnClickListener(this);
        submit.setOnClickListener(this);

    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                codeUtils = CodeUtils.getInstance();
                Bitmap bitmap = codeUtils.createBitmap();
                image.setImageBitmap(bitmap);

                break;
            case R.id.btn_submit:
                codeStr = et.getText().toString().trim();
                Log.e("codeStr", codeStr);
                if (null == codeStr || TextUtils.isEmpty(codeStr)) {
                    Toast.makeText(this, "請輸入驗證碼", 0).show();
                    return;
                }
                String code = codeUtils.getCode();
                Log.e("code", code);
                if (code.equalsIgnoreCase(codeStr)) {
                    Toast.makeText(this, "驗證碼正確", 0).show();
                    Intent intent = new Intent(this, MemberTakepictureActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "驗證碼錯誤", 0).show();
                }
                break;
            default:
                break;
        }

    }
}
