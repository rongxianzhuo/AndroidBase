package com.zhimeng.base.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhimeng.base.R;

/**
 * author:rongxianzhuo create at 2017/1/10
 * email: rongxianzhuo@gmail.com
 * email: https://github.com/rongxianzhuo
 */
public class LoginView extends LinearLayout {
    
    public interface LoginCallback {
        void signUp(String email, String username, String password);
        void signIn(String username, String password);
    }

    private Context context;
    private LoginCallback callback;
    private boolean isShowingLoginView = false;
    private TextView title;
    private EditText email, username, password, confirm;
    private Button actionButton, changeButton;
    private View emailContainer, confirmContainer;
    private LinearLayout form;

    public LoginView(Context context) {
        super(context);
        init(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.zhimeng_view_login, this, true);
        form = (LinearLayout) findViewById(R.id.form);
        title = (TextView) findViewById(R.id.title);
        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        confirm = (EditText) findViewById(R.id.password_confirm);
        actionButton = (Button) findViewById(R.id.action_button);
        changeButton = (Button) findViewById(R.id.change_button);
        emailContainer = findViewById(R.id.email_container);
        confirmContainer = findViewById(R.id.confirm_container);
        isShowingLoginView = false;
        clickChange();
        changeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickChange();
            }
        });
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                clickAction();
            }
        });
    }

    public LoginView setup(LoginCallback callback) {
        this.callback = callback;
        return this;
    }

    private void clickAction() {
        if (isShowingLoginView) login();
        else register();
    }

    private void clickChange() {
        isShowingLoginView = !isShowingLoginView;
        email.setText("");
        username.setText("");
        password.setText("");
        confirm.setText("");
        if (isShowingLoginView) {
            title.setText(R.string.zhimeng_view_login_login_title);
            form.removeView(emailContainer);
            form.removeView(confirmContainer);
            actionButton.setText(R.string.zhimeng_view_login_login_button);
            changeButton.setText(R.string.zhimeng_view_login_to_register);
        }
        else {
            title.setText(R.string.zhimeng_view_login_register_title);
            form.addView(emailContainer, 1);
            form.addView(confirmContainer, 4);
            actionButton.setText(R.string.zhimeng_view_login_register_button);
            changeButton.setText(R.string.zhimeng_view_login_to_login);
        }
    }

    private void register() {
        String emailText = email.getText().toString().trim();
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String confirmText = confirm.getText().toString().trim();
        if (emailText.equals("")) {
            email.requestFocus();
            email.setError(context.getString(R.string.zhimeng_view_login_email_blank));
            email.setText("");
            return;
        }
        if (usernameText.equals("")) {
            username.requestFocus();
            username.setError(context.getString(R.string.zhimeng_view_login_username_blank));
            username.setText("");
            return;
        }
        if (passwordText.equals("")) {
            password.requestFocus();
            password.setError(context.getString(R.string.zhimeng_view_login_password_too_short));
            return;
        }
        if (!confirmText.equals(passwordText)) {
            confirm.requestFocus();
            confirm.setError(context.getString(R.string.zhimeng_view_login_password_not_match));
            confirm.setText("");
            return;
        }
        actionButton.setClickable(false);
        changeButton.setClickable(false);
        callback.signUp(emailText, usernameText, passwordText);
    }
    
    public void setButtonEnable() {
        actionButton.setClickable(true);
        changeButton.setClickable(true);
    }

    private void login() {
        String usernameText = username.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        if (usernameText.equals("")) {
            username.requestFocus();
            username.setError(context.getString(R.string.zhimeng_view_login_username_blank));
            username.setText("");
            return;
        }
        if (passwordText.equals("")) {
            password.requestFocus();
            password.setError(context.getString(R.string.zhimeng_view_login_password_too_short));
            return;
        }
        actionButton.setClickable(false);
        changeButton.setClickable(false);
        callback.signIn(usernameText, passwordText);
    }
}
