package com.example.jotdown.task;

import android.util.Log;

import java.io.File;

public class DeleteAudioThread extends Thread {
    private final static String TAG = "DeleteAudioThread";

    private String deleteAudioFileName = "";

    public DeleteAudioThread(String deleteAudioFileName) {
        this.deleteAudioFileName = deleteAudioFileName;
    }

    @Override
    public void run() {
        super.run();

        //执行删除音频文件的操作
        File audioFile = new File(deleteAudioFileName);
        if (audioFile.exists()) {
            boolean deleted = audioFile.delete();
            if (deleted) {
                Log.d(TAG, "Audio file deleted successfully");
            } else {
                Log.d(TAG, "Failed to delete audio file");
            }
        } else {
            Log.d(TAG, "Audio file does not exist");
        }
    }
}