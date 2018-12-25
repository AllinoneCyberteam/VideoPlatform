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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import cyberteam.videoplatform.R;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    EditText UserName;
    EditText EmailId;
    EditText newPassword;
    EditText confirmPassword;
    Button SignUp;
    TextView goBack;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    Map<String, String> mMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = mAuth.getCurrentUser();
                mMap.put("UserName", UserName.getText().toString());
                if (user != null) {
                    mDatabaseReference.child("users").child(user.getUid()).setValue(mMap);
                }
            }
        };

        SignUp.setOnClickListener(this);
        goBack.setOnClickListener(this);
    }

    void initialize() {
        UserName = findViewById(R.id.UserName);
        EmailId = findViewById(R.id.emailId);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        SignUp = findViewById(R.id.SignUp);
        goBack = findViewById(R.id.Login);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(DatabaseLink);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.SignUp: {
                if (!EmailId.getText().toString().equals("") && newPassword.getText().toString().equals(confirmPassword.getText().toString()) && !newPassword.getText().toString().equals("") && !confirmPassword.getText().toString().equals(""))
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
                break;
            }
            case R.id.Login: {
                finish();
                break;
            }
        }
    }
}
