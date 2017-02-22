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

import java.io.IOException;

import android.content.Intent;
import android.content.Context;
import android.content.CursorLoader;
import android.provider.MediaStore;
import android.util.Log;
import android.database.Cursor;

import io.ditt.util.FileOp;
import io.ditt.DittTask;

public class DittActivity extends Activity {
   private View selectedDittTask = null;
   private final int REQUEST_VIDEO_CAPTURE = 1;
   private final String videoStorageDirectory = "/data/data/io.ditt/videos/";
   private DittArrayAdapter dittDisplayAdapter;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      System.out.println("Launching DittActivity...");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ditt);

      dittDisplayAdapter = new DittArrayAdapter(this, DummyData.taskList);  
      final ListView dittTaskList = (ListView)findViewById(R.id.ditt_list);
      dittTaskList.setEmptyView(findViewById(R.id.empty));
      dittTaskList.setAdapter(dittDisplayAdapter);
   }

   protected View getSelectedDittTask() {
      return selectedDittTask;
   }

   public void setSelectedDittTask(View newSelectedDittTask) {
      if(selectedDittTask != null) {
         hideAndUnhighlight(selectedDittTask);

      }

      expandAndHighlight(newSelectedDittTask);
      this.selectedDittTask = newSelectedDittTask;
   }

   private void hideAndUnhighlight(View parentView) {
      TextView oldDescView = (TextView)parentView.findViewById(R.id.ditt_desc);
      oldDescView.setVisibility(View.GONE);

      int unhighlightedTextColor = getResources().getColor(R.color.unselected_list_text);
      oldDescView.setTextColor(unhighlightedTextColor);
      TextView oldTaskNameView = (TextView)parentView.findViewById(R.id.ditt_name);
      oldTaskNameView.setTextColor(unhighlightedTextColor);

      parentView.setBackgroundResource(android.R.color.transparent);
   }      

   private void expandAndHighlight(View parentView) {
      int highlightedTextColor = getResources().getColor(R.color.selected_list_text);
      TextView newDescView = (TextView)parentView.findViewById(R.id.ditt_desc);
      newDescView.setTextColor(highlightedTextColor);
      TextView newTaskNameView = (TextView)parentView.findViewById(R.id.ditt_name);
      newTaskNameView.setTextColor(highlightedTextColor);

      parentView.setBackgroundResource(R.color.row_selected);
      newDescView.setVisibility(View.VISIBLE);
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

   private final View populateView(View rowView, ViewGroup parent, DittTask task) {
      if(rowView == null) {
         rowView = getLayoutInflater().inflate(R.layout.ditt_task, parent, false);
      }
      
      String taskId = Integer.toString(task.id);
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

      return rowView;
   }

   private class DittArrayAdapter extends ArrayAdapter<DittTask> {
      private final Context context;
      private DittTask[] tasks;

      public DittArrayAdapter(Context context, DittTask[] objects) {
         super(context, android.R.id.list, objects);
         System.out.println("Creating DittArrayAdapter...");
         this.context = context;
         this.tasks = objects;
      }

      @Override
      public long getItemId(int position) {
         DittTask item = tasks[position];
         return item.id;
      }

      @Override
      public int getCount() {
         return tasks.length;
      }

      @Override
      public boolean hasStableIds() {
         return true;
      }

      @Override
      public View getView(int index, View convertView, ViewGroup parent) {
         System.out.println("Laying out view index [" + index + "]");

         View rowView = null;
         if(index < tasks.length) {
            rowView = populateView(convertView, parent, tasks[index]);
         }

         return rowView;
      }
   }

   private boolean existsVideoRecording(String taskId) {
      System.out.println("Checking if video recording exists for task [" + taskId + "]");
      return FileOp.fileExists(videoStorageDirectory + taskId + ".mp4"); 
   }
}
