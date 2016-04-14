package com.iaware.cabuu.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.iaware.cabuu.entidades.Midia;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by PeDeNRiQue on 15/11/2015.
 */
public class ImagemUtils {
    public static byte[] bitmapToByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    public static String bitmapImageToString(Bitmap bitmapPicture){
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        //System.out.println("IMAGE:"+encodedImage);
        return encodedImage;
    }

    public static String bitmapToString(Bitmap bitmapPicture){
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }
    public static String pathVideoToString(String path) throws IOException {
        String encodedImage;
        FileInputStream fis = new FileInputStream(path);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        for (int readNum; (readNum = fis.read(b)) != -1;) {
            bos.write(b, 0, readNum);
        }

        byte[] bytes = bos.toByteArray();
        encodedImage = Base64.encodeToString(bytes, Base64.DEFAULT);
        System.out.println("VIDEO:"+encodedImage);
        return encodedImage;

    }

    public static Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            //Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            //return bitmap;

            BitmapFactory.Options options=new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
            options.inPurgeable = true; // inPurgeable is used to free up memory while required
            Bitmap songImage1 = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length, options);//Decode image, "thumbnail" is the object of image file
            Bitmap songImage = Bitmap.createScaledBitmap(songImage1, 300 , 300 , true);// convert decoded bitmap into well scalled Bitmap format.

            return songImage;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }


    //private String userFacebookId;
    //private FileOutputStream fos;
    public static void loadImage(String id,ImageView myImage){

        String caminho  = Environment.getExternalStorageDirectory()+ File.separator + id + ".jpg";

        File imgFile = new  File(caminho);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            //ImageView myImage = (ImageView) findViewById(R.id.idView);

            myImage.setImageBitmap(myBitmap);

        }
    }

    public static void saveImageToSD(Bitmap bmp,String id) {

    /*--- this method will save your downloaded image to SD card ---*/
        FileOutputStream fos = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    /*--- you can select your preferred CompressFormat and quality.
     * I'm going to use JPEG and 100% quality ---*/
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
    /*--- create a new file on SD card ---*/
        String caminho  = Environment.getExternalStorageDirectory()+ File.separator + id + ".jpg";

        File file = new File(caminho);
        try {

            file.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    /*--- create a new FileOutputStream and write bytes to file ---*/
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(bytes.toByteArray());
            fos.close();
            Log.i("Path Image", caminho);


            byte[] byteArray = bytes.toByteArray();
            //imagemPerfilString = Base64.encodeToString(byteArray, Base64.DEFAULT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();

        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;

        float scaleHeight = ((float) newHeight) / height;

// CREATE A MATRIX FOR THE MANIPULATION

        Matrix matrix = new Matrix();

// RESIZE THE BIT MAP

        matrix.postScale(scaleWidth, scaleHeight);

// RECREATE THE NEW BITMAP

        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }


    /* RECUPERAR A MIDIA E EXIBIR NO LAYOUT */

    public static int TAKE_PICTURE = 1;
    public static int SELECT_FILE = 3;
    public static int SELECT_VIDEO = 4;
    public static int GRAVAR_VIDEO = 5;

    private void selectImage(final Activity activity) {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activity.startActivityForResult(intent, TAKE_PICTURE);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    activity.startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public static Midia setImage(Activity activity,ImageView imgMidia1,int requestCode, int resultCode, Intent data, int controle) throws IOException {
        Bitmap bitmap = null;
        Midia midia = null;

        if (requestCode == TAKE_PICTURE && resultCode== activity.RESULT_OK && data != null){
            // get bundle
            Bundle extras = data.getExtras();
            // get bitmap
            bitmap = (Bitmap) extras.get("data");

            midia = new Midia(bitmapImageToString(bitmap), "png", controle);
            imgMidia1.setImageBitmap(bitmap);

        }else if(requestCode == SELECT_FILE && resultCode== activity.RESULT_OK && data != null){
            Uri selectedImageUri = data.getData();
            Log.i("Caminho: ",selectedImageUri.getPath());
            String[] projection = { MediaStore.MediaColumns.DATA };
            CursorLoader cursorLoader = new CursorLoader(activity,selectedImageUri, projection, null, null,
                    null);
            Cursor cursor =cursorLoader.loadInBackground();
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String selectedImagePath = cursor.getString(column_index);
            //Bitmap bitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(selectedImagePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(selectedImagePath, options);

            midia = new Midia(bitmapImageToString(bitmap), "png", controle);
            imgMidia1.setImageBitmap(bitmap);

        }else if(requestCode == SELECT_VIDEO && resultCode== activity.RESULT_OK && data != null){
            Uri selectedImageUri = data.getData();
            String path = getPath(activity, selectedImageUri);
            Log.i("Caminho: ", path + " ");
            bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
            midia = new Midia(pathVideoToString(path), "mp4", controle);
            imgMidia1.setImageBitmap(bitmap);
        }else if(requestCode == GRAVAR_VIDEO && resultCode== activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String path = getPath(activity,selectedImageUri);
            bitmap = ThumbnailUtils.createVideoThumbnail(path,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            Log.e("SELECTED IMAGE URI: ", path);
            midia = new Midia(pathVideoToString(path), "mp4", controle);
            imgMidia1.setImageBitmap(bitmap);
        }
        return midia;
    }

    public static String getPath(Activity activity,Uri uri) {

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}
