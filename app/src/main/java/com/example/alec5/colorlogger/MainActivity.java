package com.example.alec5.colorlogger;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private Socket socket = null;
    private TextView status = null;
    private OpenConnection openConnection = new OpenConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.status);
        openConnection.execute();
    }

    public void closeConnection(View view) {
        //add some code to shutdown the connection
    }

    public void refreshConnection(View view) {
        if (openConnection.getStatus() == AsyncTask.Status.FINISHED) {
            new OpenConnection().execute();
        }
    }

    private class OpenConnection extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            status.setText("Attempting Connection");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket = new Socket("192.168.4.1", 23);
            } catch (IOException e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (socket != null && socket.isConnected()) {
                status.setText("Connected");
                status.setTextColor(Color.GREEN);
            }else {
                status.setText("Error Not Connected");
                status.setTextColor(Color.RED);
            }
        }
    }
}
