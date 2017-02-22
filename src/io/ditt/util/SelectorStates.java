package io.ditt.util;

public class SelectorStates {
   public static String translateDrawableState(int drawableState) {
      switch (drawableState) {
         case android.R.attr.state_activated:
            return "state_activated";
         case android.R.attr.state_active:
            return "state_active";
         case android.R.attr.checkable:
            return "state_checkable";
         case android.R.attr.checked:
            return "state_checked";
         case android.R.attr.enabled:
            return "state_enabled";
         case android.R.attr.state_first:
            return "state_first";
         case android.R.attr.state_focused:
            return "state_focused";
         case android.R.attr.state_last:
            return "state_last";
         case android.R.attr.state_middle:
            return "state_middle";
         case android.R.attr.state_pressed:
            return "state_pressed";
         case android.R.attr.state_selected:
            return "state_selected";
         case android.R.attr.state_single:
            return "state_single";
         case android.R.attr.state_window_focused:
            return "state_window_focused";
         case android.R.attr.variablePadding:
            return "variable_padding";
         case android.R.attr.visible:
            return "visible";
         case android.R.attr.state_enabled:
            return "state_enabled";
         default:
            return "unknown state value: " + drawableState;
      }
   }
}
