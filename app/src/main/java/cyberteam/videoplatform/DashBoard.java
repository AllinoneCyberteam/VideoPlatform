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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import cyberteam.videoplatform.login.Login;
import cyberteam.videoplatform.login.UserProfile;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String TAG = "DashBoard";
    FirebaseAuth mAuth;
    NavigationView navigationView;
    private ImageView android;
    private ImageView web;
    private ImageView ml;
    private ImageView marketing;
    private String UserName;
    private TextView username;
    private ImageView ProfilePhoto;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorageReference = FirebaseStorage.getInstance().getReference("ProfilePhotos");

        android = findViewById(R.id.android);
        web = findViewById(R.id.web);
        ml = findViewById(R.id.ml);
        marketing = findViewById(R.id.marketing);
        mAuth = FirebaseAuth.getInstance();

        if (getIntent().getExtras() != null) {
            UserName = getIntent().getExtras().getString("UserName");
        }
        android.setOnClickListener(this);
        web.setOnClickListener(this);
        ml.setOnClickListener(this);
        marketing.setOnClickListener(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        Menu menu = navigationView.getMenu();

        MenuItem user = menu.findItem(R.id.sign_out);
        if (mAuth.getCurrentUser() == null) {
            user.setTitle(R.string.log_in);
        } else {
            username = view.findViewById(R.id.DashUserName);
            ProfilePhoto = view.findViewById(R.id.DashProfilePhoto);
            username.setText(UserName);
            user.setTitle(R.string.log_out);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(DashBoard.this, VideoActivity.class);
            if (v == android) {
                intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.ANDROID_LINKS);
                intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.ANDROID_TEXT);
            } else if (v == ml) {
                intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.Machine_Learning_LINKS);
                intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.Machine_Learning_TEXT);
            } else if (v == web) {
                intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.Web_Development_LINKS);
                intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.Web_Development_TEXT);
            } else if (v == marketing) {
                intent.putExtra(CONSTANTS.LINK_KEY, CONSTANTS.Marketing_LINKS);
                intent.putExtra(CONSTANTS.TEXT_KEY, CONSTANTS.Marketing_TEXT);
            }
            startActivity(intent);
        } else
            Toast.makeText(DashBoard.this, "You need to Login first", Toast.LENGTH_SHORT).show();
    }

    void getProfilePhotoUrl() {
        if (mAuth.getCurrentUser() != null)
            mStorageReference.child(mAuth.getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).fit().centerCrop().into(ProfilePhoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    ProfilePhoto.setImageBitmap(null);
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
                    Log.d(TAG, "onNavigationItemSelected: sign out");
                    mAuth.signOut();
                    UpdateUI();
                } else {
                    Log.d(TAG, "onNavigationItemSelected: sign in");
                    intent = new Intent(DashBoard.this, Login.class);
                    startActivity(intent);
                }
                break;
            }
            case R.id.UploadProfilePicture:
                startActivity(new Intent(DashBoard.this, UploadProfilePhoto.class));
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void UpdateUI() {
        username.setText("");

        Menu menu = navigationView.getMenu();
        MenuItem user = menu.findItem(R.id.sign_out);
        user.setTitle(R.string.log_in);
        ProfilePhoto.setImageBitmap(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfilePhotoUrl();
    }
}
