package com.example.diego.lugaresfavoritos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diego.lugaresfavoritos.dao.LugaresDAO;
import com.example.diego.lugaresfavoritos.dao.LugaresSQLiteHelper;
import com.example.diego.lugaresfavoritos.model.Categorias;
import com.example.diego.lugaresfavoritos.model.Lugares;

public class InformacionActivity extends AppCompatActivity {

    TextView nombre,categoriaview,longitud,latitud,valoracion,comentarios;
    RatingBar puntuacion;
    LugaresSQLiteHelper con;
    LugaresDAO proceso;
    int id;
    Lugares lugar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nombre = (TextView) findViewById(R.id.txtNombreMostrado);
        categoriaview = (TextView) findViewById(R.id.txtCategoriaMostrado);
        longitud = (TextView) findViewById(R.id.txtLongitudMostrado);
        latitud = (TextView) findViewById(R.id.txtLatitudMostrado);
        comentarios = (TextView) findViewById(R.id.txtComentariosMostrado);
        puntuacion = (RatingBar) findViewById(R.id.ratingValoracion);

        Intent recibir = getIntent();
        id = recibir.getIntExtra("id",0);

        con = new LugaresSQLiteHelper(this);
        SQLiteDatabase bd = con.getReadableDatabase();
        Cursor fila = bd.rawQuery("select * from lugares WHERE id="+id, null);
        lugar = new Lugares();
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

        nombre.setText(lugar.getNombre());
        longitud.setText(String.valueOf(lugar.getLongitud()));
        latitud.setText(String.valueOf(lugar.getLatitud()));
        puntuacion.setRating(lugar.getValoracion());
        comentarios.setText(lugar.getComentarios());


         fila = bd.rawQuery("select * from categorias WHERE id="+lugar.getId_categoria(),null);
        Categorias categoria = new Categorias();
        if (fila.moveToFirst()){
            do {
                categoria.setId(fila.getInt(0));
                categoria.setNombre(fila.getString(1));
            }while(fila.moveToNext());
        }

        categoriaview.setText(categoria.getNombre());
        bd.close();

    }


    @Override
    //Creamos el menu
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_secundario, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //Cuando se clickea el botón guardar del menú superior
        if (id == R.id.modificar) {
            Intent i = new Intent(InformacionActivity.this,ModificarActivity.class);
            i.putExtra("id",lugar.getId());
            startActivity(i);

            return true;
        }else if (id==R.id.guardar){
            LugaresSQLiteHelper conexion = new LugaresSQLiteHelper(this);
            int id_borrar = lugar.getId();

            eliminaLugar(id_borrar,conexion);
            Toast toast1 =
                    Toast.makeText(getApplicationContext(), R.string.eliminado, Toast.LENGTH_SHORT);
            toast1.show();
            Intent intent = new Intent(InformacionActivity.this,PrincipalActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void eliminaLugar(int id,LugaresSQLiteHelper con){

        //Abrimos la base de datos 'favoritos' en modo escritura

        SQLiteDatabase bd = con.getWritableDatabase();


        bd.execSQL("DELETE FROM lugares WHERE id="+id);
        bd.close();
    }


}
