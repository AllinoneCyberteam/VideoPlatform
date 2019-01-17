package cyberteam.videoplatform.addNewCourse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cyberteam.videoplatform.R;

public class SelectCourseName extends AppCompatActivity implements View.OnClickListener {
    public static final int Phase1 = 1;     //  CheckCourse
    public static final int Phase2 = 2;     //  Select Icon
    public static final int Phase3 = 3;     //  Add Videos
    public static final int Phase4 = 4;     //  Upload Videos

    public static final String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    private static final int PICK_IMAGE_REQUEST = 1;
    private ArrayList<String> CourseNameList = new ArrayList<>();
    private ArrayList<VideoNameUri> videoList = new ArrayList<>();
    private TextView VideoList;
    private EditText CourseName;
    private EditText Description;
    private ImageView SelectedImage;
    private ImageView IconUploadStatus;
    private Button CheckCourseName;
    private Button SelectIcon;
    private Button UploadIcon;
    private Button AddVideos;
    private Button Upload;
    private ProgressBar IconUploadWaitProgress;
    private ProgressBar UploadProgress;
    private Uri PhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course_name);

        init();

        CheckCourseName.setOnClickListener(this);
        SelectIcon.setOnClickListener(this);
        UploadIcon.setOnClickListener(this);
        AddVideos.setOnClickListener(this);
        Upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.SelectIcon: {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
            break;
            case R.id.CheckCourseName:
                if (checkCourseName(CourseName.getText().toString())) {
                    CheckCourseName.setVisibility(View.GONE);
                    SelectIcon.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.UploadIcon: {
                IconUploadWaitProgress.setVisibility(View.VISIBLE);
                FirebaseStorage.getInstance().getReference("CourseIcons").child(CourseName.getText().toString() + ".jpg").putFile(PhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d("SelectCourseName", "onSuccess: starts");
                        IconUploadWaitProgress.setVisibility(View.GONE);
                        IconUploadStatus.setVisibility(View.VISIBLE);
                        VideoList.setVisibility(View.VISIBLE);
                        AddVideos.setVisibility(View.VISIBLE);
                        SelectIcon.setVisibility(View.GONE);
                        UploadIcon.setVisibility(View.GONE);
                        Description.setEnabled(false);
                        CourseName.setEnabled(false);
                        Log.d("SelectCourseName", "onSuccess: ends");
                    }
                });
            }
            break;
            case R.id.AddVideos: {

            }
            break;
        }
    }

    private boolean checkCourseName(String CourseName) {
        FirebaseDatabase.getInstance(DatabaseLink)
                .getReference("Courses")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            CourseNameList.add(ds.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        for (int i = 0; i < CourseNameList.size(); i++) {
            if (CourseNameList.get(i).equals(CourseName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            PhotoUri = data.getData();
            Picasso.get().load(PhotoUri).noFade().into(SelectedImage);
            SelectedImage.setVisibility(View.VISIBLE);
            UploadIcon.setVisibility(View.VISIBLE);
        }
    }

    void init() {
        CourseName = findViewById(R.id.CourseName);
        Description = findViewById(R.id.DescriptionNote);
        CheckCourseName = findViewById(R.id.CheckCourseName);
        SelectIcon = findViewById(R.id.SelectIcon);
        SelectIcon.setVisibility(View.GONE);
        SelectedImage = findViewById(R.id.SelectedImage);
        SelectedImage.setVisibility(View.GONE);
        IconUploadStatus = findViewById(R.id.IconUploadStatus);
        IconUploadStatus.setVisibility(View.GONE);
        UploadIcon = findViewById(R.id.UploadIcon);
        UploadIcon.setVisibility(View.GONE);
        VideoList = findViewById(R.id.VideoList);
        VideoList.setVisibility(View.GONE);
        AddVideos = findViewById(R.id.AddVideos);
        AddVideos.setVisibility(View.GONE);
        Upload = findViewById(R.id.UploadVideos);
        Upload.setVisibility(View.GONE);
        IconUploadWaitProgress = findViewById(R.id.IconUploadWaitProgress);
        IconUploadWaitProgress.setVisibility(View.GONE);
        UploadProgress = findViewById(R.id.progressBar);
        UploadProgress.setVisibility(View.GONE);
    }

    class VideoNameUri {
        private String VideoName;
        private Uri VideoUri;

        public String getVideoName() {
            return VideoName;
        }

        public void setVideoName(String videoName) {
            VideoName = videoName;
        }

        public Uri getVideoUri() {
            return VideoUri;
        }

        public void setVideoUri(Uri videoUri) {
            VideoUri = videoUri;
        }
    }
}
