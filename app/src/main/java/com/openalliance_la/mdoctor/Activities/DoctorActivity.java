package com.openalliance_la.mdoctor.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.openalliance_la.mdoctor.ManageDB;
import com.openalliance_la.mdoctor.R;
import com.openalliance_la.mdoctor.clsDoctor;
import com.openalliance_la.mdoctor.item.adapter.DoctorItem;

import java.util.ArrayList;
import java.util.List;

public class DoctorActivity extends AppCompatActivity {
    Context Context = (Context) this;

    clsDoctor DoctorObj = new clsDoctor(Context);
    ArrayList<clsDoctor> DoctorList = new ArrayList<clsDoctor>();
    DoctorItem adapter;
    GridView dataList;
    String[] menuItems = new String[]{ "Eliminar" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Rescatamos el Action Bar y activamos el boton HomeActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        dataList = (GridView) findViewById(R.id.lvDoctor);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent DoctorActivity = new Intent(DoctorActivity.this, AddDoctorActivity.class);
                clsDoctor ItemSelected = adapter.getItem(position);
                DoctorActivity.putExtra("current_id", ItemSelected.get_id() + "");
                startActivity(DoctorActivity);
            }
        });
        dataList.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                final ImageView imgIcon = (ImageView) view.findViewById(R.id.ivImage);
                final TextView txtTitle = (TextView) view.findViewById(R.id.tvName);
                //final TextView txtCount = (TextView) view.findViewById(R.id.txtCount);

                txtTitle.setText(null);
                //txtCount.setVisibility(View.GONE);
                //txtCount.setText(null);
                txtTitle.setVisibility(View.GONE);
                imgIcon.setImageBitmap(null);
                imgIcon.setScaleType(ImageView.ScaleType.CENTER);
                imgIcon.setImageDrawable(Context.getResources().getDrawable(R.drawable.loading_24x24));
            }
        });

        LoadListData();
        registerForContextMenu(dataList);
    }

    void LoadListData(){
        LoadListData("");
    }
    void LoadListData(String TextToSearch){
        LoadData Task = new LoadData(TextToSearch);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else { Task.execute(); }
    }
    protected class LoadData extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;
        List<Object[]> args;

        public LoadData(String TextToSearch) {
            List<Object[]> args = new ArrayList<Object[]>();
            if (!TextToSearch.equals("")){
                args.add(new Object[]{ManageDB.ColumnsDoctor.DOCTOR_NAME, "like",  "%" + TextToSearch + "%"});
            }
            this.args = args;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = ProgressDialog.show(DoctorActivity.this, "", "Cargando Datos");
        }

        @Override
        protected String doInBackground(String... params) {
            DoctorList.clear();
            final List<clsDoctor> images = DoctorObj.getRecords(args);
            for(clsDoctor im : images){
                DoctorList.add(im);
            }
            adapter = new DoctorItem(Context, R.layout.list_item_doctor, DoctorList);
            return "";
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            dataList.setAdapter(adapter);
            pDialog.dismiss();
        }
    }
}
