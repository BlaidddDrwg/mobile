package io.ditt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.app.ListActivity;
import android.widget.ListAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.database.MatrixCursor;

public class DittActivity extends ListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("Launching DittActivity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ditt);

        // Create a cursor to supply information..
        String[] dataColumnNames = new String[] {"_id", "Task description", "Status" };
        MatrixCursor dataCursor = new MatrixCursor(dataColumnNames);
        dataCursor.addRow(new Object[] {"1", "Unboxing SKU#13", TaskStatus.Incomplete});
        dataCursor.addRow(new Object[] {"2", "Powering up SKU#13", TaskStatus.Incomplete});

        ListAdapter dittTasks = new SimpleCursorAdapter(
              this,  // context
              R.layout.ditt_task, // Row template...
              //android.R.layout.simple_list_item_1,
              dataCursor,
              new String[] {dataColumnNames[1], dataColumnNames[2]},
              new int[]{R.id.ditt_id, R.id.ditt_name},
              CursorAdapter.FLAG_AUTO_REQUERY);

        setListAdapter(dittTasks);
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
