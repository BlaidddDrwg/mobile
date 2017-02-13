package io.ditt;

import android.net.Uri;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.IOException;

import android.content.Intent;
import android.view.LayoutInflater;
import android.content.Context;
import android.content.CursorLoader;
import android.provider.MediaStore;
import android.util.Log;
import android.database.Cursor;
import android.graphics.PixelFormat;

import io.ditt.util.FileOp;

public class DittActivity extends Activity {
   private View selectedDittTask = null;
   private final String description = "Placeholder description for any activity to see if it works...";
   private final int REQUEST_VIDEO_CAPTURE = 1;
   private final String videoStorageDirectory = "/data/data/io.ditt/videos/";

   @Override
   public void onCreate(Bundle savedInstanceState) {
      System.out.println("Launching DittActivity...");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ditt);

      DittArrayAdapter dittDisplayAdapter = new DittArrayAdapter(this, fetchTasks());
      final ListView dittTaskList = (ListView)findViewById(R.id.ditt_list);
      dittTaskList.setAdapter(dittDisplayAdapter);
   }

   protected View getSelectedDittTask() {
      return selectedDittTask;
   }

   public void setSelectedDittTask(View newSelectedDittTask) {
      if(selectedDittTask != null) {
         TextView oldDescView = (TextView)selectedDittTask.findViewById(R.id.ditt_desc);
         oldDescView.setVisibility(View.GONE);
      }

      displayViewStructure(newSelectedDittTask, "");
      TextView newDescView = (TextView)newSelectedDittTask.findViewById(R.id.ditt_desc);
      System.out.println("Selected ditt task: " + newDescView.getTag());
      newDescView.setVisibility(View.VISIBLE);

      this.selectedDittTask = newSelectedDittTask;
   }

   /* 
    * Much of the code for recording videos taken with attribution from the android developer site @
    * https://developer.android.com/training/camera/videobasics.html#TaskCaptureIntent
    */
   public void processAction(View actionButton) {
      View topLevelListView = getTopLevelListView(actionButton);
      setSelectedDittTask(topLevelListView);

      if (isRecordButton(actionButton)) {
         recordVideo();
      } else {
         playVideo(getDittTaskIdFrom((Button) actionButton));
      }
   }

   private void setButtonTypeToPlayOrRecord(Button actionButton) {
      String taskId = (String)(actionButton.getTag());

      if(existsVideoRecording(taskId)) {
         actionButton.setText("P");
         System.out.println("Adding task id: " + taskId + " [with play]");
      }
      else {
         actionButton.setText("R");
         System.out.println("Adding task id: " + taskId + " [with record]");
      }
   }

   private View getTopLevelListView(View recordOrPlayButton) {
      return (View)(((View)recordOrPlayButton.getParent()).getParent());
   }

   private boolean isRecordButton(View actionButton) {
      String buttonText = ((Button)actionButton).getText().toString();
      return buttonText.equalsIgnoreCase("R") ? true : false;
   }

   private String getDittTaskIdFrom(Button button) {
      return (String)button.getTag();
   }

   private String getVideoFor(String taskId) {
      return videoStorageDirectory + taskId + ".mp4";
   }

   private void playVideo(String taskId) {
      final String videoFilePath = getVideoFor(taskId);
      Intent viewVideoIntent = new Intent(this, ViewVideoActivity.class);
      viewVideoIntent.putExtra("video.file.path", videoFilePath);
      viewVideoIntent.putExtra("video.title", "Video for ditt task [" + taskId + "]");
      if(viewVideoIntent.resolveActivity(getPackageManager()) != null) {
        startActivity(viewVideoIntent); 
      }
   }

   private void recordVideo() {
      Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      if(takeVideoIntent.resolveActivity(getPackageManager()) != null) {
         startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
      if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
         final Uri videoUri = intent.getData();
         final String videoFilePath = getRealPathFromUri(videoUri);
         final View actionButton = selectedDittTask.findViewById(R.id.action);
         final String taskId = (String)actionButton.getTag();
         System.out.println("Got video: " + videoFilePath + " for task [" + taskId + "]");

         try {
            FileOp.moveFile(videoFilePath, videoStorageDirectory, taskId + ".mp4");

            System.out.println("Setting button type for action..");
            setButtonTypeToPlayOrRecord((Button)actionButton);
            actionButton.invalidate();
         }
         catch(IOException ioe) {
            Log.e("tag", ioe.getMessage()); 
         }
      }
   }

   private String getRealPathFromUri(Uri uri) {
      String[] proj = { MediaStore.Video.Media.DATA };
      CursorLoader cursorLoader = new CursorLoader(this, uri, proj, null, null, null);
      Cursor cursor = cursorLoader.loadInBackground();

      int columnIndex = cursor.getColumnIndexOrThrow(proj[0]);
      cursor.moveToFirst();
      return cursor.getString(columnIndex);
   }

   private void displayViewStructure(View view, String prefix) {
      for(int index = 0; index < ((ViewGroup)view).getChildCount(); index++) {
         View nextChild = ((ViewGroup)view).getChildAt(index);
         System.out.println("-" + nextChild.getTag());

         if(nextChild instanceof ViewGroup) {
            displayViewStructure(nextChild, prefix + " ");
         }
      }
   }

   private DittTask[] fetchTasks() {
      DittTask[] taskList = new DittTask[] {
         new DittTask(1, "Unboxing SKU #113", "Task 1: " + description),
             new DittTask(2,  "Powering up SKU #113", "Task 2: " + description),
             new DittTask(3,  "Action A on SKU #113", "Task 3: " + description),
             new DittTask(3,  "Action B on SKU #113", "Task 4: " + description),
             new DittTask(5,  "Action C on SKU #113", "Task 5: " + description),
             new DittTask(6,  "Action D on SKU #113", "Task 6: " + description),
             new DittTask(7,  "Action E on SKU #113", "Task 7: " + description),
             new DittTask(8,  "Action F on SKU #113", "Task 8: " + description),
             new DittTask(9,  "Action G on SKU #113", "Task 9: " + description),
             new DittTask(10, "Action H on SKU #113", "Task 10: " + description),
             new DittTask(11, "Action I on SKU #113", "Task 11: " + description),
             new DittTask(12, "Action J on SKU #113", "Task 12: " + description),
             new DittTask(13, "Action K on SKU #113", "Task 13: " + description),
             new DittTask(14, "Action L on SKU #113", "Task 14: " + description),
             new DittTask(15, "Action M on SKU #113", "Task 15: " + description),
             new DittTask(16, "Powering down SKU #113", "Task 16: " + description),
      };

      return taskList;
   }

   private class DittArrayAdapter extends ArrayAdapter<DittTask> {
      private final Context context;
      private DittTask[] tasks;

      public DittArrayAdapter(Context context, DittTask[] objects) {
         super(context, R.id.ditt_list, objects);
         this.context = context;
         this.tasks = objects;
      }

      @Override
      public long getItemId(int position) {
         DittTask item = tasks[position];
         return item.id;
      }

      @Override
      public boolean hasStableIds() {
         return true;
      }

      @Override
      public View getView(int index, View convertView, ViewGroup parent) {
         LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

         View rowView = null;
         if(index < tasks.length) {
            final DittTask task = tasks[index];
            final String taskId = Integer.toString(task.id);

            rowView = inflater.inflate(R.layout.ditt_task, parent, false);
            rowView.setTag(taskId);

            TextView dittName = (TextView) rowView.findViewById(R.id.ditt_name);
            dittName.setText(task.name);
            dittName.setTag(taskId);

            TextView dittDesc = (TextView) rowView.findViewById(R.id.ditt_desc);
            dittDesc.setText(task.desc);
            dittDesc.setTag(Integer.valueOf(task.id));
            dittDesc.setVisibility(View.GONE); // Initialize all 'descriptions' to be hidden...

            Button actionButton = (Button) rowView.findViewById(R.id.action);
            actionButton.setTag(taskId);
            setButtonTypeToPlayOrRecord(actionButton);
         }

         return rowView;
      }
   }

   private boolean existsVideoRecording(String taskId) {
      System.out.println("Checking if video recording exists for task [" + taskId + "]");
      return FileOp.fileExists(videoStorageDirectory + taskId + ".mp4"); 
   }

   public static class DittTask {
      public final int id;
      public final String name;
      public final String desc;

      public DittTask(int id, String taskName, String desc) {
         this.id = id;
         this.name = taskName;
         this.desc = desc;
      }

      public String toString() {
         return name;
      }
   }
}
