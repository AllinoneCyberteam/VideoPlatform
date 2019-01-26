package cyberteam.videoplatform.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
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

import cyberteam.videoplatform.DashBoard;
import cyberteam.videoplatform.R;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private Button Login;
    private Button forgotPassword;
    private EditText userId;
    private EditText userPassword;
    private TextView newUser;
    private String uid;
    private String UserName;
    private String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    private FirebaseAuth mAuth;
    private ConstraintLayout mConstraintLayout;
    private TextView passShow;
    private boolean setType = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        passShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setType) {
                    setType = false;
                    userPassword.setTransformationMethod(null);
                    if (userPassword.getText().length() > 0)
                        userPassword.setSelection(userPassword.getText().length());
                    passShow.setBackgroundResource(R.drawable.hide_icon);
                } else {
                    setType = true;
                    userPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if (userPassword.getText().length() > 0)
                        userPassword.setSelection(userPassword.getText().length());
                    passShow.setBackgroundResource(R.drawable.show_icon);
                }
            }
        });

        Login.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
        newUser.setOnClickListener(this);

        userPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    login(Login);
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.login: {
                login(view);
                break;
            }
            case R.id.forgotpassword: {
                startActivity(new Intent(Login.this, ForgotPassword.class));
                break;
            }
            case R.id.newuser: {
                startActivity(new Intent(Login.this, SignUp.class));
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
        passShow = findViewById(R.id.textView9);
        mConstraintLayout = findViewById(R.id.ConstraintLayout);
    }

    void login(View view) {
        String emailId = userId.getText().toString();
        String password = userPassword.getText().toString();
        if (!emailId.equals("") && !password.equals("")) {
            mAuth.signInWithEmailAndPassword(emailId, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                                Toast.makeText(Login.this, "Invalid Details", Toast.LENGTH_SHORT).show();
                            else {
                                if (mAuth.getCurrentUser() != null) {
                                    uid = mAuth.getCurrentUser().getUid();
                                    FirebaseDatabase.getInstance(DatabaseLink).getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            UserName = dataSnapshot.child(uid).child("UserName").getValue(String.class);
                                            Intent intent = new Intent(Login.this, DashBoard.class);
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
            Snackbar snackbar = Snackbar.make(view, "Enter Details", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
        }
    }
}
