package cyberteam.videoplatform;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.List;


public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private List<YouTubeVideos> youtubeVideoList;

    VideoAdapter(List<YouTubeVideos> youtubeVideoList) {
        this.youtubeVideoList = youtubeVideoList;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_view, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        holder.videoWeb.loadData(youtubeVideoList.get(position).getVideoUrl(), "text/html", "utf-8");
    }

    @Override
    public int getItemCount() {
        return youtubeVideoList.size();
    }


    class VideoViewHolder extends RecyclerView.ViewHolder {
        WebView videoWeb;

        @SuppressLint("SetJavaScriptEnabled")
        VideoViewHolder(View itemView) {
            super(itemView);
            videoWeb = itemView.findViewById(R.id.videoWebView);
            videoWeb.getSettings().setJavaScriptEnabled(true);
            videoWeb.setWebChromeClient(new WebChromeClient() {
            });
        }
    }

}

