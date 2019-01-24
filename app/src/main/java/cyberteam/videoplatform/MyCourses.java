package cyberteam.videoplatform;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyCourses extends AppCompatActivity {
    public static final String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    private ArrayList<DownloadData> mCourseData = new ArrayList<>();
    private ArrayList<String> mArrayList = new ArrayList<>();
    private ListView CourseList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_courses);
        CourseList = findViewById(R.id.CourseList);

        mAuth = FirebaseAuth.getInstance();

        Load();
    }

    private void Load() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance(DatabaseLink).getReference("users").child(mAuth.getCurrentUser().getUid()).child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        mArrayList.add(ds.getValue(String.class));
                    }
                    LoadCourseData();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MyCourses.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void LoadCourseData() {
        FirebaseDatabase.getInstance().getReference("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    for (int i = 0; i < mArrayList.size(); i++) {
                        if (ds.getKey() != null && ds.getKey().equals(mArrayList.get(i))) {
                            DownloadData downloadData = new DownloadData();
                            downloadData.setCourseName(ds.getKey());
                            downloadData.setDateAdded(ds.child("Date Added").getValue(String.class));
                            downloadData.setVideoCount(ds.child("Video Count").getValue(String.class));
                            downloadData.setPhotoUri(Uri.parse(ds.child("IconLink").getValue(String.class)));
                            mCourseData.add(downloadData);
                        }
                    }
                }
                UpdateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyCourses.this, "Something went wrong, Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateList() {
        CourseDataAdapter courseDataAdapter = new CourseDataAdapter(MyCourses.this, mCourseData);
        CourseList.setAdapter(courseDataAdapter);
    }
}
