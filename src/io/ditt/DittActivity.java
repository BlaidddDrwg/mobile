package io.ditt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.HashMap;
import android.content.Context;

public class DittActivity extends Activity {
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      System.out.println("Launching DittActivity...");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ditt);

      final ListView dittTaskList = (ListView)findViewById(R.id.ditt_list);
      fetchAndPopulateTasksInList(dittTaskList);
   }

   private void fetchAndPopulateTasksInList(ListView taskList) {
      DittTask[] tasks = fetchTasks();

      // Create an adapter to supply the data to the list view...
      final DittArrayAdapter dittDisplayAdapter = new DittArrayAdapter(this, R.layout.ditt_task, R.id.ditt_name, tasks);

      taskList.setAdapter(dittDisplayAdapter);
   }

   private DittTask[] fetchTasks() {
      DittTask[] taskList = new DittTask[] {
         new DittTask("1", "Unboxing SKU #113", TaskStatus.Incomplete),
             new DittTask("2", "Powering up SKU #113", TaskStatus.Incomplete),
      };

      return taskList;
   }

   private class DittArrayAdapter extends ArrayAdapter<DittTask> {
      HashMap<DittTask, Integer> mIdMap = new HashMap<DittTask, Integer>();

      public DittArrayAdapter(Context context, int resource, int textViewResourceId, DittTask[] objects) {
         super(context, resource, textViewResourceId, objects);
         for(int i = 0; i < objects.length; i++) {
            mIdMap.put(objects[i], i);
         }
      }

      @Override
      public long getItemId(int position) {
         DittTask item = getItem(position);
         return mIdMap.get(item);
      }

      @Override
      public boolean hasStableIds() {
         return true;
      }
   }

   public static class DittTask {
      public final String id;
      public final String taskName;
      public final TaskStatus taskStatus;

      public DittTask(String id, String taskName, TaskStatus taskStatus) {
         this.id = id;
         this.taskName = taskName;
         this.taskStatus = taskStatus;
      }
   }

   public enum TaskStatus {
      Complete, Incomplete;
   }
}
