package org.gdgph.watchface;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class WearableConfigAdapter extends WearableListView.Adapter {

    private final Context mContext;
    private final List<WearableConfiguration> mConfigurations;

    public WearableConfigAdapter(Context context, List<WearableConfiguration> mConfigurations) {
        mContext = context;
        this.mConfigurations = mConfigurations;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WearableListView.ViewHolder(new WearableListItemLayout(mContext));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        WearableListItemLayout layout = (WearableListItemLayout) holder.itemView;

        WearableConfiguration configuration = mConfigurations.get(position);

        TextView nameTextView = (TextView) layout.findViewById(R.id.setting_text_view);
        nameTextView.setText(configuration.getTitle());

        CircledImageView circleImage = (CircledImageView) layout.findViewById(R.id.setting_circle);
        circleImage.setImageResource(configuration.getIcon());

        ;
    }

    @Override
    public int getItemCount() {
        return mConfigurations.size();
    }
}