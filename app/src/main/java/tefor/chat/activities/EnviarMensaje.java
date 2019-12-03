package tefor.chat.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import tefor.chat.R;
import tefor.chat.dao.FactoryDAO;
import tefor.chat.dao.UsuarioDAO;
import tefor.chat.dto.Usuario;

// TODO enviar mensaje se muestra en fragmento y no en activity
public class EnviarMensaje extends AppCompatActivity implements View.OnClickListener {

    private AutoCompleteTextView nickAutoCompleteTextView;
    private EditText mensajeEditText;
    private Button enviarMensajeButton;
    private TextView caracteresRestantesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enviar_mensaje);

        inicializarComponentes();
        enviarMensajeButton.setOnClickListener(this);
        nickAutoCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String texto = String.format(getString(R.string.caracteres_restantes), s.length());
                caracteresRestantesTextView.setText(texto);

               if (s.length() >= 2) {
                   String nombre = nickAutoCompleteTextView.getText().toString().trim();
                   new CargarUsuariosTask().execute(nombre);
               }
            }

        });
    }

    private void inicializarComponentes() {
        nickAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.enviarMensaje_nickEditText);
        mensajeEditText = (EditText) findViewById(R.id.enviarMensaje_mensajeEditText);
        enviarMensajeButton = (Button) findViewById(R.id.enviarMensaje_enviarButton);
        caracteresRestantesTextView =
                (TextView) findViewById(R.id.enviarMensaje_caracteresRestantesTextView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enviarMensaje_enviarButton:
                enviarMensaje();
        }
    }

    private void enviarMensaje() {
        boolean esValido = true;

        String destinatario = nickAutoCompleteTextView.getText().toString().trim();
        if (destinatario.isEmpty()) {
            nickAutoCompleteTextView.setError(getString(R.string.destinatario_vacio_error));
            esValido = false;
        }

        String mensaje = mensajeEditText.getText().toString().trim();
        if (mensaje.isEmpty()) {
            mensajeEditText.setError(getString(R.string.mensaje_vacio_error));
            esValido = false;
        }

        if (!esValido) {
            return;
        }

        new EnviarMensajeTask().execute(destinatario, mensaje);
    }

    private class EnviarMensajeTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... parametros) {
            String nick = parametros[0];
            String mensaje = parametros[1];

            FactoryDAO factory = FactoryDAO.getOrCreate();
            UsuarioDAO dao = factory.newUsuarioDAO();

            Usuario obj = Usuario.getInstancia();

            Usuario destinatario = new Usuario();
            destinatario.setNick(nick);

            String estado = dao.enviarMensaje(obj, destinatario, mensaje);

            return estado;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null) {
                Snackbar.make(findViewById(R.id.enviarMensaje_contenedorLinearLayout),
                        R.string.conexion_servidor_error, Snackbar.LENGTH_INDEFINITE).show();
                return;
            }

            if (s.equals("usuario_inexistente")) {
                Snackbar.make(findViewById(R.id.enviarMensaje_contenedorLinearLayout),
                        "No se encontró al destinatario", Snackbar.LENGTH_INDEFINITE).show();
                return;
            }

            finish();
        }

    }

    private class CargarUsuariosTask extends AsyncTask<String, String, List<String>> {

        @Override
        protected List<String> doInBackground(String... parametros) {
            String letras = parametros[0];

            FactoryDAO factory = FactoryDAO.getOrCreate();
            UsuarioDAO dao = factory.newUsuarioDAO();

            List<String> lista = dao.cargarUsuarios(letras);

            return lista;
        }

        @Override
        protected void onPostExecute(List<String> lista) {
            super.onPostExecute(lista);

            if (lista == null) {
                return;
            }

            ArrayAdapter<String> adaptador = new ArrayAdapter<>(EnviarMensaje.this,
                    android.R.layout.simple_dropdown_item_1line, lista);
            nickAutoCompleteTextView.setAdapter(adaptador);
        }

    }

}
