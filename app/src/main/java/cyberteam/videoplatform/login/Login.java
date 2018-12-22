package cyberteam.videoplatform.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cyberteam.videoplatform.CategorySelection;
import cyberteam.videoplatform.R;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    Button Login;
    Button forgotPassword;
    EditText userId;
    EditText userPassword;
    TextView newUser;
    String EmailId;
    String Password;
    FirebaseAuth mAuth;
    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmailId = userId.getText().toString();
                Password = userPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(EmailId, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful())
                                    startActivity(new Intent(Login.this, CategorySelection.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar snackbar = Snackbar.make(mConstraintLayout, "Enter valid Credentials".toUpperCase(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }
                });
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });
    }

    private void initialize() {
        Login = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgotpassword);
        userId = findViewById(R.id.loginid);
        userPassword = findViewById(R.id.password);
        newUser = findViewById(R.id.newuser);
        mAuth = FirebaseAuth.getInstance();
        mConstraintLayout = findViewById(R.id.constraintLayout);
    }

    @Override
    protected void onStart() {
        if (mAuth.getCurrentUser() != null)
            startActivity(new Intent(Login.this, CategorySelection.class));
        super.onStart();
    }
}
