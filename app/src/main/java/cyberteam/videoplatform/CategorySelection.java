package cyberteam.videoplatform;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CategorySelection extends AppCompatActivity implements View.OnClickListener{

    private TextView t;
    private CardView b;
    private ImageView i1;
    private ImageView i2;
    private ImageView i3;
    private ImageView i4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_selection);

        b=(CardView) findViewById(R.id.logout);
        i1= (ImageView) findViewById(R.id.ent_id);
        i2= (ImageView) findViewById(R.id.info_id);
        i3= (ImageView) findViewById(R.id.news_id);
        i4= (ImageView) findViewById(R.id.tech_id);

        b.setOnClickListener(this);
        i1.setOnClickListener(this);
        i2.setOnClickListener(this);
        i3.setOnClickListener(this);
        i4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v==i1)
        {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        }
        else if(v==i3)
        {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        }
        else if(v==i2)
        {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        }
        else if(v==i4)
        {
            startActivity(new Intent(CategorySelection.this, VideoActivity.class));
        }
     /*  else if(v==b) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this, login.class));
        }*/

    }


}
