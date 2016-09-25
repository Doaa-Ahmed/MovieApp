package doaaahmed.movie_app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RTAdapter extends RecyclerView.Adapter<RTAdapter.RTHolder>{

    private List<RT> mData = Collections.emptyList();
    private Activity activity;

    @Override
    public RTHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        RTHolder mHolder = new RTHolder(view);
        return mHolder;
    }

    @Override
    public void onBindViewHolder(final RTHolder holder, int position) {
        final RT m = mData.get(position);
        if (m.isTrailer()) {
            holder.title.setText(m.RTtitle);
            if (holder.title.getText().toString().equalsIgnoreCase("trailers")) {
                holder.content.setScaleY((float) 0.00001);
            }
            holder.content.setText(m.content);
            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewTrailer(m.getContent());
                }
            });
        }
        else { // review
            holder.content.setText(m.getContent());
            holder.content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.content.getEllipsize() == TextUtils.TruncateAt.END) {
                        holder.content.setEllipsize(null);
                        holder.content.setMaxLines(100);
                    }
                    else {
                        holder.content.setEllipsize(TextUtils.TruncateAt.END);
                        holder.content.setMaxLines(3);
                    }
                }
            });
            holder.title.setText(m.getRTtitle());
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public RTAdapter(FragmentActivity activity, ArrayList<RT> mData) {
        this.mData = mData;
        this.activity = activity;
    }

    public void updateList(List<RT> data) {
        for (RT m : data) {
            mData.add(m);
        }
        notifyDataSetChanged();
    }

    private void viewTrailer(String id){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            activity.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            activity.startActivity(intent);
        }
    }

    class RTHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView content;

        public RTHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.RT_title_Item);
            content = (TextView) itemView.findViewById(R.id.RT_content_Item);
        }
    }
}