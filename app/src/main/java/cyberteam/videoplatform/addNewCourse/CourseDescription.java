package cyberteam.videoplatform.addNewCourse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import cyberteam.videoplatform.DashBoard;
import cyberteam.videoplatform.R;

public class CourseDescription extends AppCompatActivity implements View.OnClickListener {
    public static final String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    private TextView CourseName;
    private EditText Description;
    private Button Submit;
    private Button Reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_description);

        init();

        if (getIntent().getExtras() != null) {
            CourseName.setText(getIntent().getExtras().getString("CourseName"));
        }

        Submit.setOnClickListener(this);
        Reset.setOnClickListener(this);
    }

    void init() {
        CourseName = findViewById(R.id.CourseName);
        Description = findViewById(R.id.Description);
        Submit = findViewById(R.id.Submit);
        Reset = findViewById(R.id.rest);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.Submit: {
                UpdateDB();
            }
            break;
            case R.id.Reset: {
                startActivity(new Intent(CourseDescription.this, SelectCourseName.class));
            }
            break;
        }
    }

    void UpdateDB() {
        ArrayList<Map<String, String>> arrayList = null;

        if (getIntent().getExtras() != null)
            arrayList = (ArrayList<Map<String, String>>) getIntent().getExtras().get("Map");

        Map<String, String> map;

        if (arrayList != null) {
            map = arrayList.get(0);
            map.put("Description", Description.getText().toString());
            FirebaseDatabase.getInstance(DatabaseLink).getReference("Courses").child(CourseName.getText().toString()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startActivity(new Intent(CourseDescription.this, DashBoard.class));
                }
            });
        }
    }
}
