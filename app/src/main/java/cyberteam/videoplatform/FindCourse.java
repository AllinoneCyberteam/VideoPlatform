package cyberteam.videoplatform;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindCourse extends AppCompatActivity {
    private EditText SearchText;
    private ListView SearchFilter;
    private ArrayList<String> mCourseData = new ArrayList<>();
    private ArrayList<String> MatchedCase = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_course);
        SearchText = findViewById(R.id.SearchText);
        SearchFilter = findViewById(R.id.SearchFilter);

        LoadCourseData();

        SearchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                MatchedCase.clear();

                for (int i = 0; i < mCourseData.size(); i++) {
                    if (mCourseData.get(i).contains(SearchText.getText().toString())) {
                        MatchedCase.add(mCourseData.get(i));
                        ArrayAdapter<String> courseDataAdapter = new ArrayAdapter<>(FindCourse.this, android.R.layout.simple_list_item_1, MatchedCase);
                        SearchFilter.setAdapter(courseDataAdapter);
                    }
                }

                return true;
            }
        });

        SearchFilter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindCourse.this, CourseDetails.class);
                intent.putExtra("CourseName", MatchedCase.get(position));
                startActivity(intent);
            }
        });
    }

    private void LoadCourseData() {
        FirebaseDatabase.getInstance().getReference("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey() != null) {
                        mCourseData.add(ds.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FindCourse.this, "Something went wrong, Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
