package io.ditt;

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

public class AuthFragment extends Fragment {

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      // Inflate the layout for this fragment..
      return inflater.inflate(R.layout.fragment_auth, container, false);
   }

   public int getOrientation(Context context, Uri imageUri, String imagePath){
      int rotate = 0;
      try {
         context.getContentResolver().notifyChange(imageUri, null);
         File imageFile = new File(imagePath);

         ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
         int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

         switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
               rotate = 270;
               break;
            case ExifInterface.ORIENTATION_ROTATE_180:
               rotate = 180;
               break;
            case ExifInterface.ORIENTATION_ROTATE_90:
               rotate = 90;
               break;
         }

         Log.i("RotateImage", "Exif orientation: " + orientation);
         Log.i("RotateImage", "Rotate value: " + rotate);
      } catch (Exception e) {
         e.printStackTrace();
      }
      return rotate;
   }
}
