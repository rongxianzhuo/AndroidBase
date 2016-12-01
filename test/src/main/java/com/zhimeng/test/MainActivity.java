package com.zhimeng.test;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zhimeng.base.view.NavigationBar;
import com.zhimeng.base.view.NavigationFrameLayout;

public class MainActivity extends AppCompatActivity {

    private Fragment[] fragments = new Fragment[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentSetup();
    }

    private void fragmentSetup() {
        fragments[0] = new Fragment1();
        fragments[1] = new Fragment2();
        NavigationFrameLayout navigationFrameLayout = (NavigationFrameLayout) findViewById(R.id.navigation_frame_layout);
        NavigationBar navigationBar = (NavigationBar) findViewById(R.id.navigation_bar);
        navigationBar.setup(this
                , new int[]{R.string.app_name, R.string.app_name}
                , new int[]{R.drawable.ic_home_black_24dp, R.drawable.ic_home_black_24dp}
                , R.color.common_text, R.color.colorAccent
                , R.color.common_text, R.color.colorAccent);
        navigationFrameLayout.setupWithNavigation(this, R.id.navigation_frame_layout, navigationBar, fragments);
    }
}
