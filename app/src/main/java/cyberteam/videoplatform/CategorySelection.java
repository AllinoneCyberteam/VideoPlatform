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
    private ImageView i1;
    private ImageView i2;
    private ImageView i3;
    private ImageView i4;
    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        i1 = (ImageView) findViewById(R.id.ent_id);
        i2 = (ImageView) findViewById(R.id.info_id);
        i3 = (ImageView) findViewById(R.id.news_id);
        i4 = (ImageView) findViewById(R.id.tech_id);
        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null)
            UserName = getIntent().getExtras().getString("UserName");
        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == i1) {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        } else if (v == i3) {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        } else if (v == i2) {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        } else if (v == i4) {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        }
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
