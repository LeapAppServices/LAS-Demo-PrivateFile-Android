package as.leap.demo.privatefile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import as.leap.LASLog;
import as.leap.LASUser;
import as.leap.LASUserManager;
import as.leap.callback.LogInCallback;
import as.leap.callback.SignUpCallback;
import as.leap.exception.LASException;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.username_edit_text);
        passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        LASUser currentUser = LASUser.getCurrentUser();
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
                    LASLog.t("Please enter both username and password!");
                    return;
                }

                final ProgressDialog progressDialog = createProgressDialog();
                LASUserManager.logInInBackground(username, password, new LogInCallback<LASUser>() {
                    @Override
                    public void done(LASUser user, LASException exception) {
                        progressDialog.dismiss();
                        if (exception != null) {
                            exception.printStackTrace();
                            LASLog.t(exception.getMessage());
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
                    LASLog.t("Please enter both username and password!");
                    return;
                }
                LASUser user = new LASUser();
                user.setUserName(username);
                user.setPassword(password);
                final ProgressDialog progressDialog = createProgressDialog();
                LASUserManager.signUpInBackground(user, new SignUpCallback() {
                    @Override
                    public void done(LASException exception) {
                        progressDialog.dismiss();
                        if (exception != null) {
                            exception.printStackTrace();
                            LASLog.t(exception.getMessage());
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
