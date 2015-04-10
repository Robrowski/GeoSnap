package edu.cs430x.fuschia.geosnap.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.cs430x.fuschia.geosnap.R;
import edu.cs430x.fuschia.geosnap.dummy.DummyContent;

/**
 * Created by Matt on 4/9/2015.
 */
public class DiscoveredAdapter extends RecyclerView.Adapter<DiscoveredAdapter.DiscoveredViewHolder> {

    List<DummyContent.DummyItem> data;
    public DiscoveredAdapter(List<DummyContent.DummyItem> data){
        this.data = data;
    }
    @Override
    public DiscoveredViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.discovered_photo_cardview, viewGroup, false);
        DiscoveredViewHolder pvh = new DiscoveredViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DiscoveredViewHolder discoveredViewHolder, int i) {
        discoveredViewHolder.textView.setText(data.get(i).content);
    }


    @Override
    public int getItemCount() {
        return this.data.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class DiscoveredViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView textView;

        DiscoveredViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_view);
            textView = (TextView)itemView.findViewById(R.id.info_text);
        }
    }
}
