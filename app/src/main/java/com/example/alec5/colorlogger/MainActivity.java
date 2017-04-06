package com.example.alec5.colorlogger;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    //Network objects
    private Socket socket = null;
    private BufferedReader os = null;
    private PrintStream ps = null;
    private StringBuffer outputBuffer = new StringBuffer();

    private TextView status = null;
    private TextView output = null;
    private OpenConnection openConnection = new OpenConnection();
    private Handler mHandler;
    private Thread t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = (TextView) findViewById(R.id.status);
        output = (TextView) findViewById(R.id.outputBox);
        openConnection.execute();

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message inputMessage) {
                outputBuffer.append((String) inputMessage.obj);
                outputBuffer.append("\n");
                output.setText(outputBuffer);
            }
        };


    }

    public void closeConnection(View view) {
        //add some code to shutdown the connection
        new CloseConnection().execute();
    }

    public void refreshConnection(View view) {
        if (openConnection.getStatus() == AsyncTask.Status.FINISHED) {
            new OpenConnection().execute();
        }
    }

    //This class opens the connection to the device
    private class OpenConnection extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            status.setTextColor(Color.GRAY);
            status.setText("Attempting Connection");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                socket = new Socket("192.168.4.1", 23);
                os = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ps = new PrintStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            new Thread(new ReadData()).start();
            if (socket != null && socket.isConnected()) {
                status.setText("Connected");
                status.setTextColor(Color.GREEN);
            } else {
                status.setText("Error Not Connected");
                status.setTextColor(Color.RED);
            }
        }
    }
    //This task closes the network connection
    private class CloseConnection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //Send the close command to the device
            if (ps != null) {
                ps.println("/Close/");

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            status.setText("Connection Closed");
            status.setTextColor(Color.GRAY);
        }
    }
    //send a command
    private class SendCommand extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            if (ps != null) {
                for (String s : strings) {
                    ps.println(s);
                }
            }
            return null;
        }
    }
    //This class reads the data coming from the device
    private class ReadData implements Runnable {
        String received;

        @Override
        public void run() {
            try {
                while (os != null && (received = os.readLine()) != null) {
                    Message m = mHandler.obtainMessage(0, received);
                    m.sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
