package com.example.jotdown.widget;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {
    private MediaRecorder mediaRecorder;
    private String outputFile;

    public void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        outputFile = String.format("%s%s%s.3gp",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath(),
                File.separator,
                System.currentTimeMillis());
        mediaRecorder.setOutputFile(outputFile);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 停止录制音频
    public String stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        return outputFile;
    }

    // 保存录制的音频文件路径
    public void saveAudioFilePath(String filePath) {
        outputFile = filePath;
        // 保存文件路径，可以使用 SharedPreferences 或其他方式进行存储
    }
}
