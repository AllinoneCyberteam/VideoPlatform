package cyberteam.videoplatform;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

import cyberteam.videoplatform.login.UserProfile;

public class CategorySelection extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    private ImageView android;
    private ImageView web;
    private ImageView ml;
    private ImageView marketing;
    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        android = (ImageView) findViewById(R.id.android);
        web = (ImageView) findViewById(R.id.web);
        ml = (ImageView) findViewById(R.id.ml);
        marketing = (ImageView) findViewById(R.id.marketing);
        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null)
            UserName = getIntent().getExtras().getString("UserName");
        android.setOnClickListener(this);
        web.setOnClickListener(this);
        ml.setOnClickListener(this);
        marketing.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CategorySelection.this, VideoActivity.class);
        if (v == android) {
            intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.ANDROID_LINKS);
            intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.ANDROID_TEXT);
        } else if (v == ml) {
            intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.Machine_Learning_LINKS);
            intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.Machine_Learning_TEXT);
        } else if (v == web) {
            intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.Web_Development_LINKS);
            intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.Web_Development_TEXT);
        } else if (v == marketing) {
            intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.Marketing_LINKS);
            intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.Marketing_TEXT);
        }
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onStop() {
        if (mAuth.getCurrentUser() == null)
            finish();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_selection_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.profile: {
                Intent intent = new Intent(CategorySelection.this, UserProfile.class);
                intent.putExtra("UserName", UserName);
                startActivity(intent);
                break;
            }
            case R.id.Log_out: {
                mAuth.signOut();
                finish();
                break;
            }
        }

        return true;
    }
}
