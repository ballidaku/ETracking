package com.ballidaku.etracking.commonClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, String>
{

    Context context;
    private PowerManager.WakeLock mWakeLock;
    public ProgressDialog mProgressDialog;
    private String fileName;
    private OutputStream output = null;

    private File root = android.os.Environment.getExternalStorageDirectory();
    private String tempraryDirectory;

    File f;

    public DownloadTask(Context context, ProgressDialog mProgressDialog, String fileName, String tempDirect)
    {
        this.context = context;

        this.mProgressDialog = mProgressDialog;
        this.fileName = fileName;
        this.tempraryDirectory = tempDirect;
      //  tempraryDirectory = root.getAbsolutePath() + "/" + context.getString(R.string.app_name) + "/Audios";

    }

    @Override
    protected String doInBackground(String... sUrl)
    {
        InputStream input = null;

        HttpURLConnection connection = null;
        try
        {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
            }

            int fileLength = connection.getContentLength();

            input = connection.getInputStream();

            String tempraryFilePath = tempraryDirectory + "/" + fileName;

            File dir = new File(tempraryDirectory);
            if (!dir.exists())
            {

                dir.mkdirs();
            }
            f = new File(tempraryFilePath);

            output = new FileOutputStream(tempraryFilePath);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1)
            {
                // allow canceling with back button
                if (isCancelled())
                {
                    input.close();
                    return null;
                }
                total += count;

                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        }
        catch (Exception e)
        {
            return e.toString();
        }
        finally
        {
            try
            {
                if (output != null) output.close();
                if (input != null) input.close();
            }
            catch (IOException ignored)
            {
            }

            if (connection != null) connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... progress)
    {
        super.onProgressUpdate(progress);

        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);

    }

    @Override
    protected void onPostExecute(String result)
    {

        mWakeLock.release();
        mProgressDialog.dismiss();
        if (result != null)
        {
            mProgressDialog.show();

        }
        else
        {
            Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

        }

    }
}