package cyberteam.videoplatform;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import cyberteam.videoplatform.login.Login;

public class SplashScreen extends AppCompatActivity {
    int splashTime = 3000;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Login.class));
            }
        }, splashTime);
    }
}
