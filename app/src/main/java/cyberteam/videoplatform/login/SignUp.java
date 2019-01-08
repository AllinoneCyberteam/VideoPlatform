package cyberteam.videoplatform.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    TextView UserNameE;
    TextView showPassword1;
    TextView showPassword2;
    boolean setType1 = true;
    boolean setType2 = true;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseAuth.AuthStateListener mAuthListener;
    String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    Map<String, String> mMap = new HashMap<>();
    ArrayList<String> UserArrayList = new ArrayList<>();

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
        showPassword1.setOnClickListener(this);
        showPassword2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.SignUp: {
                int arraySize;
                boolean avail;
                if (!UserName.getText().toString().equals("")) {
                    arraySize = UserArrayList.size();
                    avail = true;
                    for (int i = 0; i < arraySize; i++) {
                        if (UserArrayList.get(i).equals(UserName.getText().toString())) {
                            avail = false;
                            break;
                        }
                    }

                    if (!avail) {
                        UserNameE.setText(R.string.username_not_available);
                        UserNameE.setTextColor(getResources().getColor(R.color.red));
                        UserNameE.setVisibility(View.VISIBLE);
                    } else {
                        UserNameE.setText(R.string.username_available);
                        UserNameE.setTextColor(getResources().getColor(R.color.green));
                        UserNameE.setVisibility(View.VISIBLE);
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
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUp.this, "Something went wrong, try after some time", Toast.LENGTH_SHORT).show();
                                }
                            });
                        else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString()))
                            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        else if (EmailId.getText().toString().equals(""))
                            Toast.makeText(SignUp.this, "Enter your Email first", Toast.LENGTH_SHORT).show();
                        else if (newPassword.getText().toString().equals(""))
                            Toast.makeText(SignUp.this, "Password Field is empty", Toast.LENGTH_SHORT).show();
                        else if (confirmPassword.getText().toString().equals(""))
                            Toast.makeText(SignUp.this, "Confirm Password Field is empty", Toast.LENGTH_SHORT).show();
                    }
                } else Toast.makeText(SignUp.this, "Pick a Username", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.Login: {
                finish();
                break;
            }
            case R.id.showPassword1: {
                if (setType1) {
                    setType1 = false;
                    newPassword.setTransformationMethod(null);
                    if (newPassword.getText().length() > 0)
                        newPassword.setSelection(newPassword.getText().length());
                    showPassword1.setBackgroundResource(R.drawable.hide_icon);
                } else {
                    setType1 = true;
                    newPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if (newPassword.getText().length() > 0)
                        newPassword.setSelection(newPassword.getText().length());
                    showPassword1.setBackgroundResource(R.drawable.show_icon);
                }
                break;
            }
            case R.id.showPassword2: {
                if (setType2) {
                    setType2 = false;
                    confirmPassword.setTransformationMethod(null);
                    if (confirmPassword.getText().length() > 0)
                        confirmPassword.setSelection(confirmPassword.getText().length());
                    showPassword2.setBackgroundResource(R.drawable.hide_icon);
                } else {
                    setType2 = true;
                    confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    if (confirmPassword.getText().length() > 0)
                        confirmPassword.setSelection(confirmPassword.getText().length());
                    showPassword2.setBackgroundResource(R.drawable.show_icon);
                }
                break;
            }
        }
    }

    void initialize() {
        UserName = findViewById(R.id.DashUserName);
        EmailId = findViewById(R.id.emailId);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        SignUp = findViewById(R.id.SignUp);
        goBack = findViewById(R.id.Login);
        mAuth = FirebaseAuth.getInstance();
        UserNameE = findViewById(R.id.userNameError);
        showPassword1 = findViewById(R.id.showPassword1);
        showPassword2 = findViewById(R.id.showPassword2);
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
    protected void onResume() {
        FirebaseDatabase.getInstance(DatabaseLink)
                .getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            String UserId = ds.getKey();
                            String UserName = "";
                            if (UserId != null)
                                UserName = ds.child("UserName").getValue(String.class);
                            UserArrayList.add(UserName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        super.onResume();

    }
}
