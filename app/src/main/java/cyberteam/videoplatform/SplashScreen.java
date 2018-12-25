package cyberteam.videoplatform;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cyberteam.videoplatform.login.Login;

public class SplashScreen extends AppCompatActivity {
    int splashTime = 3000;
    Handler mHandler = new Handler();
    String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    FirebaseAuth mAuth;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null)
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                }
            }, splashTime);
    }

    @Override
    protected void onStart() {
        if (mAuth.getCurrentUser() != null) {
            uid = mAuth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance(DatabaseLink)
                    .getReference("users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String UserName = dataSnapshot.child(uid).child("UserName").getValue(String.class);
                            Intent intent = new Intent(SplashScreen.this, CategorySelection.class);
                            intent.putExtra("UserName", UserName);
                            startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
        }
        super.onStart();
    }
}
