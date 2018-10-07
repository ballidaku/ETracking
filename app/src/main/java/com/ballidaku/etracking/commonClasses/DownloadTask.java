package com.ballidaku.etracking.commonClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;

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
    private OutputStream output = null;
    File file;
    ProgressDialog progressDialog;
    Interfaces.OnDownloadCompleted onDownloadCompleted;

    public DownloadTask(Context context, File file, Interfaces.OnDownloadCompleted onDownloadCompleted)
    {
        this.context = context;
        this.file=file;
        this.onDownloadCompleted=onDownloadCompleted;

        progressDialog= new ProgressDialog(context);
        progressDialog.setMax(100);
        progressDialog.setMessage("Please wait....");
        progressDialog.setTitle("Video Downloading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        mWakeLock.acquire();

        progressDialog.show();

//        CommonDialogs.getInstance().showProgressDialog(context,"Downloading please wait...");
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

            output = new FileOutputStream(file);

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
    protected void onProgressUpdate(Integer... progress)
    {
        super.onProgressUpdate(progress);
        progressDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result)
    {

        mWakeLock.release();
        progressDialog.dismiss();
        if (result != null)
        {
            progressDialog.show();

        }
        else
        {
            CommonMethods.getInstance().showToast(context, "File downloaded");
            onDownloadCompleted.onCompleted();

        }

    }
}