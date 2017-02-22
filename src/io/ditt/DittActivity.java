package io.ditt;

import android.net.Uri;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import io.ditt.util.SelectorStates;

public class DittActivity extends ListActivity {
   private View selectedDittTask = null;
   private final int REQUEST_VIDEO_CAPTURE = 1;
   private final String videoStorageDirectory = "/data/data/io.ditt/videos/";
   //private DittArrayAdapter dittDisplayAdapter;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      System.out.println("Launching DittActivity...");
      super.onCreate(savedInstanceState);
      //setContentView(R.layout.empty_list);
      setContentView(R.layout.activity_ditt);

      //dittDisplayAdapter = new DittArrayAdapter(this, DummyData.taskList);  
      //final ListView dittTaskList = (ListView)findViewById(R.id.ditt_list);
      //dittTaskList.setAdapter(dittDisplayAdapter);
      MyListAdapter myListAdapter = new MyListAdapter(DummyData.taskList);
      //MyListAdapter myListAdapter = new MyListAdapter(new DittTask[]{ });
      setListAdapter(myListAdapter);

      System.out.println("Notifying list of data change...");
   }

   protected View getSelectedDittTask() {
      return selectedDittTask;
   }

   public void setSelectedDittTask(View newSelectedDittTask) {
      if(selectedDittTask != null) {
         TextView oldDescView = (TextView)selectedDittTask.findViewById(R.id.ditt_desc);
         System.out.println("Setting background for task[" + oldDescView.getTag() + "] to transparent...");
         oldDescView.setVisibility(View.GONE);
         oldDescView.setTextColor(getResources().getColor(R.color.unselected_list_text));
         TextView oldTaskNameView = (TextView)selectedDittTask.findViewById(R.id.ditt_name);
         oldTaskNameView.setTextColor(getResources().getColor(R.color.unselected_list_text));
         selectedDittTask.setBackgroundResource(android.R.color.transparent);
      }

      displayViewStructure(newSelectedDittTask, "");
      TextView newDescView = (TextView)newSelectedDittTask.findViewById(R.id.ditt_desc);
      newDescView.setVisibility(View.VISIBLE);
      newDescView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(3000).scaleY(1f);
      //newDescView.setHeight(dittDisplayAdapter.getHeight((Integer)newDescView.getTag()));
      int[] drawableStates = newSelectedDittTask.getDrawableState();
      for(int drawableState : drawableStates) {
         System.out.println("Drawable state: " + SelectorStates.translateDrawableState(drawableState));
      }

      newSelectedDittTask.setBackgroundColor(getResources().getColor(R.color.row_selected));
      newDescView.setTextColor(getResources().getColor(R.color.selected_list_text));
      TextView newTaskNameView = (TextView)newSelectedDittTask.findViewById(R.id.ditt_name);
      String defaultColor = String.format("#%06X", (0xFFFFFF & newTaskNameView.getTextColors().getDefaultColor()));
      System.out.println("Setting background for task[" + newDescView.getTag() + "] fg was {#" + defaultColor + "}");
      newTaskNameView.setTextColor(getResources().getColor(R.color.selected_list_text));

      //newDescView.setBackgroundResource(R.color.row_selected);
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

   private class MyListAdapter extends BaseAdapter {
      private final DittTask[] tasks;

      public MyListAdapter(DittTask[] tasks) {
         this.tasks = tasks;
      }

      @Override
      public int getCount() {
         System.out.println("Returning count [" + tasks.length + "]");
         return tasks.length;
      }

      @Override
      public String getItem(int position) {
         System.out.println("Returning item at position [" + position + "]: " + tasks[position].name);
         return tasks[position].name;
      }

      @Override
      public long getItemId(int position) {
         System.out.println("Returning item id at position [" + position + "]: " + tasks[position].id);
         return tasks[position].id;
      }

      @Override
      public View getView(int position, View convertView, ViewGroup parent) {
         System.out.println("Inflating view [" + position + "]");
         return populateView(convertView, parent, tasks[position]);
      }
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
