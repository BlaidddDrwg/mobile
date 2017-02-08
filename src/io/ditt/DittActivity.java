package io.ditt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.Random;

import android.view.LayoutInflater;
import android.content.Context;

public class DittActivity extends Activity {
   private static Random random = new Random(System.currentTimeMillis());
   private final int[] selectedPosition = new int[]{-1};
   private final String description = "Dummy description for any activity to see if it works...";

   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      System.out.println("Launching DittActivity...");
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ditt);

      DittArrayAdapter dittDisplayAdapter = new DittArrayAdapter(this, fetchTasks());
      final ListView dittTaskList = (ListView)findViewById(R.id.ditt_list);
      dittTaskList.setAdapter(dittDisplayAdapter);
      dittTaskList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            DittActivity.this.selectedPosition[0] = position;
            System.out.println("Task selected: " + position);
         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {
         }
      });
   }

   private DittTask[] fetchTasks() {
      DittTask[] taskList = new DittTask[] {
         new DittTask(1, "Unboxing SKU #113", TaskStatus.Incomplete),
             new DittTask(2,  "Powering up SKU #113", TaskStatus.Incomplete),
             new DittTask(3,  "Action A on SKU #113", TaskStatus.Incomplete),
             new DittTask(4,  "Action B on SKU #113", TaskStatus.Incomplete),
             new DittTask(5,  "Action C on SKU #113", TaskStatus.Incomplete),
             new DittTask(6,  "Action D on SKU #113", TaskStatus.Incomplete),
             new DittTask(7,  "Action E on SKU #113", TaskStatus.Incomplete),
             new DittTask(8,  "Action F on SKU #113", TaskStatus.Incomplete),
             new DittTask(9,  "Action G on SKU #113", TaskStatus.Incomplete),
             new DittTask(10, "Action H on SKU #113", TaskStatus.Incomplete),
             new DittTask(11, "Action I on SKU #113", TaskStatus.Incomplete),
             new DittTask(12, "Action J on SKU #113", TaskStatus.Incomplete),
             new DittTask(13, "Action K on SKU #113", TaskStatus.Incomplete),
             new DittTask(14, "Action L on SKU #113", TaskStatus.Incomplete),
             new DittTask(15, "Action M on SKU #113", TaskStatus.Incomplete),
             new DittTask(16, "Powering down SKU #113", TaskStatus.Incomplete),
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
            DittTask task = tasks[index];
            rowView = inflater.inflate(R.layout.ditt_task, parent, false);
            TextView dittName = (TextView) rowView.findViewById(R.id.ditt_name);
            dittName.setText(task.name);
            TextView dittDesc = (TextView) rowView.findViewById(R.id.ditt_desc);
            //dittDesc.setText(description);
            dittDesc.setVisibility(View.GONE);
            //dittDesc.setHeight(0);
            int taskId = task.id;

            Button actionButton = (Button) rowView.findViewById(R.id.action);
            if(existsVideoRecording(taskId)) {
               actionButton.setText("P");
               System.out.println("Adding task: " + task.name + " [with play]");
            }
            else {
               actionButton.setText("R");
               System.out.println("Adding task: " + task.name + " [with record]");
            }

            if(index == DittActivity.this.selectedPosition[0]) {
               rowView.setBackgroundColor(android.R.color.darker_gray);
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
      public final TaskStatus status;

      public DittTask(int id, String taskName, TaskStatus taskStatus) {
         this.id = id;
         this.name = taskName;
         this.status = taskStatus;
      }

      public String toString() {
         return name;
      }
   }

   public enum TaskStatus {
      Complete, Incomplete;
   }
}
