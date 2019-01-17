package cyberteam.videoplatform;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePhoto extends AppCompatActivity implements View.OnClickListener {
    public static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private String result = null;
    private Button Upload;
    private Button Cancel;
    private ImageView SelectedImage;
    private EditText PhotoLink;
    private ProgressBar UploadProgress;
    private Uri PhotoUri;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadprofilephoto);
        Button selectPhoto = findViewById(R.id.selectPhoto);
        Upload = findViewById(R.id.uploadPhoto);
        Upload.setVisibility(View.INVISIBLE);
        Cancel = findViewById(R.id.cancel);
        Cancel.setVisibility(View.INVISIBLE);
        UploadProgress = findViewById(R.id.UploadProgress);
        ImageView back = findViewById(R.id.back);
        SelectedImage = findViewById(R.id.SelectedPhoto);
        PhotoLink = findViewById(R.id.PhotoLink);

        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference("ProfilePhotos");

        selectPhoto.setOnClickListener(this);
        Cancel.setOnClickListener(this);
        back.setOnClickListener(this);
        Upload.setOnClickListener(this);

        currentProfilePhoto();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.selectPhoto: {
                OpenFileChooser();
            }
            break;
            case R.id.uploadPhoto:
                UploadImage();
                break;
            case R.id.cancel:
                PhotoLink.setText("");
                currentProfilePhoto();
                Upload.setVisibility(View.INVISIBLE);
                Cancel.setVisibility(View.INVISIBLE);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void UploadImage() {
        StorageReference reference;

        if (mAuth.getCurrentUser() != null && !profilePhotoAdded()) {
            reference = mStorageReference.child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(PhotoUri));
            reference.putFile(PhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            UploadProgress.setProgress(0);
                        }
                    }, 3000);
                    Toast.makeText(UploadProfilePhoto.this, "Profile Photo Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePhoto.this, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    UploadProgress.setProgress((int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()));
                }
            });
        } else if (mAuth.getCurrentUser() != null && profilePhotoAdded()) {
            if (mAuth.getCurrentUser() != null)
                mStorageReference.child(mAuth.getCurrentUser().getUid() + ".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
            reference = mStorageReference.child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(PhotoUri));
            reference.putFile(PhotoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePhoto.this, "Failure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    UploadProgress.setProgress((int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()));
                }
            });
        }
    }

    private void OpenFileChooser() {
        PhotoLink.setText("");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            PhotoUri = data.getData();
            PhotoLink.setText(PhotoUri.getPath());
            Picasso.get().load(PhotoUri).into(SelectedImage);
            Upload.setVisibility(View.VISIBLE);
            Cancel.setVisibility(View.VISIBLE);
        }
    }

    boolean profilePhotoAdded() {
        if (mAuth.getCurrentUser() != null)
            mStorageReference.child(mAuth.getCurrentUser().getUid() + "." + getFileExtension(PhotoUri)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    result = uri.toString();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        return result != null;
    }

    void currentProfilePhoto() {
        if (mAuth.getCurrentUser() != null)
            mStorageReference.child(mAuth.getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri.toString()).fit().centerCrop().into(SelectedImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
    }
}
