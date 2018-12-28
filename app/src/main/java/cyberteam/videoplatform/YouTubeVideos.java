package cyberteam.videoplatform;

public class YouTubeVideos {
    private String videoUrl;

    YouTubeVideos() {
    }

    public YouTubeVideos(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    String getVideoUrl() {
        return videoUrl;
    }

    void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}