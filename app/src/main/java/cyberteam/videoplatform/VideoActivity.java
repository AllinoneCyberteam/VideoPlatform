package cyberteam.videoplatform;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    RecyclerView recyclerView;
    TextView CategoryText;
    String Link;
    ArrayList<YouTubeVideos> mYouTubeVideosArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        CategoryText = findViewById(R.id.categoryText);
        if (getIntent().getExtras() != null) {
            Link = getIntent().getExtras().getString(CONSTANTS.LINK_KEY);
            CategoryText.setText(getIntent().getExtras().getString(CONSTANTS.TEXT_KEY));
        }

        recyclerView = findViewById(R.id.recyclerView3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseDatabase.getInstance(CONSTANTS.DatabaseLink).getReference("VideoLinks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child(Link);
                long LinkLimit = ds.getChildrenCount();
                for (long i = 1; i <= LinkLimit; i++) {
                    String link = "Link" + Long.toString(i);
                    String VideoLink = CONSTANTS.LINK_PART1 + ds.child(link).getValue(String.class) + CONSTANTS.LINK_PART2;
                    YouTubeVideos youTube = new YouTubeVideos();
                    youTube.setVideoUrl(VideoLink);
                    mYouTubeVideosArrayList.add(youTube);


                    Log.d(TAG, Link + " : " + CONSTANTS.LINK_PART1 + ds.child(link).getValue(String.class) + CONSTANTS.LINK_PART2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VideoActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        VideoAdapter videoAdapter = new VideoAdapter(mYouTubeVideosArrayList);
        recyclerView.setAdapter(videoAdapter);
    }
}