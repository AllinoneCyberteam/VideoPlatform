package cyberteam.videoplatform;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {
    private String CourseName;
    private String Author;
    private String DateAdded;
    private String Description;
    private String IconLink;
    private String VideoCount;
    private ImageView courseIcon;
    private TextView Date_Added;
    private TextView Description_Note;
    private TextView AuthorName;
    private ListView VideoList;
    private ArrayList<String> mArrayList = new ArrayList<>();
    private boolean Downloading = false;
    private FirebaseAuth mAuth;
    private String VideoName1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        courseIcon = findViewById(R.id.courseIcon);
        VideoList = findViewById(R.id.VideoList);
        TextView courseName = findViewById(R.id.courseName);
        Date_Added = findViewById(R.id.DateAdded);
        Description_Note = findViewById(R.id.Description);
        AuthorName = findViewById(R.id.AuthorName);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null) {
            CourseName = getIntent().getExtras().getString("CourseName");
            courseName.setText(CourseName);
            getCourseData();
        }

        VideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!Downloading) {
                    VideoName1 = CourseName + "-Video-" + Integer.toString(position + 1) + ".MP4";
                    if (mAuth.getCurrentUser() != null) {
                        stream(VideoName1);
                    }
                } else
                    Toast.makeText(VideoActivity.this, "Downloading...\nPlease wait", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void stream(String courseName) {
        FirebaseStorage.getInstance().getReference("CourseVideos").child(courseName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(VideoName1);
                request.setDescription("File is being downloaded");


                //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, VideoName1);

                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VideoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getCourseData() {
        FirebaseDatabase.getInstance().getReference("Courses").child(CourseName).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Author = dataSnapshot.child("Author").getValue(String.class);
                DateAdded = dataSnapshot.child("Date Added").getValue(String.class);
                Description = dataSnapshot.child("Description").getValue(String.class);
                IconLink = dataSnapshot.child("IconLink").getValue(String.class);
                VideoCount = dataSnapshot.child("Video Count").getValue(String.class);
                Picasso.get().load(IconLink).into(courseIcon);
                Description_Note.setText(Description);
                AuthorName.setText(Author);
                Date_Added.setText("Date Added: " + DateAdded);
                generateVideoList(CourseName, Integer.parseInt(VideoCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void generateVideoList(String courseName, int size) {
        for (int i = 0; i < size; i++) {
            mArrayList.add(courseName + "-Video-" + Integer.toString(i + 1) + ".mp4");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(VideoActivity.this, android.R.layout.simple_list_item_1, mArrayList);
        VideoList.setAdapter(arrayAdapter);
    }
}
