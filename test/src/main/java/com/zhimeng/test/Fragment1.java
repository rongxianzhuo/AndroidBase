package com.zhimeng.test;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zhimeng.base.base.BaseContext;
import com.zhimeng.base.base.BaseFragment;

/**
 * author:rongxianzhuo create at 2016/8/16
 * email: rongxianzhuo@gmail.com
 */
public class Fragment1 extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_1, container, false);
        rootView.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToActivity.startActivity(Fragment1.this, ToActivity.class, "send", new BaseContext.OnResultListener() {
                    @Override
                    public void onResult(Object o) {
                        Toast.makeText(getActivity(), "fragment get" + o, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return rootView;
    }
}
