package com.openalliance_la.mdoctor.activities;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CategoryActivity = new Intent(Context, AddDoctorActivity.class);
                startActivity(CategoryActivity);

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvDoctor) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Opciones");

            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (menuItems[item.getItemId()]){
            case "Eliminar":
                clsDoctor RowtoDelete = adapter.getItem(info.position);
                DoctorObj.Delete(RowtoDelete.get_id());
                adapter.remove(RowtoDelete);
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.doctor, menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();

            search.setSuggestionsAdapter(mAdapter);
            search.setIconifiedByDefault(false);
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    LoadListData(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    populateAdapter(newText);
                    return false;
                }
            });
            search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        LoadListData();
                    }
                }
            });
            search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }

                @Override
                public boolean onSuggestionClick(int position) {
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private static final String[] SUGGESTIONS = {
            "Bauru", "Sao Paulo", "Rio de Janeiro",
            "Bahia", "Mato Grosso", "Minas Gerais",
            "Tocantins", "Rio Grande do Sul"
    };
    private SimpleCursorAdapter mAdapter;

    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{ BaseColumns._ID, "cityName" });
        for (int i=0; i<SUGGESTIONS.length; i++) {
            if (SUGGESTIONS[i].toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[] {i, SUGGESTIONS[i]});
        }
        mAdapter.changeCursor(c);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.btnAdd:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
