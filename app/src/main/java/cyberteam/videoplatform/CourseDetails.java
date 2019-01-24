package cyberteam.videoplatform;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashMap;
import java.util.Map;

import cyberteam.videoplatform.login.Login;

public class CourseDetails extends AppCompatActivity implements View.OnClickListener {
    public static final String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    private ImageView courseIcon;
    private Button subscribe;
    private TextView Date_Added;
    private TextView Description_Note;
    private TextView AuthorName;
    private ListView VideoList;
    private ConstraintLayout DetailsLay;
    private ConstraintLayout ContentsLay;
    private ConstraintLayout RootLay;
    private ArrayList<String> ListArray = new ArrayList<>();
    private Map<String, String> mMap = new HashMap<>();
    private FirebaseAuth mAuth;
    private String CourseName;
    private String Author;
    private String DateAdded;
    private String Description;
    private String IconLink;
    private String VideoCount;
    private String VideoName1;
    private boolean Downloading = false;
    private boolean subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        courseIcon = findViewById(R.id.courseIcon);
        VideoList = findViewById(R.id.VideoList);
        TextView courseName = findViewById(R.id.courseName);
        Date_Added = findViewById(R.id.DateAdded);
        Description_Note = findViewById(R.id.Description);
        AuthorName = findViewById(R.id.AuthorName);
        Button details = findViewById(R.id.Desc);
        Button contents = findViewById(R.id.CourseContents);
        subscribe = findViewById(R.id.Subscribe);
        DetailsLay = findViewById(R.id.Details);
        ContentsLay = findViewById(R.id.Contents);
        RootLay = findViewById(R.id.RootLay);
        RootLay.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null) {
            CourseName = getIntent().getExtras().getString("CourseName");
            courseName.setText(CourseName);
            getCourseData();
            checkSubscription();
        }

        VideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (subscription) {
                    if (!Downloading) {
                        VideoName1 = CourseName + "-Video-" + Integer.toString(position + 1) + ".MP4";
                        if (mAuth.getCurrentUser() != null) {
                            DownloadVideo(VideoName1);
                        }
                    } else
                        Toast.makeText(CourseDetails.this, "Downloading...\nPlease wait", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(CourseDetails.this, "Subscription needed", Toast.LENGTH_SHORT).show();
            }
        });

        details.setOnClickListener(this);
        contents.setOnClickListener(this);
        subscribe.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.Desc: {
                DetailsLay.setVisibility(View.VISIBLE);
                ContentsLay.setVisibility(View.GONE);
            }
            break;
            case R.id.CourseContents: {
                DetailsLay.setVisibility(View.GONE);
                ContentsLay.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.Subscribe: {
                if (mAuth.getCurrentUser() != null)
                    Subscribe();
                else
                    startActivity(new Intent(CourseDetails.this, Login.class));
            }
            break;
        }
    }

    /**
     * Subscription Process
     */
    private void Subscribe() {
        onSubscriptionComplete();
    }

    private void checkSubscription() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance(DatabaseLink).getReference("users").child(mAuth.getCurrentUser().getUid()).child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (CourseName.equals(ds.getValue(String.class))) {
                            subscribe.setVisibility(View.GONE);
                            subscription = true;
                        }
                        subscription = false;
                        RootLay.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CourseDetails.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onSubscriptionComplete() {
        if (mAuth.getCurrentUser() != null) {
            FirebaseDatabase.getInstance(DatabaseLink).getReference("users").child(mAuth.getCurrentUser().getUid()).child("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String value = ds.getValue(String.class);
                        if (ds.getKey() != null && value != null) {
                            mMap.put(ds.getKey(), value);
                        }
                    }

                    mMap.put(Long.toString(dataSnapshot.getChildrenCount() + 1), CourseName);
                    FirebaseDatabase.getInstance(DatabaseLink).getReference("users").child(mAuth.getCurrentUser().getUid()).child("Courses").setValue(mMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            subscribe.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(CourseDetails.this, "Subscription Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void DownloadVideo(String courseName) {
        FirebaseStorage.getInstance().getReference("CourseVideos").child(courseName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setTitle(VideoName1);
                request.setDescription("File is being downloaded");


                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, VideoName1);

                DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                manager.enqueue(request);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CourseDetails.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCourseData() {
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

    private void generateVideoList(String courseName, int size) {
        for (int i = 0; i < size; i++) {
            ListArray.add(courseName + "-Video-" + Integer.toString(i + 1) + ".mp4");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(CourseDetails.this, android.R.layout.simple_list_item_1, ListArray);
        VideoList.setAdapter(arrayAdapter);
    }
}
