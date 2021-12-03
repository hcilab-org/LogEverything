package org.hcilab.projects.logeverything.sensor.implementation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hcilab.projects.logeverything.sensor.AbstractSensor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class StillAliveSensor extends AbstractSensor {
	
	private static final long serialVersionUID = 1L;
		
	private long count = -1;

    private final OkHttpClient client = new OkHttpClient();

	public StillAliveSensor() {
		TAG = getClass().getName();
		SENSOR_NAME = "Still Alive";
		FILE_NAME = "stillalive.csv";
        m_FileHeader = "";
	}

	@Override
	public View getSettingsView(Context context) {
		String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		if(deviceId == null) {
			deviceId = "NULL";
		}

		TextView text = new TextView(context);
		text.setText("Device Id: " + deviceId);
		text.setPadding(20, 20, 20, 20);
		return text;
	}

	@Override
	public boolean isAvailable(Context context) {
		return true;
	}

	@Override
	public void start(Context context) {
		super.start(context);
        Long t = System.currentTimeMillis();
		if (!m_isSensorAvailable)
			return;

		count++;
		if(count % 15 != 0) {
			return;
		}
		
		try {
			m_OutputStream.write((t + "\n").getBytes());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		
		if(isNetworkAvailable(context)) {
			//Log.d(TAG, "Network available");
            //TODO:
		}
	}

	@Override
	public void stop() {
		try {
			m_OutputStream.close();
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void post(Context context) {
        String json = "{}";

        String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder()
                .url("http://projects.hcilab.org/tapsnap/logeverything/php/stillalive.php?id=" + deviceId)
                .post(body).build();

        client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				// TODO
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				// TODO
			}
		});
    }
}