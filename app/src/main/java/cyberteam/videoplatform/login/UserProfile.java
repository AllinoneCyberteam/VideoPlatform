package cyberteam.videoplatform.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cyberteam.videoplatform.R;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {
    TextView username;
    TextView email;
    EditText currPassword;
    EditText newPassword;
    EditText cfnPassword;
    Button ChangePassword;
    Button Save;
    FirebaseAuth mAuth;
    String UserName;
    String EmailID;
    String CurPassword;
    String NewPassword;
    String CfnPassword;
    ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);
        initialize();
        ChangePassword.setOnClickListener(this);
        Save.setOnClickListener(this);
    }

    void initialize() {
        username = findViewById(R.id.Username);
        email = findViewById(R.id.EmailId);
        ChangePassword = findViewById(R.id.Change_Password);
        Save = findViewById(R.id.Save);
        currPassword = findViewById(R.id.curPassword);
        newPassword = findViewById(R.id.newPassword);
        cfnPassword = findViewById(R.id.cfnPassword);
        mConstraintLayout = findViewById(R.id.ChangePasswordLayout);
        mConstraintLayout.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        if (getIntent().getExtras() != null) {
            UserName = getIntent().getExtras().getString("UserName");
            username.setText(UserName);
        }
        if (mAuth.getCurrentUser() != null) {
            EmailID = mAuth.getCurrentUser().getEmail();
            email.setText(EmailID);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.Change_Password: {
                mConstraintLayout.setVisibility(View.VISIBLE);
                break;
            }
            case R.id.Save: {
                CurPassword = currPassword.getText().toString();
                NewPassword = newPassword.getText().toString();
                CfnPassword = cfnPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(EmailID, CurPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful() && NewPassword.equals(CfnPassword) && mAuth.getCurrentUser() != null)
                            mAuth.getCurrentUser().updatePassword(CfnPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserProfile.this, "Password Changed", Toast.LENGTH_SHORT).show();
                                        mConstraintLayout.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UserProfile.this, "Password Change Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserProfile.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            }
        }
    }
}
