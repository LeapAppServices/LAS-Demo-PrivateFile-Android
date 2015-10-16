package com.maxleap.demo.privatefile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.maxleap.LogInCallback;
import com.maxleap.MLLog;
import com.maxleap.MLUser;
import com.maxleap.MLUserManager;
import com.maxleap.SignUpCallback;
import com.maxleap.exception.MLException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        MLUser currentUser = MLUser.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, PrivateFileActivity.class));
            finish();
        }

        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    MLLog.t("Please enter both username and password!");
                    return;
                }

                final ProgressDialog progressDialog = createProgressDialog();
                MLUserManager.logInInBackground(username, password, new LogInCallback<MLUser>() {
                    @Override
                    public void done(MLUser user, MLException exception) {
                        progressDialog.dismiss();
                        if (exception != null) {
                            exception.printStackTrace();
                            MLLog.t(exception.getMessage());
                            return;
                        }
                        startActivity(new Intent(LoginActivity.this, PrivateFileActivity.class));
                        finish();
                    }
                });
            }
        });

        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    MLLog.t("Please enter both username and password!");
                    return;
                }
                MLUser user = new MLUser();
                user.setUserName(username);
                user.setPassword(password);
                final ProgressDialog progressDialog = createProgressDialog();
                MLUserManager.signUpInBackground(user, new SignUpCallback() {
                    @Override
                    public void done(MLException exception) {
                        progressDialog.dismiss();
                        if (exception != null) {
                            exception.printStackTrace();
                            MLLog.t(exception.getMessage());
                            return;
                        }
                        startActivity(new Intent(LoginActivity.this, PrivateFileActivity.class));
                        finish();
                    }
                });
            }
        });
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Load...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }
}
