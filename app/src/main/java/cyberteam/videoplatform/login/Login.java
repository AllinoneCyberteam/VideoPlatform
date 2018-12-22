package cyberteam.videoplatform.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cyberteam.videoplatform.CategorySelection;
import cyberteam.videoplatform.R;

public class Login extends AppCompatActivity {
    Button Login;
    Button forgotPassword;
    EditText userId;
    EditText userPassword;
    TextView newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSuccess();
            }
        });
    }

    private void initialize() {
        Login = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgotpassword);
        userId = findViewById(R.id.loginid);
        userPassword = findViewById(R.id.password);
        newUser = findViewById(R.id.newuser);
    }

    void onSuccess() {
        Intent intent = new Intent(Login.this, CategorySelection.class);
        startActivity(intent);
    }
}
