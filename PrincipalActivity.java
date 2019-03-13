package com.example.diego.lugaresfavoritos;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.diego.lugaresfavoritos.dao.Adaptador;
import com.example.diego.lugaresfavoritos.dao.LugaresDAO;
import com.example.diego.lugaresfavoritos.dao.LugaresSQLiteHelper;
import com.example.diego.lugaresfavoritos.model.Lugares;

import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    Spinner spinnerCategoria;
    LugaresSQLiteHelper con;
    Button botonMaps;
    int categoria;
    RecyclerView recyclerViewLugares;
    List<Lugares> lugares = new ArrayList<>();
    Adaptador adaptador;
    Context context = this;
    LugaresDAO proceso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PrincipalActivity.this,CreacionActivity.class);
                startActivity(i);
            }
        });

        con = new LugaresSQLiteHelper(this);
        spinnerCategoria  = (Spinner) findViewById(R.id.spinnerCategorias);

        ArrayList<String> categorias = new ArrayList<>();
        SQLiteDatabase bd = con.getReadableDatabase();
        Cursor fila = bd.rawQuery("select * from categorias", null);
        if(fila.moveToFirst()){
            do{
                categorias.add( fila.getString(1));
            }while(fila.moveToNext());
        }
        bd.close();


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(dataAdapter);

        recyclerViewLugares = findViewById(R.id.recyclerLista);
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                categoria = pos+1;
                proceso = new LugaresDAO();
                recyclerViewLugares.setLayoutManager(new LinearLayoutManager(context));
                adaptador = new Adaptador(proceso.mostrarLugares(con,categoria,lugares));
                recyclerViewLugares.setAdapter(adaptador);

                adaptador.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(PrincipalActivity.this,InformacionActivity.class);
                        i.putExtra("id",lugares.get(recyclerViewLugares.getChildAdapterPosition(v)).getId());
                        startActivity(i);

                    }
                });

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        botonMaps = (Button) findViewById(R.id.btnMaps);
        botonMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PrincipalActivity.this,MapsActivity.class);
                startActivity(i);
            }
        });
    }

    public List<Lugares> mostrarLugares(){
        SQLiteDatabase db = con.getReadableDatabase();
        Cursor cursor2 = db.rawQuery("SELECT * FROM lugares WHERE id_categoria="+categoria,null);


        if (cursor2.moveToFirst()){
            do{
                lugares.add(new Lugares(cursor2.getInt(0),cursor2.getString(1),cursor2.getInt(2),
                        cursor2.getDouble(3),cursor2.getDouble(4),cursor2.getInt(5),cursor2.getString(6)));
            }while (cursor2.moveToNext());
            }
            return lugares;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
