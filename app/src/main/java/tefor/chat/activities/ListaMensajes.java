package tefor.chat.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import tefor.chat.R;
import tefor.chat.adaptador.AdaptadorListaMensajes;
import tefor.chat.dao.FactoryDAO;
import tefor.chat.dao.UsuarioDAO;
import tefor.chat.adaptador.AdaptadorListaMensajes.ModeloMensajes;
import tefor.chat.dto.Usuario;
import tefor.chat.utiles.AnimacionBotonFlotante;

public class ListaMensajes extends AppCompatActivity implements View.OnClickListener,
        AdaptadorListaMensajes.OnItemTouchListener {

    private FloatingActionButton botonFlotante;
    private SwipeRefreshLayout contenedorSwipeRefreshLayout;
    private RecyclerView mensajesRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mensajes);

        inicializarCompontes();

        botonFlotante.setOnClickListener(this);
        contenedorSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                actualizarLista();
                contenedorSwipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    private void inicializarCompontes() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        botonFlotante = (FloatingActionButton) findViewById(R.id.botonFlotante);
        contenedorSwipeRefreshLayout = (SwipeRefreshLayout)
                findViewById(R.id.listaMensajes_swipeRefreshLayout);
        mensajesRecyclerView = (RecyclerView) findViewById(R.id.listaMensajes_mensajesRecyclerView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnimacionBotonFlotante.animar(botonFlotante, getBaseContext());
        actualizarLista();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void actualizarLista() {
        Usuario objUsuario = Usuario.getInstancia();

        if (objUsuario != null) {
            new CargarMensajesTask().execute(objUsuario);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.botonFlotante:
                enviarMensaje();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_actualizar:
                actualizarLista();
                break;

            case R.id.accion_acerca:
                new AcercaDe().show(getFragmentManager(), "ACERCA_APP");
                break;

            case R.id.accion_cerrar_sesion:
                cerrarSesion();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        Usuario.setInstancia(null);

        SharedPreferences preferencias =
                getSharedPreferences("preferencias", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putInt("ID", 0);
        editor.putString("nick", "");
        editor.putString("contraseña", "");
        editor.putString("token", "");

        editor.apply();

        finish();
    }

    private void enviarMensaje() {
        Intent intent = new Intent(this, EnviarMensaje.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(Usuario usuario, int posicion) {
        Intent intent = new Intent(getApplicationContext(), DetalleMensajes.class);
        intent.putExtra(DetalleMensajes.REMITENTE, usuario.getNick());

        startActivity(intent);
    }

    private class CargarMensajesTask extends AsyncTask<Usuario, String, ModeloMensajes> {

        private ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(ListaMensajes.this);
            progreso.setIndeterminate(true);
            progreso.setTitle(getString(R.string.obteniendo_datos));
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected ModeloMensajes doInBackground(Usuario... usuarios) {
            publishProgress(getString(R.string.por_favor_espere));

            Usuario usuario = usuarios[0];

            FactoryDAO factory = FactoryDAO.getOrCreate();
            UsuarioDAO dao = factory.newUsuarioDAO();

            List<Usuario> chats = dao.cargarChats(usuario);
            List<String> mensajes = dao.cargarUltimosMensajes(usuario);

            if (chats == null || mensajes == null) {
                return null;
            }

            ModeloMensajes modelo = ModeloMensajes.getOrCreate()
                    .setUsuarios(chats)
                    .setMensajes(mensajes);

            return modelo;
        }

        @Override
        protected void onPostExecute(ModeloMensajes modelo) {
            super.onPostExecute(modelo);

            progreso.dismiss();

            if (modelo == null) {
                Snackbar.make(findViewById(R.id.listaMensajes_contenedorCoordinatorLayout),
                        R.string.conexion_servidor_error, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }

            AdaptadorListaMensajes adaptador = new AdaptadorListaMensajes();
            adaptador.setOnItemTouchListener(ListaMensajes.this);

            mensajesRecyclerView.setAdapter(adaptador);
            mensajesRecyclerView.setLayoutManager(new LinearLayoutManager(ListaMensajes.this));
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            progreso.setMessage(values[0]);
        }

    }

}
