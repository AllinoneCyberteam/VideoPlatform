package cyberteam.videoplatform.addNewCourse;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cyberteam.videoplatform.R;

public class SelectCourseName extends AppCompatActivity implements View.OnClickListener {
    public static final String DatabaseLink = "https://videoaplication-application.firebaseio.com";
    private static final int PICK_REQUEST = 1;
    private ArrayList<String> CourseNameList = new ArrayList<>();
    private ArrayList<VideoNameUri> videoList = new ArrayList<>();
    private Uri PhotoUri;

    private Button uploadCourse;
    private EditText CourseName;
    private ImageView SelectedIcon;
    private ListView VideoList;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_course_name);

        Button checkCourseName = findViewById(R.id.CheckCourseName);
        Button pickIcon = findViewById(R.id.PickIcon);
        Button selectVideo = findViewById(R.id.SelectVideo);
        uploadCourse = findViewById(R.id.UploadCourse);
        CourseName = findViewById(R.id.CourseName);
        SelectedIcon = findViewById(R.id.SelectedIcon);
        VideoList = findViewById(R.id.VideoList);
        mProgressBar = findViewById(R.id.progressBar);

        checkCourseName.setOnClickListener(this);
        pickIcon.setOnClickListener(this);
        selectVideo.setOnClickListener(this);
        uploadCourse.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.CheckCourseName: {
                if (checkCourseName(CourseName.getText().toString())) {
                    findViewById(R.id.IMAGE_PICK_LAY).setVisibility(View.VISIBLE);
                    findViewById(R.id.CheckCourseName).setVisibility(View.GONE);
                }
            }
            break;
            case R.id.PickIcon: {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_REQUEST);
            }
            break;
            case R.id.SelectVideo: {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_REQUEST);
            }
            break;
            case R.id.UploadCourse: {
                Upload();
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

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            String fileName = getFileName(data.getData());
            if (!fileName.substring(fileName.indexOf(".") + 1).equals("mp4")) {
                PhotoUri = data.getData();
                Picasso.get().load(PhotoUri).noFade().into(SelectedIcon);
                SelectedIcon.setVisibility(View.VISIBLE);
                findViewById(R.id.VIDEO_PICK_LAY).setVisibility(View.VISIBLE);
            }
            if (fileName.substring(fileName.indexOf(".") + 1).equals("mp4")) {
                Uri videoUri = data.getData();
                String VideoName = getFileName(videoUri);
                VideoNameUri videoNameUri = new VideoNameUri();
                videoNameUri.setVideoName(VideoName);
                videoNameUri.setVideoUri(videoUri);
                videoList.add(videoNameUri);
                UpdateList(videoList);
                uploadCourse.setVisibility(View.VISIBLE);
            }
        }
    }

    private void UpdateList(ArrayList<VideoNameUri> videoList) {
        ArrayList<String> arrayList = new ArrayList<>();
        VideoNameUri videoNameUri;
        for (int i = 0; i < videoList.size(); i++) {
            videoNameUri = videoList.get(i);
            arrayList.add(videoNameUri.getVideoName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectCourseName.this, android.R.layout.simple_list_item_1, arrayList);
        VideoList.setAdapter(adapter);
    }

    private void Upload() {
        Upload(PhotoUri);
    }

    private void Upload(Uri uri) {
        FirebaseStorage.getInstance().getReference("CourseIcons").child(CourseName.getText().toString() + "Icon.jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Upload(videoList, 0);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SelectCourseName.this, "Task Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Upload(final ArrayList<VideoNameUri> videoList, final int Index) {
        VideoNameUri videoNameUri;
        if (Index < videoList.size()) {
            videoNameUri = videoList.get(Index);
            FirebaseStorage.getInstance().getReference("CourseVideos")
                    .child(CourseName.getText().toString() + "-Video-" + Integer.toString(Index + 1))
                    .putFile(videoNameUri.getVideoUri())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Upload(videoList, Index + 1);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Upload(videoList, Index);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    mProgressBar.setProgress((int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()));
                }
            });
        }
    }

    class VideoNameUri {
        private String VideoName;
        private Uri VideoUri;

        String getVideoName() {
            return VideoName;
        }

        void setVideoName(String videoName) {
            VideoName = videoName;
        }

        Uri getVideoUri() {
            return VideoUri;
        }

        void setVideoUri(Uri videoUri) {
            VideoUri = videoUri;
        }
    }
}
