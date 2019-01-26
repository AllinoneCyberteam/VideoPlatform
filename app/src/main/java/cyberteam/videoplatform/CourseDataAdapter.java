package cyberteam.videoplatform;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CourseDataAdapter extends ArrayAdapter {
    private final int layoutResource;
    private final LayoutInflater mLayoutInflater;
    private List<FetchData> Data;
    private Uri PhotoUri;

    CourseDataAdapter(Context context, List<FetchData> data) {
        super(context, R.layout.dashbord_listhiew_hiewholder);
        this.layoutResource = R.layout.dashbord_listhiew_hiewholder;
        mLayoutInflater = LayoutInflater.from(context);
        this.Data = data;
    }

    @Override
    public int getCount() {
        return Data.size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        FetchData currentData = Data.get(position);

        String VideoCountText = "Video count: " + currentData.getVideoCount();
        viewHolder.CourseName.setText(currentData.getCourseName());
        viewHolder.DateAdded.setText(currentData.getDateAdded());
        viewHolder.VideoCount.setText(VideoCountText);
        Picasso.get().load(currentData.getPhotoUri()).noFade().into(viewHolder.CourseIcon);

        return convertView;
    }

    private class ViewHolder {
        final TextView CourseName;
        final TextView DateAdded;
        final TextView VideoCount;
        final ImageView CourseIcon;

        ViewHolder(View v) {
            this.CourseName = v.findViewById(R.id.CourseName);
            this.DateAdded = v.findViewById(R.id.DateAdded);
            this.VideoCount = v.findViewById(R.id.VideoCount);
            this.CourseIcon = v.findViewById(R.id.CourseIcon);
        }
    }
}
