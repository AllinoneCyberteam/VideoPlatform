package cyberteam.videoplatform;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cyberteam.videoplatform.addNewCourse.SelectCourseName;
import cyberteam.videoplatform.login.Login;
import cyberteam.videoplatform.login.UserProfile;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    private NavigationView navigationView;
    private MenuItem ProfilePhotoStatus;
    private TextView username;
    private TextView AccountTypeView;
    private ImageView ProfilePhoto;
    private ListView CourseList;
    private StorageReference mStorageReference;
    private String AccountType;
    private String UserName;
    private ArrayList<DownloadData> mCourseData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoadCourseData();

        mStorageReference = FirebaseStorage.getInstance().getReference("ProfilePhotos");

        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null) {
            UserName = getIntent().getExtras().getString("UserName");
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);

        Menu menu = navigationView.getMenu();
        MenuItem user = menu.findItem(R.id.sign_out);
        ProfilePhotoStatus = menu.findItem(R.id.UploadProfilePicture);
        final MenuItem AddCourses = menu.findItem(R.id.AddCourse);
        MenuItem ChangePassword = menu.findItem(R.id.PasswordChange);
        MenuItem UploadProfilePhoto = menu.findItem(R.id.UploadProfilePicture);
        CourseList = findViewById(R.id.CourseList);

        View view = navigationView.getHeaderView(0);
        username = view.findViewById(R.id.DashUserName);
        AccountTypeView = view.findViewById(R.id.DashAccountType);
        ProfilePhoto = view.findViewById(R.id.DashProfilePhoto);
        if (mAuth.getCurrentUser() == null) {
            user.setTitle(R.string.log_in);
            AddCourses.setVisible(false);
            ChangePassword.setVisible(false);
            UploadProfilePhoto.setVisible(false);
        } else {
            username.setText(UserName);
            ChangePassword.setVisible(true);
            UploadProfilePhoto.setVisible(true);
            FirebaseDatabase.getInstance().getReferenceFromUrl(CONSTANTS.DatabaseLink).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    AccountType = dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("Account Type").getValue(String.class);
                    AccountTypeView.setText(AccountType);
                    if (AccountType.substring(0, 1).equalsIgnoreCase("s"))
                        AddCourses.setVisible(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            user.setTitle(R.string.log_out);
        }
        navigationView.setNavigationItemSelectedListener(this);

        CourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DashBoard.this, CourseDetails.class);
                DownloadData downloadData = mCourseData.get(position);
                intent.putExtra("CourseName", downloadData.getCourseName());
                startActivity(intent);
            }
        });
    }

    void getProfilePhotoUrl() {
        if (mAuth.getCurrentUser() != null)
            mStorageReference.child(mAuth.getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).noFade().fit().centerCrop().into(ProfilePhoto);
                    ProfilePhotoStatus.setTitle(R.string.upload_profile_picture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ProfilePhoto.setImageResource(R.drawable.default_profile_pic);
                    ProfilePhotoStatus.setTitle(R.string.upload_profile_pic1);
                }
            });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finishAffinity();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dash_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.PasswordChange: {
                Intent intent;
                if (mAuth.getCurrentUser() != null) {
                    intent = new Intent(DashBoard.this, UserProfile.class);
                    intent.putExtra("UserName", UserName);
                    startActivity(intent);
                } else
                    Toast.makeText(DashBoard.this, "You need to Login first", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.sign_out: {
                Intent intent;
                if (mAuth.getCurrentUser() != null) {
                    mAuth.signOut();
                    UpdateUI();
                } else {
                    intent = new Intent(DashBoard.this, Login.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.UploadProfilePicture: {
                startActivity(new Intent(DashBoard.this, UploadProfilePhoto.class));
                break;
            }
            case R.id.FindCourses: {
                break;
            }
            case R.id.MyCourses: {
                startActivity(new Intent(DashBoard.this, MyCourses.class));
                break;
            }
            case R.id.AddCourse: {
                Intent intent = new Intent(DashBoard.this, SelectCourseName.class);
                intent.putExtra("UserName", UserName);
                startActivity(intent);
                break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void UpdateUI() {
        username.setText("");
        AccountTypeView.setText("");
        ProfilePhoto.setImageResource(R.drawable.default_profile_pic);
        Menu menu = navigationView.getMenu();
        MenuItem user = menu.findItem(R.id.sign_out);
        MenuItem AddCourses = menu.findItem(R.id.AddCourse);
        MenuItem ChangePassword = menu.findItem(R.id.PasswordChange);
        MenuItem UploadProfilePhoto = menu.findItem(R.id.UploadProfilePicture);
        AddCourses.setVisible(false);
        ChangePassword.setVisible(false);
        UploadProfilePhoto.setVisible(false);
        user.setTitle(R.string.log_in);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfilePhotoUrl();
    }

    private void LoadCourseData() {
        FirebaseDatabase.getInstance().getReference("Courses").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey() != null) {
                        DownloadData downloadData = new DownloadData();
                        downloadData.setCourseName(ds.getKey());
                        downloadData.setDateAdded(ds.child("Date Added").getValue(String.class));
                        downloadData.setVideoCount(ds.child("Video Count").getValue(String.class));
                        downloadData.setPhotoUri(Uri.parse(ds.child("IconLink").getValue(String.class)));
                        mCourseData.add(downloadData);
                    }
                }
                UpdateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DashBoard.this, "Something went wrong, Try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UpdateList() {
        CourseDataAdapter courseDataAdapter = new CourseDataAdapter(DashBoard.this, mCourseData);
        CourseList.setAdapter(courseDataAdapter);
    }
}
