package com.example.diego.lugaresfavoritos;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diego.lugaresfavoritos.dao.LugaresDAO;
import com.example.diego.lugaresfavoritos.dao.LugaresSQLiteHelper;
import com.example.diego.lugaresfavoritos.model.Categorias;
import com.example.diego.lugaresfavoritos.model.Lugares;

import java.util.ArrayList;

public class ModificarActivity extends AppCompatActivity {


    public TextView viewNombre,viewLongitud,viewLatitud,viewComentarios;
    public String nombre,comentarios;
    double latitud,longitud;
    int categoria,puntuacion,id;
    Spinner spinnerCategoria;
    LugaresSQLiteHelper con;
    RatingBar ratingPuntuacion;
    LugaresDAO proceso = new LugaresDAO();
    Button botonGps;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent recibir = getIntent();
        id = recibir.getIntExtra("id",0);

        viewNombre = (TextView)findViewById(R.id.txtNombre);
        viewLongitud = (TextView)findViewById(R.id.txtLongitud);
        viewLatitud = (TextView)findViewById(R.id.txtLatitud);
        viewComentarios = (TextView)findViewById(R.id.txtComentarios);
        ratingPuntuacion = (RatingBar)findViewById(R.id.ratingPuntuacion);
        spinnerCategoria = (Spinner) findViewById(R.id.spinnerCategorias);

        con = new LugaresSQLiteHelper(this);


        ArrayList<String> categorias = new ArrayList<>();
        categorias.add("Selecciona");
        SQLiteDatabase bd = con.getReadableDatabase();
        Cursor fila = bd.rawQuery("select * from categorias", null);
        if(fila.moveToFirst()){
            do{
                categorias.add( fila.getString(1));
            }while(fila.moveToNext());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(dataAdapter);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                categoria = pos;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


         fila = bd.rawQuery("select * from lugares WHERE id="+id, null);
        Lugares lugar = new Lugares();
        if(fila.moveToFirst()){
            do{
                lugar.setId(fila.getInt(0));
                lugar.setNombre(fila.getString(1));
                lugar.setId_categoria(fila.getInt(2));
                lugar.setLongitud(fila.getDouble(3));
                lugar.setLatitud(fila.getDouble(4));
                lugar.setValoracion(fila.getInt(5));
                lugar.setComentarios(fila.getString(6));

            }while(fila.moveToNext());
        }

        viewNombre.setText(lugar.getNombre());
        viewLongitud.setText(String.valueOf(lugar.getLongitud()));
        viewLatitud.setText(String.valueOf(lugar.getLatitud()));
        ratingPuntuacion.setRating(lugar.getValoracion());
        viewComentarios.setText(lugar.getComentarios());
        spinnerCategoria.setSelection(lugar.getId_categoria());

        bd.close();

        botonGps = (Button) findViewById(R.id.btnGpsmod);

        botonGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    ActivityCompat.requestPermissions(ModificarActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        return;
                    }
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    Location location = null;
                    LocationListener mlocListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    };

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
                    if (locationManager != null) {
                        //Existe GPS_PROVIDER obtiene ubicación
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }

                    if(location == null){ //Trata con NETWORK_PROVIDER
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
                        if (locationManager != null) {
                            //Existe NETWORK_PROVIDER obtiene ubicación
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    }
                    if(location != null) {
                        latitud = location.getLatitude();
                        longitud = location.getLongitude();
                        viewLatitud.setText(null);
                        viewLongitud.setText(null);
                        viewLatitud.setText(String.valueOf(latitud));
                        viewLongitud.setText(String.valueOf(longitud));

                    }else {
                        Toast.makeText(context, "No se pudo obtener geolocalización", Toast.LENGTH_LONG).show();
                    }

                }



            }
        });


    }

    @Override
    //Creamos el menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_guardar, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Cuando se clickea el botón guardar del menú superior
        if (id == R.id.guardar) {

            viewNombre = (TextView) findViewById(R.id.txtNombre);
            viewLongitud = (TextView) findViewById(R.id.txtLongitud);
            viewLatitud = (TextView) findViewById(R.id.txtLatitud);
            viewComentarios = (TextView) findViewById(R.id.txtComentarios);
            spinnerCategoria = (Spinner) findViewById(R.id.spinner2);

            nombre = String.valueOf(viewNombre.getText());
            longitud = Double.valueOf(Double.valueOf(String.valueOf(viewLongitud.getText())));
            latitud = Double.valueOf(Double.valueOf(String.valueOf(viewLatitud.getText())));
            comentarios = String.valueOf(viewComentarios.getText());

            ratingPuntuacion = (RatingBar) findViewById(R.id.ratingPuntuacion);

            puntuacion = (int) ratingPuntuacion.getRating();

            Lugares lugar = new Lugares();
            Intent recibir = getIntent();

            lugar.setId(recibir.getIntExtra("id",0));
            lugar.setNombre(nombre);
            lugar.setId_categoria(categoria);
            lugar.setLongitud(longitud);
            lugar.setLatitud(latitud);
            lugar.setValoracion(puntuacion);
            lugar.setComentarios(comentarios);


            proceso.actualizaLugar(lugar,con);
            Toast toast1 =
                    Toast.makeText(getApplicationContext(), R.string.actualizado, Toast.LENGTH_SHORT);
            toast1.show();

            Intent i = new Intent(ModificarActivity.this,PrincipalActivity.class);
            startActivity(i);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
