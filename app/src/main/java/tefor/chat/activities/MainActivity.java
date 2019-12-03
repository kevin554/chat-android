package tefor.chat.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import tefor.chat.R;
import tefor.chat.dao.FactoryDAO;
import tefor.chat.dao.UsuarioDAO;
import tefor.chat.dto.Usuario;
import tefor.chat.utiles.Contexto;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button crearCuentaButton, entrarButton;
    private TextInputEditText nickTextInputEditText, contraseñaTextInputEditText;
    private CheckBox recordarmeCheckBox;
    private TextView contraseñaOlvidadaTextView, caracteresRestantesTextView;

    public static final int CUENTA_INEXISTENTE = -1;
    public static final int CONTRASEÑA_INCORRECTA = -2;
    public static final int INGRESO_CORRECTO = -3;
    public static final int CUENTA_EN_USO = -4;
    public static final int NO_HAY_CONEXION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();
        crearCuentaButton.setOnClickListener(this);
        entrarButton.setOnClickListener(this);
        contraseñaOlvidadaTextView.setOnClickListener(this);
        nickTextInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable componente) {
                String texto = String.format(getString(R.string.caracteres_restantes), componente.length());
                caracteresRestantesTextView.setText(texto);
            }

        });

        obtenerUsuarioDePreferencias();

        if (Usuario.getInstancia() != null) {
            new AutenticarUsuarioTask().execute(Usuario.getInstancia());
        }
    }

    private void inicializarComponentes() {
        crearCuentaButton = (Button) findViewById(R.id.activityMain_crearCuentaButton);
        entrarButton = (Button) findViewById(R.id.activityMain_entrarButton);
        nickTextInputEditText = (TextInputEditText)
                findViewById(R.id.activityMain_nickTextInputEditTet);
        contraseñaTextInputEditText = (TextInputEditText)
                findViewById(R.id.activityMain_contraseñaTextInputEditText);
        recordarmeCheckBox = (CheckBox) findViewById(R.id.activityMain_recordarmeCheckBox);
        contraseñaOlvidadaTextView =
                (TextView) findViewById(R.id.activityMain_contraseñaOlvidadaTextView);
        caracteresRestantesTextView =
                (TextView) findViewById(R.id.activityMain_caracteresRestantesTextView);
    }

    @Override
    public void onClick(View v) {
        // Oculta el teclado
        InputMethodManager manager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        switch (v.getId()) {
            case R.id.activityMain_crearCuentaButton:
                crearCuenta();
                break;

            case R.id.activityMain_entrarButton:
                Usuario obj = obtenerUsuarioDeFormulario();

                if (obj != null) {
                    new AutenticarUsuarioTask().execute(obj);
                }

                break;

            case R.id.activityMain_contraseñaOlvidadaTextView:
                Snackbar.make(v, R.string.contraseña_olvidada, Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    private Usuario obtenerUsuarioDeFormulario() {
        boolean esValido = true;

        String nick = nickTextInputEditText.getText().toString().trim();
        if (nick.isEmpty()) {
            nickTextInputEditText.setError(getString(R.string.nick_vacio_error));
            esValido = false;
        }

        String contraseña = contraseñaTextInputEditText.getText().toString().trim();
        if (contraseña.isEmpty()) {
            contraseñaTextInputEditText.setError(getString(R.string.contraseña_vacia_error));
            esValido = false;
        }

        if (!esValido) {
            return null;
        }

        String token = FirebaseInstanceId.getInstance().getToken();

        Usuario objUsuario = new Usuario();
        objUsuario.setNick(nick);
        objUsuario.setContrasena(contraseña);
        objUsuario.setToken(token);

        return objUsuario;
    }

//    public User readUser() {
//        SharedPreferences prefs = this.context.getSharedPreferences("user", this.context.MODE_PRIVATE);
//        String s = prefs.getString("user", null);
//        return s != null ? new Gson().fromJson(s, User.class) : null;
//    }

//    public void writeUser(User user) {
//        SharedPreferences prefs = this.context.getSharedPreferences("user", this.context.MODE_PRIVATE);
//        SharedPreferences.Editor ed = prefs.edit();
//        ed.putString("user", new Gson().toJson(user));
//        ed.commit();
//    }

//    ls(this).readUser() != null) {
//        userCn = new Utils(this).readUser();
//    }

    private void obtenerUsuarioDePreferencias() {
        SharedPreferences preferencias = getSharedPreferences("preferencias", Context.MODE_PRIVATE);

        int ID = preferencias.getInt("ID", 0);
        String nick = preferencias.getString("nick", "");
        String contraseña = preferencias.getString("contraseña", "");
        String token = preferencias.getString("token", "");

        if (nick.isEmpty() || contraseña.isEmpty() || token.isEmpty()) {
            return;
        }

        Usuario.setInstancia(new Usuario(ID, nick, contraseña, token));
    }

    private void crearCuenta() {
        Usuario usuario = obtenerUsuarioDeFormulario();

        if (usuario != null) {
            new CrearCuentaTask().execute(usuario);
        }
    }

    private void ingresar() {
        Intent intent = new Intent(getApplicationContext(), ListaMensajes.class);
        startActivity(intent);
    }

    private class AutenticarUsuarioTask extends AsyncTask<Usuario, String, String> {

        private ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(MainActivity.this);
            progreso.setIndeterminate(true);
            progreso.setTitle(getString(R.string.obteniendo_datos));
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Usuario... usuarios) {
            publishProgress(getString(R.string.por_favor_espere));

            Usuario usuario = usuarios[0];

            FactoryDAO factory = FactoryDAO.getOrCreate();
            UsuarioDAO dao = factory.newUsuarioDAO();

            String respuesta = dao.ingresar(usuario);

            if (respuesta == null) {
                Log.e(Contexto.APP_TAG, "No se pudo establecer la conexion con el servidor");
                return String.valueOf(NO_HAY_CONEXION);
            }

            try { // intento obtener los errores
                Integer.parseInt(respuesta);
            } catch (NumberFormatException e) { // no hubo errores
                usuario = (Usuario) dao.seleccionar(respuesta);
                Usuario.setInstancia(usuario);

                return String.valueOf(INGRESO_CORRECTO);
            }

            return respuesta;
        }

        @Override
        protected void onPostExecute(String respuesta) {
            super.onPostExecute(respuesta);

            progreso.dismiss();

            switch (Integer.parseInt(respuesta)) {
                case CUENTA_INEXISTENTE:
                    nickTextInputEditText.setError(getString(R.string.nick_inexistente_error));
                    return;

                case CONTRASEÑA_INCORRECTA:
                    contraseñaTextInputEditText.setError(getString(
                            R.string.contraseña_incorrecta_error));
                    return;

                case NO_HAY_CONEXION:
                    Snackbar.make(findViewById(R.id.activityMain_contenedorCoordinatorLayout),
                            R.string.conexion_servidor_error, Snackbar.LENGTH_INDEFINITE).show();
                    return;

                case INGRESO_CORRECTO:
                    guardarPreferencia();
                    ingresar();

                    break;
            }
        }

        private void guardarPreferencia() {
            Usuario obj = Usuario.getInstancia();

            SharedPreferences preferencias =
                    getSharedPreferences("preferencias", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preferencias.edit();

            if (recordarmeCheckBox.isChecked()) {
                editor.putInt("ID", obj.getCodigoID());
                editor.putString("nick", obj.getNick());
                editor.putString("contraseña", obj.getContrasena());
                editor.putString("token", obj.getToken());
            } else {
                editor.putInt("ID", 0);
                editor.putString("nick", "");
                editor.putString("contraseña", "");
                editor.putString("token", "");
            }

            editor.commit();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            progreso.setMessage(values[0]);
        }

    }

    private class CrearCuentaTask extends AsyncTask<Usuario, String, String> {

        private ProgressDialog progreso;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progreso = new ProgressDialog(MainActivity.this);
            progreso.setIndeterminate(false);
            progreso.setMessage(getString(R.string.obteniendo_datos));
            progreso.setCancelable(false);
            progreso.show();
        }

        @Override
        protected String doInBackground(Usuario... usuarios) {
            publishProgress(getString(R.string.por_favor_espere));

            Usuario usuario = usuarios[0];

            FactoryDAO factory = FactoryDAO.getOrCreate();
            UsuarioDAO dao = factory.newUsuarioDAO();

            dao.insertar(usuario);

            return Integer.toString(usuario.getCodigoID());
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            progreso.dismiss();

            switch (Integer.parseInt(s)) {
                case NO_HAY_CONEXION:
                    Snackbar.make(findViewById(R.id.activityMain_contenedorCoordinatorLayout),
                            R.string.conexion_servidor_error, Snackbar.LENGTH_INDEFINITE).show();
                    return;

                case CUENTA_EN_USO:
                    nickTextInputEditText.setError(getString(R.string.nick_registrado_error));
                    return;

                default:
                    nickTextInputEditText.setText("");
                    contraseñaTextInputEditText.setText("");
                    Snackbar.make(findViewById(R.id.activityMain_contenedorCoordinatorLayout),
                            R.string.cuenta_registrada, Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            progreso.setMessage(values[0]);
        }

    }

}
