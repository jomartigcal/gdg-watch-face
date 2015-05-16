package org.gdgph.watchface;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ColorConfigActivity extends Activity {
    public static final String CONFIG_HEADER = "org.gdgph.watchface.CONFIG_HEADER";

    private String mHeader;

    private WearableListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearable_configuration);

        final TextView headerText = (TextView) findViewById(R.id.settings_header);
        if (getIntent().getExtras().containsKey(CONFIG_HEADER)) {
            mHeader = getIntent().getStringExtra(CONFIG_HEADER);
            headerText.setText(mHeader);
        } else {
            finish();
        }

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


        displayColorSelections();
    }

    private void displayColorSelections() {
        List<String> colorList = new ArrayList<>();

        if (mHeader.contains("Background")) {
            String[] backgrounds = getResources().getStringArray(R.array.background_selection);
            for (String background : backgrounds) {
                colorList.add(background);
            }
        } else {
            String[] colors = getResources().getStringArray(R.array.color_selection);
            for (String color : colors) {
                colorList.add(color);
            }
        }

        ColorConfigAdapter adapter = new ColorConfigAdapter(this, colorList);
        mListView.setAdapter(adapter);
        mListView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                WearableListItemLayout layout = (WearableListItemLayout) viewHolder.itemView;
//
                TextView nameTextView = (TextView) layout.findViewById(R.id.setting_text_view);
                String message = nameTextView.getText().toString();
                Toast.makeText(ColorConfigActivity.this,
                        message,
                        Toast.LENGTH_SHORT).show();
//                switch ((viewHolder.getPosition())) {
//                    case 0:
//                        break;
//                }
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });
    }
}
