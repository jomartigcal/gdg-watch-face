package org.gdgph.watchface;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BackgroundConfigActivity extends Activity {

    private WearableListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearable_configuration);

        final TextView headerText = (TextView) findViewById(R.id.settings_header);

        mListView = (WearableListView) findViewById(R.id.settings_list);
        mListView.addOnScrollListener(new WearableListView.OnScrollListener() {
            @Override
            public void onScroll(int i) {


            }

            @Override
            public void onAbsoluteScrollChange(int scroll) {
                float translation = Math.min(-scroll, 0);
                headerText.setTranslationY(translation);
            }

            @Override
            public void onScrollStateChanged(int i) {

            }

            @Override
            public void onCentralPositionChanged(int i) {

            }
        });

        loadConfigurations();
    }

    private void loadConfigurations() {
        List<WearableConfiguration> configurationList = new ArrayList<>();
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, "Background"));
        configurationList.add(new WearableConfiguration(R.drawable.ic_date_on, "Date"));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, "Hour Hand"));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, "Minute Hand"));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, "Second Hand"));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, "Hour Marker"));

        WearableConfigAdapter adapter = new WearableConfigAdapter(this, configurationList);
        mListView.setAdapter(adapter);
        mListView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                WearableListItemLayout layout = (WearableListItemLayout) viewHolder.itemView;

                TextView nameTextView = (TextView) layout.findViewById(R.id.setting_text_view);
                String message = nameTextView.getText().toString();
                Toast.makeText(BackgroundConfigActivity.this,
                        message,
                        Toast.LENGTH_SHORT).show();
                switch ((viewHolder.getPosition())) {
                    case 0:
                        break;
                }
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });
    }
}
