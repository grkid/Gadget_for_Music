package com.czy.mp002;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import static android.content.ContentValues.TAG;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getDataDirectory;
import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by CZY on 2019/4/16.
 */

public class SoundRecorder {
    private static MediaRecorder recorder;

    private static Context mContext=null;
    public static File file;

    public static void setContext(Context a)
    {
        mContext=a;
    }

    public static void startRecord()
    {
        try
        {
            recorder=new MediaRecorder();
            recorder.reset();

            file=new File(mContext.getObbDir()+"/1.aac");

            file.mkdir();
            file.delete();
            file.createNewFile();

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS );
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(file);
            //recorder.setMaxDuration(5000);

            //recorder.prepare();
            recorder.prepare();
            recorder.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void StopRecord()
    {
        try {
            recorder.stop();
            recorder.reset();
            recorder.release();
            Log.d(TAG, file.getAbsolutePath());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}