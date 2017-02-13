package io.ditt;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class ViewVideoActivity extends Activity {

@Override
   public void onCreate(Bundle savedInstanceState) {
      System.out.println("Launching ViewVideoActivity...");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_vv);

      final String videoFilePath = getIntent().getStringExtra("video.file.path");
      final String videoTitle = getIntent().getStringExtra("video.title");

      final VideoView videoView = (VideoView)findViewById(R.id.video_view);
      final ProgressDialog progressDialog = new ProgressDialog(ViewVideoActivity.this);

      System.out.println("Playing video: " + videoFilePath);
      progressDialog.setTitle("Playing: " + videoTitle);
      progressDialog.setMessage("Buffering...");
      progressDialog.setIndeterminate(false);
      progressDialog.setCancelable(false);
      progressDialog.show();

      try {
         System.out.println("Adding media controller...");
         MediaController mediaController = new MediaController(ViewVideoActivity.this);
         mediaController.setAnchorView(videoView);
         videoView.setMediaController(mediaController);
         videoView.setVideoPath(videoFilePath);
      }
      catch(Exception e) {
         Log.e("Error", e.getMessage());
      }

      System.out.println("Waiting for video play prep completion...");
      videoView.requestFocus();
      videoView.setOnPreparedListener(new OnPreparedListener() {
         public void onPrepared(MediaPlayer mediaPlayer) {
            System.out.println("Adding listener to exit on completion...");
            videoView.setOnCompletionListener(new OnCompletionListener() {

               @Override
               public void onCompletion(MediaPlayer mp) {
                  ViewVideoActivity.this.finish();
               }
            });
            System.out.println("Playing video...");
            getWindow().setTitle(videoTitle);
            progressDialog.dismiss();
            videoView.start();
         }
      });
   }       
}
