package io.ditt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import android.util.Log;

public class FileOp {

   public static void copyFile(final String inputPath, final String inputName, 
         final String outputPath, final String outputName) throws IOException {
      InputStream in = null;
      OutputStream out = null;
      try {
         File dir = new File(outputPath);
         if(!dir.exists()) {
            dir.mkdirs();
         }
         
         in = new FileInputStream(addTerminatingPathSeparatorIfAbsent(inputPath) + inputName);
         out = new FileOutputStream(addTerminatingPathSeparatorIfAbsent(outputPath) + outputName);

         byte[] buffer = new byte[1024];
         int read;
         while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
         }

         in.close();
         in = null;

         out.flush();
         out.close();
         out = null;
      } catch (FileNotFoundException fnfe) {
         Log.e("tag", fnfe.getMessage());
      } catch (Exception e) {
         Log.e("tag", e.getMessage());
      }
   }

   private static String addTerminatingPathSeparatorIfAbsent(String inputPath) {
      return (inputPath.endsWith(File.separator) ? inputPath : (inputPath + "/"));
   }

   public static void deleteFile(final String inputPath, final String inputName) {
      try {
         new File(inputPath + inputName).delete();
      }
      catch(Exception e) {
         Log.e("tag", e.getMessage());
      }
   }

   public static void moveFile(final String inputFilePath, final String outputPath, final String outputName) throws IOException{
      final int lastPathSeparatorIndex = inputFilePath.lastIndexOf(File.separator);
      System.out.println("Path separator (" + File.separator + ") for [" + inputFilePath + "] is at [" + lastPathSeparatorIndex + "]");
      final String inputPath = inputFilePath.substring(0, lastPathSeparatorIndex);
      System.out.println("Input path = [" + inputPath + "]");
      final String inputFile = inputFilePath.substring(lastPathSeparatorIndex + 1);
      System.out.println("Input file = [" + inputFile + "]");
      moveFile(inputPath, inputFile, outputPath, outputName);
   }

   public static void moveFile(final String inputPath, final String inputName, 
         final String outputPath, final String outputName) throws IOException {
      copyFile(inputPath, inputName, outputPath, outputName);
      deleteFile(inputPath, inputName);
   }
}
