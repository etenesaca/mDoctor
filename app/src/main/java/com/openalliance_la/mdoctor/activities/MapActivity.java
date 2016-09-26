package com.openalliance_la.mdoctor.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.openalliance_la.mdoctor.R;

public class MapActivity extends AppCompatActivity {
    Context ctx = (Context) this;

    EditText txtLongitud;
    EditText txtLatitud;
    Button btnGotoMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Rescatamos el Action Bar y activamos el boton HomeActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        txtLongitud = (EditText) findViewById(R.id.txtLongitud);
        txtLatitud = (EditText) findViewById(R.id.txtLatitud);
        btnGotoMap = (Button) findViewById(R.id.btnGotoMap);

        btnGotoMap.setOnClickListener(lsbtnGotoMap);
    }

    View.OnClickListener lsbtnGotoMap = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String Longitud = txtLongitud.getText().toString();
            String Latitud = txtLatitud.getText().toString();
            String Zoom = "15";

            //Uri intentUri = Uri.parse("geo:41.382,2.170?z=16&q=41.382,2.170");

            //geo:<lat>,<lon>?z=<zoom>&q=<lat>,<lon>(<label>)
            //geo:41.3825581,2.1704375?z=16&q=41.3825581,2.1704375(Barcelona)

            String URLMap = "geo:" + Latitud + "," + Longitud + "?z=" + Zoom + "&q=" + Latitud + ", "+ Longitud + "(Cuenca)";
            Uri intentUri = Uri.parse(URLMap);
            Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
            startActivity(intent);
        }
    };
}
