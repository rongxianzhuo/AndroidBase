package com.zhimeng.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhimeng.base.base.BaseActivity;

public class ToActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to);
        Toast.makeText(this, "" + getIntentData(), Toast.LENGTH_SHORT).show();
    }

    public void buttonClick(View view) {
        setResult("result");
        finish();
    }
}
