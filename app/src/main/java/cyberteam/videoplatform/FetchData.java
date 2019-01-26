package cyberteam.videoplatform;

import android.net.Uri;

class FetchData {
    private Uri PhotoUri;
    private String DateAdded;
    private String CourseName;
    private String VideoCount;

    Uri getPhotoUri() {
        return PhotoUri;
    }

    void setPhotoUri(Uri photoUri) {
        PhotoUri = photoUri;
    }

    String getDateAdded() {
        return DateAdded;
    }

    void setDateAdded(String dateAdded) {
        DateAdded = dateAdded;
    }

    String getCourseName() {
        return CourseName;
    }

    void setCourseName(String courseName) {
        CourseName = courseName;
    }

    String getVideoCount() {
        return VideoCount;
    }

    void setVideoCount(String videoCount) {
        VideoCount = videoCount;
    }
}