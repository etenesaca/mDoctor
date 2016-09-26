package com.openalliance_la.mdoctor.activities;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.openalliance_la.mdoctor.ManageDB;
import com.openalliance_la.mdoctor.R;
import com.openalliance_la.mdoctor.clsDoctor;
import com.openalliance_la.mdoctor.gl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AddDoctorActivity extends AppCompatActivity {

    Context Context = (Context) this;
    clsDoctor SelectedRecord;

    Typeface Roboto_light;
    Typeface Roboto_bold;

    private String APP_DIRECTORY = "myPictureApp/";
    private String MEDIA_DIRECTORY = APP_DIRECTORY + "media";
    private String TEMPORAL_PICTURE_NAME = "temporal.jpg";

    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;

    clsDoctor DoctorObj = new clsDoctor(Context);
    EditText txtName;
    EditText txtPhone;
    EditText txtMobile;
    ImageView ivImage;
    TextView lblName;
    TextView lblImage;
    TextView lblSpecial;
    TextView lblPhone;
    TextView lblMobile;
    Spinner spSpecial;
    Spinner spCountry;

    Button btnGallery;
    Button btnCamera;

    private class LoadSpinners extends AsyncTask<String, Void, String> {
        String selectitem = null;

        public LoadSpinners(String selectitem) {
            this.selectitem = selectitem;
        }

        protected String doInBackground(String... params) {
            return gl.readJSONFeed(params[0]);
        }

        protected void onPostExecute(String result) {
            // Cargar la lista Paises
            try {
                JSONObject jObj = (JSONObject) new JSONTokener(result).nextValue();
                JSONArray ArrayCountry = jObj.getJSONObject("RestResponse").getJSONArray("result");
                ArrayList<String> lstCountries = new ArrayList<String>();
                for (int i = 0; i < ArrayCountry.length(); i++) {
                    JSONObject jsonObject = ArrayCountry.getJSONObject(i);
                    lstCountries.add(jsonObject.getString("name"));
                }
                ArrayAdapter<String> country_adapter = new ArrayAdapter<String>(Context, android.R.layout.simple_spinner_item, lstCountries);
                spCountry.setAdapter(country_adapter);

                if (selectitem != null){
                    spCountry.setSelection(Integer.parseInt(gl.getIndexSpinner(spCountry, selectitem) + ""));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        // Rescatamos el Action Bar y activamos el boton HomeActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        lblName = (TextView) findViewById(R.id.lblLanguaje);
        lblImage = (TextView) findViewById(R.id.lblImage);
        lblSpecial = (TextView) findViewById(R.id.lblSpecial);
        lblPhone = (TextView) findViewById(R.id.lblPhone);
        lblMobile = (TextView) findViewById(R.id.lblMobile);
        txtName = (EditText) findViewById(R.id.tvName);
        txtPhone = (EditText) findViewById(R.id.tvPhone);
        txtMobile = (EditText) findViewById(R.id.tvMobile);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        btnGallery = (Button) findViewById(R.id.btnGallery);
        btnCamera = (Button) findViewById(R.id.btnCamera);
        spSpecial = (Spinner) findViewById(R.id.spSpecial);
        spCountry = (Spinner) findViewById(R.id.spCountry);

        // Establecer las fuentes
        Roboto_light = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        Roboto_bold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        lblName.setTypeface(Roboto_bold);
        lblImage.setTypeface(Roboto_bold);
        lblSpecial.setTypeface(Roboto_bold);
        lblPhone.setTypeface(Roboto_bold);
        lblMobile.setTypeface(Roboto_bold);

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        // Obtener los parametros que se le pasan
        Bundle bundle = getIntent().getExtras();
        String curr_counrty = null;
        if (bundle != null && bundle.containsKey("current_id")) {
            getSupportActionBar().setTitle("Editar");
            SelectedRecord = new clsDoctor();
            LoadData Task = new LoadData(Integer.parseInt(bundle.getString("current_id") + ""));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Task.execute();
            }
            btnGallery.requestFocus();
            curr_counrty = SelectedRecord.get_country();
        } else {
            SelectedRecord = null;
            getSupportActionBar().setTitle("Agregar Doctor");
        }


        new LoadSpinners(curr_counrty).execute("http://services.groupkt.com/country/get/all");
        // Cargar la lista de Especialidades
        String[] lstSpecial = {
                "Odontología",
                "Gastroenterología",
                "Geriatría",
                "Neumología",
                "Cardiología",
                "Psiquiatría",
                "Ginecología",
                "Cardiología",
                "Rehabilitación",
                "Medicina Genaral"
        };
        ArrayAdapter<String> special_adapter = new ArrayAdapter<String>(Context, android.R.layout.simple_spinner_item, lstSpecial);
        spSpecial.setAdapter(special_adapter);
    }

    /**
     * Clase Asincrona para recuperar los datos del registro seleccionado
     **/
    protected class LoadData extends AsyncTask<String, Void, HashMap<String, Object>> {
        int RecordID;

        public LoadData(int RecordID) {
            this.RecordID = RecordID;
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
        protected HashMap<String, Object> doInBackground(String... params) {
            SelectedRecord = new clsDoctor(Context, RecordID);
            HashMap<String, Object> res = new HashMap<String, Object>();
            // Obtener la imagen
            res.put("bmp", gl.build_image(Context, SelectedRecord.get_photo()));
            res.put("specialty_index", gl.getIndexSpinner(spSpecial, SelectedRecord.get_specialty()));
            //res.put("country_index", gl.getIndexSpinner(spCountry, SelectedRecord.get_country()));
            return res;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> res) {
            super.onPostExecute(res);

            txtName.setText(SelectedRecord.get_name());
            txtPhone.setText(SelectedRecord.get_phone());
            txtMobile.setText(SelectedRecord.get_mobile());
            ivImage.setImageBitmap((Bitmap) res.get("bmp"));
            spSpecial.setSelection(Integer.parseInt(res.get("specialty_index") + ""));
            //spCountry.setSelection(Integer.parseInt(res.get("country_index") + ""));
        }
    }

    private void openGallery() {
        startActivityForResult(
                Intent.createChooser(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), "Seleccione la imagen"), SELECT_PICTURE
        );

                            /*
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            intent.putExtra("crop", "true");
                            intent.putExtra("aspectX", 0);
                            intent.putExtra("aspectY", 0);
                            intent.putExtra("outputX", 200);
                            intent.putExtra("outputY", 150);
                            intent.putExtra("return-data", true);
                            startActivityForResult(
                                    intent.createChooser(intent, "Seleccione la imagen"), SELECT_PICTURE
                            );
                            */
    }

    public String pathactual = null;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    //Crear archivo
    protected File createImageFile() throws IOException {
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "jpg_tmp";
        File sdCard = Environment.getExternalStorageDirectory(); //Recuperar sdexterno
        File directory = new File(sdCard.getAbsolutePath() + "/Myphotos");
        directory.mkdirs();
        File image = File.createTempFile(
                imageFileName, // nombre
                ".jpg", // extension
                directory // directorio
        );
        return image;
    }

    // Añadir a galeria
    private void galleryAddPic(String path) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void openCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //El paquete existe
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Toast.makeText(this, "Existe un problema al guardar la foto ...", Toast.LENGTH_SHORT).show();
                }
                // Si el archivo pudo crearse
                if (photoFile != null) {
                    Uri fileUri = Uri.fromFile(photoFile); // Crea URI
                    pathactual = photoFile.toString(); // Guardo path
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(takePictureIntent, PHOTO_CODE);
                }
            }
        } else {
            File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            file.mkdirs();

            String path = Environment.getExternalStorageDirectory() + File.separator
                    + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;

            File newFile = new File(path);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }


    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    private String selectedImagePath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_CODE:
                    Bitmap bmp = null;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        selectedImagePath = Environment.getExternalStorageDirectory() + File.separator
                                + MEDIA_DIRECTORY + File.separator + TEMPORAL_PICTURE_NAME;
                        bmp = BitmapFactory.decodeFile(selectedImagePath);
                    } else {
                        galleryAddPic(pathactual);
                        bmp = BitmapFactory.decodeFile(pathactual);
                    }
                    ivImage.setImageBitmap(bmp);
                    break;
                case SELECT_PICTURE:
                    Uri selectedImageUri = data.getData();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                        selectedImagePath = getPath(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                        /*
                        Bitmap bitmap = data.getExtras().getParcelable("data");
                        // convert bitmap to byte
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        */
                        ivImage.setImageBitmap(bitmap);
                    } else {
                        ParcelFileDescriptor parcelFileDescriptor;
                        try {
                            parcelFileDescriptor = getContentResolver().openFileDescriptor(selectedImageUri, "r");
                            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                            parcelFileDescriptor.close();
                            ivImage.setImageBitmap(image);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Intent DoctorActivity = new Intent(Context, DoctorActivity.class);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_save:
                clsDoctor NewDoctor = new clsDoctor();
                NewDoctor.set_name(txtName.getText().toString());
                NewDoctor.set_phone(txtPhone.getText().toString());
                NewDoctor.set_mobile(txtMobile.getText().toString());
                NewDoctor.set_specialty(spSpecial.getSelectedItem() + "");
                NewDoctor.set_country(spCountry.getSelectedItem() + "");

                // Verificar si la categoria no ya no esta creda
                if (NewDoctor.get_name().equals("")) {
                    Snackbar.make(findViewById(android.R.id.content), "Primero Ingrese un nombre", Snackbar.LENGTH_LONG)
                            .show();
                } else if (DoctorObj.getRecords(new Object[]{ManageDB.ColumnsDoctor.DOCTOR_NAME, "=", NewDoctor.get_name()}).size() > 0) {
                    Snackbar.make(findViewById(android.R.id.content), "Ya hay un Doctor con este nombre", Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    // Redimensionar imagen
                    ivImage.buildDrawingCache();
                    Bitmap bm = ivImage.getDrawingCache();
                    bm = gl.scaleDown(bm, 512, true);
                    NewDoctor.set_photo(bm);

                    // Crear un Doctor
                    DoctorObj.AddRecord(NewDoctor);
                    startActivity(DoctorActivity);
                }
                return true;
            case R.id.action_save_edit:
                ContentValues vals = new ContentValues();
                vals.put(ManageDB.ColumnsDoctor.DOCTOR_NAME, txtName.getText().toString());
                vals.put(ManageDB.ColumnsDoctor.DOCTOR_PHONE, txtPhone.getText().toString());
                vals.put(ManageDB.ColumnsDoctor.DOCTOR_MOBILE, txtMobile.getText().toString());
                vals.put(ManageDB.ColumnsDoctor.DOCTOR_SPECILTY, spSpecial.getSelectedItem() + "");
                vals.put(ManageDB.ColumnsDoctor.DOCTOR_COUNTRY, spCountry.getSelectedItem() + "");

                // Redimensionar imagen
                ivImage.buildDrawingCache();
                Bitmap bmp = ivImage.getDrawingCache();
                bmp = gl.scaleDown(bmp, 512, true);
                vals.put(ManageDB.ColumnsDoctor.DOCTOR_PHOTO, gl.BitmaptoByteArray(bmp));

                DoctorObj.Update(SelectedRecord.get_id(), vals);
                startActivity(DoctorActivity);
                finish();
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar Categoria")
                        .setMessage("¿Seguro de Eliminar esta categoria?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DoctorObj.Delete(SelectedRecord.get_id());
                                startActivity(DoctorActivity);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (SelectedRecord == null) {
            inflater.inflate(R.menu.menu_add, menu);
        } else {
            inflater.inflate(R.menu.menu_edit, menu);
        }
        return true;
    }
}
