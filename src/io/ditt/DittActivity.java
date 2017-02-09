package io.ditt;

import android.net.Uri;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import android.content.Intent;
import android.view.LayoutInflater;
import android.content.Context;
import android.content.CursorLoader;
import android.provider.MediaStore;
import android.util.Log;
import android.database.Cursor;

import io.ditt.util.FileOp;

public class DittActivity extends Activity {
   private static Random random = new Random(System.currentTimeMillis());
   private View selectedDittTask = null;
   private final String description = "Dummy description for any activity to see if it works...";
   private final int REQUEST_VIDEO_CAPTURE = 1;
   private final String videoStorageDirectory = "/data/data/io.ditt/videos/";

   /** Called when the activity is first created. */
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
      selectedDittTask = (View)actionButton.getParent();
      Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      try {
         String taskId = (String)actionButton.getTag();
         System.out.println("Extracted value [" + taskId + "] from (" + taskId + ")");
         if(takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
         }
      }
      catch(ClassCastException cce) {
         Log.e("tag", cce.getMessage());
      }
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
      if(requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
         final Uri videoUri = intent.getData();
         final String videoFilePath = getRealPathFromUri(videoUri);
         final String taskId = (String)selectedDittTask.findViewById(R.id.action).getTag();
         System.out.println("Got video: " + videoFilePath + " for task [" + taskId + "]");

         try {
            FileOp.moveFile(videoFilePath, videoStorageDirectory, taskId + ".mp4");
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
            rowView = inflater.inflate(R.layout.ditt_task, parent, false);
            rowView.setTag(task.id + "");

            TextView dittName = (TextView) rowView.findViewById(R.id.ditt_name);
            dittName.setText(task.name);
            dittName.setTag(task.id + "");

            TextView dittDesc = (TextView) rowView.findViewById(R.id.ditt_desc);
            dittDesc.setText(task.desc);
            dittDesc.setTag(Integer.valueOf(task.id));
            dittDesc.setVisibility(View.GONE); // Initialize all 'descriptions' to be hidden...

            Button actionButton = (Button) rowView.findViewById(R.id.action);
            actionButton.setTag(task.id + "");
            if(existsVideoRecording(task.id)) {
               actionButton.setText("P");
               System.out.println("Adding task: " + task.name + " [with play]");
            }
            else {
               actionButton.setText("R");
               System.out.println("Adding task: " + task.name + " [with record]");
            }
         }

         return rowView;
      }
   }

   public boolean existsVideoRecording(int taskId) {
      return random.nextBoolean();
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
