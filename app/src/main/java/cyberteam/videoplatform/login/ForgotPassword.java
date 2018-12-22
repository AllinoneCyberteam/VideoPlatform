package cyberteam.videoplatform.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import cyberteam.videoplatform.R;

public class ForgotPassword extends AppCompatActivity {
    EditText EmailId;
    Button Reset;
    FirebaseAuth mAuth;
    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initialize();

        Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(EmailId.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Snackbar snackbar = Snackbar.make(mConstraintLayout,
                                        "Link to reset your password has been seen to your\nemail Id: " + EmailId.getText().toString(),
                                        Snackbar.LENGTH_INDEFINITE);
                                snackbar.show();
                            }
                        });
            }
        });
    }

    void initialize() {
        EmailId = findViewById(R.id.emailId);
        Reset = findViewById(R.id.rest);
        mConstraintLayout = findViewById(R.id.forgotPasswordLayout);
        mAuth = FirebaseAuth.getInstance();
    }
}
