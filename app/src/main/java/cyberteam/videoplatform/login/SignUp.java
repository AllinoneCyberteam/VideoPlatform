package cyberteam.videoplatform.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cyberteam.videoplatform.R;

public class SignUp extends AppCompatActivity {
    EditText EmailId;
    EditText newPassword;
    EditText confirmPassword;
    Button SignUp;
    TextView goBack;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPassword.getText().toString().equals(confirmPassword.getText().toString()))
                    mAuth.createUserWithEmailAndPassword(EmailId.getText().toString(), confirmPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.signOut();
                                        finish();
                                    }
                                }
                            });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void initialize() {
        EmailId = findViewById(R.id.emailId);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        SignUp = findViewById(R.id.SignUp);
        goBack = findViewById(R.id.Login);
        mAuth = FirebaseAuth.getInstance();
    }
}
