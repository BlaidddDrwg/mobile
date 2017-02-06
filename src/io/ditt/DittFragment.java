package io.ditt;

import android.app.Activity;
import java.io.File;
import android.util.Log;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.view.LayoutInflater;
import android.media.ExifInterface;

public class DittFragment extends Fragment {

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      // Inflate the layout for this fragment..
      return inflater.inflate(R.layout.fragment_ditt, container, false);
   }
}
