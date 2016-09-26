package com.openalliance_la.mdoctor.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.openalliance_la.mdoctor.R;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {
    Context ctx = (Context) this;

    EditText txtLongitud;
    EditText txtLatitud;
    EditText txtPlace;
    Button btnGotoMap;
    Spinner spPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Rescatamos el Action Bar y activamos el boton HomeActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtLongitud = (EditText) findViewById(R.id.txtLongitud);
        txtLatitud = (EditText) findViewById(R.id.txtLatitud);
        txtPlace = (EditText) findViewById(R.id.txtPlace);
        btnGotoMap = (Button) findViewById(R.id.btnGotoMap);
        spPlace = (Spinner) findViewById(R.id.spZoom);

        btnGotoMap.setOnClickListener(lsbtnGotoMap);

        // Cargar Spinner de Zoom
        ArrayList<String> lstZoom = new ArrayList<>();
        for (int i = 5; i <= 20; i++) {
            lstZoom.add(i + "");
        }
        ArrayAdapter<String> special_adapter = new ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_item, lstZoom);
        spPlace.setAdapter(special_adapter);
    }

    View.OnClickListener lsbtnGotoMap = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String Longitud = txtLongitud.getText().toString();
            String Latitud = txtLatitud.getText().toString();
            String Place = txtPlace.getText().toString();
            String Zoom = spPlace.getSelectedItem() + "";

            //Uri intentUri = Uri.parse("geo:41.382,2.170?z=16&q=41.382,2.170");

            //geo:<lat>,<lon>?z=<zoom>&q=<lat>,<lon>(<label>)
            //geo:41.3825581,2.1704375?z=16&q=41.3825581,2.1704375(Barcelona)

            String URLMap = "geo:" + Latitud + "," + Longitud + "?z=" + Zoom + "&q=" + Latitud + ", " + Longitud + "(mDoctor: " + Place + ")";
            Uri intentUri = Uri.parse(URLMap);
            Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
            startActivity(intent);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
