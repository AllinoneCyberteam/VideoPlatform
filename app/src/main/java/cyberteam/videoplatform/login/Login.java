package cyberteam.videoplatform.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cyberteam.videoplatform.CategorySelection;
import cyberteam.videoplatform.R;

public class Login extends AppCompatActivity implements View.OnClickListener {
    Button Login;
    Button forgotPassword;
    EditText userId;
    EditText userPassword;
    TextView newUser;
    String uid;
    String UserName;
    String EmailId;
    String Password;
    String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    FirebaseAuth mAuth;
    ConstraintLayout mConstraintLayout;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        Login.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        newUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.login: {
                EmailId = userId.getText().toString();
                Password = userPassword.getText().toString();
                if (!EmailId.equals("") && !Password.equals("")) {
                    mAuth.signInWithEmailAndPassword(EmailId, Password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful())
                                        Toast.makeText(Login.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                                    else {
                                        if (mAuth.getCurrentUser() != null) {
                                            uid = mAuth.getCurrentUser().getUid();
                                            FirebaseDatabase.getInstance(DatabaseLink)
                                                    .getReference("users")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            UserName = dataSnapshot.child(uid).child("UserName").getValue(String.class);
                                                            Intent intent = new Intent(Login.this, CategorySelection.class);
                                                            intent.putExtra("UserName", UserName);
                                                            startActivity(intent);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                                            throw databaseError.toException();
                                                        }
                                                    });
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar = Snackbar.make(mConstraintLayout, "Enter valid Credentials".toUpperCase(), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    });
                } else {
                    snackbar = Snackbar.make(view, "Enter Details", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
                break;
            }
            case R.id.forgotpassword: {
                startActivity(new Intent(Login.this, ForgotPassword.class));
                break;
            }
            case R.id.newuser: {
                startActivity(new Intent(Login.this, ForgotPassword.class));
                break;
            }
        }
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
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onStart() {
        if (mAuth.getCurrentUser() != null && getIntent().getExtras() != null) {
            UserName = getIntent().getExtras().getString("UserName");
            Intent intent = new Intent(Login.this, CategorySelection.class);
            intent.putExtra("UserName", UserName);
            startActivity(intent);
        }

        super.onStart();
    }
}
