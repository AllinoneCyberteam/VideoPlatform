package cyberteam.videoplatform;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class CategorySelection extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    private TextView logout;
    private ImageView i1;
    private ImageView i2;
    private ImageView i3;
    private ImageView i4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        CardView b = (CardView) findViewById(R.id.logout);
        i1 = (ImageView) findViewById(R.id.ent_id);
        i2 = (ImageView) findViewById(R.id.info_id);
        i3 = (ImageView) findViewById(R.id.news_id);
        i4 = (ImageView) findViewById(R.id.tech_id);
        TextView t = findViewById(R.id.textUserEmail);
        logout = findViewById(R.id.textView5);
        mAuth = FirebaseAuth.getInstance();

        b.setOnClickListener(this);
        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);
        logout.setOnClickListener(this);
        if (mAuth.getCurrentUser() != null && getIntent().getExtras() != null)
            t.setText(getIntent().getExtras().getString("UserName"));
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
        } else if (v == logout) {
            mAuth.signOut();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}
