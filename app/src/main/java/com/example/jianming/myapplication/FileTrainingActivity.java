package com.example.jianming.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.jianming.Utils.EnvArgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class FileTrainingActivity extends Activity implements View.OnClickListener{

    Context self = this;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read_file:
                readFile();
                break;
            case R.id.write_file:
                writeExtFile();
                break;
            case R.id.network:
                network();
                break;
            default:
                break;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_training);

        findViewById(R.id.write_file).setOnClickListener(this);
        findViewById(R.id.read_file).setOnClickListener(this);
        findViewById(R.id.network).setOnClickListener(this);
    }

    private void network() {
        String stringUrl = "http://%serverIP:%serverPort/picDirs/picIndexAjax"
                .replace("%serverIP", EnvArgs.serverIP)
                .replace("%serverPort", EnvArgs.serverPort);
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
            Log.i("network", "No network connection available.");
        }
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            //Log.i("network", s);

            //Log.i("network", "" + s.length());

//            try {
//                JSONArray jsonArray = new JSONArray(s);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    Log.i("network", jsonObject.getString("name") + " " + jsonObject.getString("mtime"));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

            Intent intent = new Intent(self, PicListAcivity.class);
            intent.putExtra("jsonArg", s);
            self.startActivity(intent);
        }
    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        int len = 500;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d("network", "The response is: " + response);
            is = conn.getInputStream();
            return readIt(is, len);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private String readIt(InputStream is, int len) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(is, "UTF-8");
        char[] buffer = new char[len];
        String content = "";
        int readLen;
        do {
            readLen = reader.read(buffer);
            content += new String(buffer).substring(0, readLen);
        } while (readLen == len);
        return content;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void writeExtFile() {
        if (!isExternalStorageWritable()) {
            return;
        }
        File directory = getAlbumStorageDir(this, "file");
        Boolean isDirectory = directory.isDirectory();
        String fileName = directory.getAbsolutePath();
        File file = new File(directory, "newFile.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write("Hello World".getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;

    }


    public File getAlbumStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_DOWNLOADS), albumName);
        if (!file.mkdirs()) {
            Log.e("LOG_TAG", "Directory not created");
        }
        return file;
    }

    private void writeFile() {
        String fileName = "myfile";
        String string = "Hello World";
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = openFileOutput(fileName, Context.MODE_APPEND);
            fileOutputStream.write(string.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFile() {
        String fileName = "myfile";
        FileInputStream fileInputStream;
        try {
            fileInputStream = openFileInput(fileName);
            byte[] buff = new byte[30];
            String fileContent = "";
            int readLen = 0;
            do {
                readLen = fileInputStream.read(buff);
                fileContent += new String(buff).substring(0, readLen);
            } while(readLen == 30);
            Log.i("readFile", fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
