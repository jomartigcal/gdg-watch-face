package org.gdgph.watchface;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;

public class WearableConfigurationActivity extends Activity implements DataApi.DataListener {
    public static final String PATH = "/watchface/config";
    public static final String CONFIG_BACKGROUND = "Background";
    public static final String CONFIG_DATE = "Date";
    public static final String CONFIG_HAND_HOUR = "Hour Hand";
    public static final String CONFIG_HAND_MINUTE = "Minute Hand";
    public static final String CONFIG_HAND_SECOND = "Second Hand";
    public static final String CONFIG_HOUR_MARKER = "Hour Marker";

    private static final String TAG = "WearableConfigActivity";
    private static final int REQUEST_COLOR = 0;

    private WearableListView mListView;

    private GoogleApiClient mGoogleApiClient;
    private boolean mDisplayDate = true;

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Log.d(TAG, "onConnected:" + bundle);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.d(TAG, "onConnectionSuspended:" + i);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(TAG, "onConnectionFailed:");
                    }
                })
                .build();

        loadConfigurations();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.DataApi.getDataItems(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DataItemBuffer>() {
                    @Override
                    public void onResult(DataItemBuffer dataItems) {
                        for (DataItem item : dataItems) {
                            updateConfig(item);
                        }

                        dataItems.release();
                    }
                });
    }

    private void updateConfig(DataItem item) {
        if (WearableConfigurationActivity.PATH.equals(item.getUri().getPath())) {
            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
            if (dataMap.containsKey(WearableConfigurationActivity.CONFIG_DATE)) {
                mDisplayDate = dataMap.getBoolean(WearableConfigurationActivity.CONFIG_DATE, true);
                loadConfigurations();
            }
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH);
        if (requestCode == REQUEST_COLOR && resultCode == RESULT_OK) {
            int color = data.getIntExtra(ColorConfigActivity.CONFIG_COLOR, 0);
            String action = data.getStringExtra(ColorConfigActivity.CONFIG_HEADER);
            putDataMapRequest.getDataMap().putInt(action, color);

            PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
            Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
        }
    }

    private void loadConfigurations() {
        List<WearableConfiguration> configurationList = new ArrayList<>();
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, CONFIG_BACKGROUND));
        configurationList.add(new WearableConfiguration(R.drawable.ic_date_on, CONFIG_DATE, mDisplayDate));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, CONFIG_HAND_HOUR));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, CONFIG_HAND_MINUTE));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, CONFIG_HAND_SECOND));
        configurationList.add(new WearableConfiguration(R.drawable.ic_palette, CONFIG_HOUR_MARKER));

        WearableConfigAdapter adapter = new WearableConfigAdapter(this, configurationList);
        mListView.setAdapter(adapter);
        mListView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                WearableListItemLayout layout = (WearableListItemLayout) viewHolder.itemView;

                TextView nameTextView = (TextView) layout.findViewById(R.id.setting_text_view);
                String action = nameTextView.getText().toString();
                if (action.contains("Background") || action.contains("Hand") || action.contains("Marker")) {
                    Intent intent = new Intent(WearableConfigurationActivity.this, ColorConfigActivity.class);
                    intent.putExtra(ColorConfigActivity.CONFIG_HEADER, action);
                    startActivityForResult(intent, REQUEST_COLOR);
                } else if (CONFIG_DATE.equals(action)) {
                    TextView settingTextView = (TextView) layout.findViewById(R.id.subsetting_text_view);
                    CircledImageView circleImage = (CircledImageView) layout.findViewById(R.id.setting_circle);
                    PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH);

                    if (getString(R.string.label_setting_on).equals(settingTextView.getText().toString())) {
                        settingTextView.setText(getString(R.string.label_setting_off));
                        circleImage.setImageResource(R.drawable.ic_date_off);
                        putDataMapRequest.getDataMap().putBoolean(action, false);
                    } else {
                        settingTextView.setText(getString(R.string.label_setting_on));
                        circleImage.setImageResource(R.drawable.ic_date_on);
                        putDataMapRequest.getDataMap().putBoolean(action, true);
                    }
                    PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
                    Wearable.DataApi.putDataItem(mGoogleApiClient, putDataRequest);
                }
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = dataEvent.getDataItem();
                updateConfig(item);
            }
        }

        dataEventBuffer.release();
    }
}
