package com.example.exadeduardo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.exadeduardo.model.Libros;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //Creamos el Arrailist de los libros
    private List<Libros> listLibro = new ArrayList<Libros>();
    ArrayAdapter<Libros> arrayAdapterPersona;

    //creamos los ellementos para la interfaz
    EditText isbn, autor,titulo,año;
    ListView listV_libros;

    //Instancia de la base de datos
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //creamos la seleccion de libros
    Libros libroSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isbn = findViewById(R.id.txt_isbn);
        autor = findViewById(R.id.txt_autor);
        titulo = findViewById(R.id.txt_titulo);
        año = findViewById(R.id.txt_año);

        listV_libros = findViewById(R.id.lv_Libros);
        inicializarFirebase();
        listarDatos();

        //aqui hacemos una llamada para cuando clickemos un libro nos aparezco en los Edit Text
        listV_libros.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                libroSelected = (Libros) parent.getItemAtPosition(position);
                isbn.setText((CharSequence) libroSelected.getIsbn());
                autor.setText((CharSequence) libroSelected.getAutor());
                titulo.setText((CharSequence) libroSelected.getTitulo());
                año.setText((CharSequence) libroSelected.getAno());
            }
        });

    }

    //Aqui cargamos el Listado de los libros
    private void listarDatos() {
        databaseReference.child("Libros").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listLibro.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Libros p = objSnaptshot.getValue(Libros.class);
                    listLibro.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Libros>(MainActivity.this, android.R.layout.simple_list_item_1, listLibro);
                    listV_libros.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Aqui inizializamos la Base de Datos
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    //aqui hacemos que los elementos del menu tengan unos botones funcionales
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String codigo = isbn.getText().toString();
        String nombre = autor.getText().toString();
        String portada = titulo.getText().toString();
        String fecha = año.getText().toString();

        switch (item.getItemId()){
            //el boton de add añade a la Bd los datos
            case R.id.icon_add:{
                if (codigo.equals("")||nombre.equals("")||portada.equals("")||fecha.equals("")){
                    validacion();
                }
                else {
                    Libros p = new Libros();
                    p.setUid(UUID.randomUUID().toString());
                    p.setIsbn(codigo);
                    p.setAutor(nombre);
                    p.setTitulo(portada);
                    p.setAno(fecha);
                    databaseReference.child("Libros").child(p.getUid()).setValue(p);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }
                break;
            }

            //el boton de save actualiza los cambios que hagas al libro
            case R.id.icon_save:{
                Libros p = new Libros();
                p.setUid(libroSelected.getUid());
                p.setIsbn(isbn.getText().toString().trim());
                p.setAutor(autor.getText().toString().trim());
                p.setTitulo(titulo.getText().toString().trim());
                p.setAno(año.getText().toString().trim());
                databaseReference.child("Libros").child(p.getUid()).setValue(p);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }

            //el boton Delete borra los datos en la BD
            case R.id.icon_delete:{
                Libros p = new Libros();
                p.setUid(libroSelected.getUid());
                databaseReference.child("Libros").child(p.getUid()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            default:break;
        }
        return true;
    }

    //aqui llamamos a un metodo que vacia las cajas cuando terminas una recojida/borrado de datos
    private void limpiarCajas() {
        isbn.setText("");
        autor.setText("");
        titulo.setText("");
        año.setText("");
    }

    //esto hace que confirme si hay un espacio vacio, si lo hay tienes que rellenarlo
    private void validacion() {
        String codigo = isbn.getText().toString();
        String nombre = autor.getText().toString();
        String portada = titulo.getText().toString();
        String fecha = año.getText().toString();
        if (codigo.equals("")){
            isbn.setError("Required");
        }
        else if (nombre.equals("")){
            autor.setError("Required");
        }
        else if (portada.equals("")){
            titulo.setError("Required");
        }
        else if (fecha.equals("")){
            año.setError("Required");
        }
    }
}