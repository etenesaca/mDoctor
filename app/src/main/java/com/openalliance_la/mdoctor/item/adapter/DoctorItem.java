package com.openalliance_la.mdoctor.item.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.openalliance_la.mdoctor.R;

import com.openalliance_la.mdoctor.clsDoctor;
import com.openalliance_la.mdoctor.gl;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorItem extends ArrayAdapter<clsDoctor> {
    Context context;
    int layoutResourceId;
    ArrayList<clsDoctor> data = new ArrayList<clsDoctor>();

    public DoctorItem(Context context, int layoutResourceId, ArrayList<clsDoctor> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        clsDoctor Record = data.get(position);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, null);

            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.tvName);
            holder.imgIcon = (ImageView) convertView.findViewById(R.id.ivImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Ejecutar la Tarea de acuerdo a la version de Android
        LoadView Task = new LoadView(convertView, Record);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, holder);
        } else {
            Task.execute(holder);
        }
        return convertView;
    }

    /** Clase Asincrona para recuperar los datos de la fila **/
    protected class LoadView extends AsyncTask<ViewHolder, Void, HashMap<String, Object>> {
        protected ViewHolder v;

        protected clsDoctor Record;
        protected View convertView;
        Typeface Roboto_bold;
        Typeface Roboto_light;

        public LoadView(View convertView, clsDoctor Record) {
            this.Record = Record;
            this.convertView = convertView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected HashMap<String, Object> doInBackground(ViewHolder... params) {
            v = params[0];
            Roboto_bold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
            Roboto_light = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");

            HashMap<String, Object> res = new HashMap<String, Object>();
            // Obtener la imagen
            res.put("bmp", gl.build_image(context, Record.get_photo()));
            return res;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> res) {
            super.onPostExecute(res);

            v.txtTitle.setText(Record.get_name());
            v.txtTitle.setVisibility(View.VISIBLE);
            v.txtTitle.setTypeface(Roboto_light);
            v.txtCount.setText(res.get("num_images") + "");
            v.txtCount.setVisibility(View.VISIBLE);
            v.imgIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            v.imgIcon.setImageBitmap((Bitmap) res.get("bmp"));
        }
    }

    static class ViewHolder
    {
        ImageView imgIcon;
        TextView txtTitle;
        TextView txtCount;
    }
}
