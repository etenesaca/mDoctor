package com.openalliance_la.mdoctor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Spinner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by edgar on 20/02/16.
 */
public class gl {
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
        return newBitmap;
    }

    public  static byte[] BitmaptoByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return  byteArray;
    }

    public static Bitmap build_image(Context context, byte[] img){
        Bitmap bmp;
        if (img != null){
            try{
                ByteArrayInputStream imageStream = new ByteArrayInputStream(img);
                bmp = BitmapFactory.decodeStream(imageStream);
            } catch (Throwable e) {
                bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_def_48x48);
            }
        }else{
            // En el caso de que no se pueda cargar la imagen se devuelve una por defecto
            bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_def_128);
        }
        return bmp;
    }

    public static int getIndexSpinner(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }
}
