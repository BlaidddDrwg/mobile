package io.ditt;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;

public class AuthActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
       System.out.println("Launching AuthActivity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }


   public void tryLogin(View view) {
      System.out.println("Got button click...");
      Intent dittIntent = new Intent(this, DittActivity.class);
      dittIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(dittIntent);
   }
}
