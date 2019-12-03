package tefor.chat.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import tefor.chat.R;
import tefor.chat.adaptador.AdaptadorDetalleMensajes;
import tefor.chat.dao.FactoryDAO;
import tefor.chat.dao.UsuarioDAO;
import tefor.chat.dto.Usuario;

public class DetalleMensajes extends AppCompatActivity {

    private SwipeRefreshLayout contenedorSwipeRefreshLayout;
    private RecyclerView mensajesRecyclerView;
    public static final String REMITENTE = "NICK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_mensajes);

        inicializarComponentes();

        contenedorSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                cargarMensajes();
                contenedorSwipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarMensajes();
    }

    private void inicializarComponentes() {
        contenedorSwipeRefreshLayout = (SwipeRefreshLayout)
                findViewById(R.id.detalleMensajes_swipeRefreshLayout);
        mensajesRecyclerView = (RecyclerView)
                findViewById(R.id.detalleMensajes_mensajesRecyclerView);
    }

    private void cargarMensajes() {
        String remitente = getIntent().getStringExtra(REMITENTE);
        new CargarMensajesTask().execute(remitente);
    }

    private class CargarMensajesTask extends AsyncTask<String, String, List<String>> {

        private ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(DetalleMensajes.this);
            progreso.setIndeterminate(true);
            progreso.setTitle(getString(R.string.obteniendo_datos));
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected List<String> doInBackground(String... parametros) {
            publishProgress(getString(R.string.por_favor_espere));

            String nick = parametros[0];

            FactoryDAO factory = FactoryDAO.getOrCreate();
            UsuarioDAO dao = factory.newUsuarioDAO();

            Usuario objUsuario = Usuario.getInstancia();

            Usuario remitente = new Usuario();
            remitente.setNick(nick);

            List<String> mensajes = dao.cargarMensajes(objUsuario, remitente);

            return mensajes;
        }

        @Override
        protected void onPostExecute(List<String> mensajes) {
            super.onPostExecute(mensajes);

            progreso.dismiss();

            if (mensajes == null) {
                Snackbar.make(findViewById(R.id.detalleMensajes_contenedorCoordinatorLayout),
                        R.string.conexion_servidor_error, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }

            AdaptadorDetalleMensajes adaptador = new AdaptadorDetalleMensajes(mensajes);
            mensajesRecyclerView.setAdapter(adaptador);
            mensajesRecyclerView.setLayoutManager(new LinearLayoutManager(DetalleMensajes.this));
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            progreso.setMessage(values[0]);
        }

    }

}
