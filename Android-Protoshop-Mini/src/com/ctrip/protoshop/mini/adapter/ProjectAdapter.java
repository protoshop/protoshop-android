package com.ctrip.protoshop.mini.adapter;

import java.io.File;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctrip.protoshop.mini.R;
import com.ctrip.protoshop.mini.model.ProjectModel;
import com.ctrip.protoshop.mini.util.Util;

public class ProjectAdapter extends MiniAdapter {

    public ProjectAdapter(Context context, List<?> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, Context context) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.project_item_layout, null);
            holder.thumbView = (ImageView) convertView.findViewById(R.id.project_icon_view);
            holder.nameView = (TextView) convertView.findViewById(R.id.project_name_view);
            holder.pageNumView = (TextView) convertView.findViewById(R.id.project_page_num_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProjectModel model = (ProjectModel) getItem(position);

        holder.nameView.setText(model.appName);
        int pageNum = model.scenes == null ? 0 : model.scenes.size();
        holder.pageNumView.setText(pageNum + context.getString(R.string.page_text));
        if (pageNum != 0) {
            String bitmapPath = Util.getLocalProFile(model.appID).getAbsolutePath() + File.separator
                + model.scenes.get(0).background;
            Bitmap bitmap = Util.getBitmap(bitmapPath);
            holder.thumbView.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, context.getResources()
                .getDimensionPixelSize(R.dimen._60), context.getResources().getDimensionPixelSize(R.dimen._60)));
        } else {
            holder.thumbView.setImageResource(R.drawable.ic_launcher);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView thumbView;
        TextView nameView;
        TextView pageNumView;
    }
}
