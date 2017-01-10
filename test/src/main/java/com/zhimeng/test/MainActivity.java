package com.zhimeng.test;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.zhimeng.base.base.BaseActivity;
import com.zhimeng.base.view.LoginView;
import com.zhimeng.base.view.TabPagerView;

public class MainActivity extends BaseActivity {

    private LoginView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabPagerView tabPagerView = (TabPagerView) findViewById(R.id.tabPagerView);
        tabPagerView.getTabLayout().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabPagerView.setup(this, new String[]{"1", "2"}, new android.support.v4.app.Fragment[]{new Fragment1(), new Fragment2()});
        view = new LoginView(this).setup(new LoginView.LoginCallback() {
            @Override
            public void signUp(String email, String username, String password) {
                //view.setButtonEnable();
            }

            @Override
            public void signIn(String username, String password) {
                //view.setButtonEnable();
            }
        });
         new AlertDialog.Builder(this).setView(view).show();
    }
}
