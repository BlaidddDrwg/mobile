package io.ditt;

public class DittTask {
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
