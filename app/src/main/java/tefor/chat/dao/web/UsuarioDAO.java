package tefor.chat.dao.web;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import tefor.chat.clienteHTTP.HttpConnection;
import tefor.chat.clienteHTTP.MethodType;
import tefor.chat.clienteHTTP.RequestConfiguration;
import tefor.chat.clienteHTTP.StandarRequestConfiguration;
import tefor.chat.dto.DTO;
import tefor.chat.dto.Usuario;
import tefor.chat.utiles.Contexto;

/**
 * La implementacion DAO para SQLite de la tabla Usuario
 */
class UsuarioDAO extends tefor.chat.dao.UsuarioDAO {

    // private static final String URL = "http://192.168.43.163:8080/Chat/ProcesadorServlet";
    private static final String URL = "http://192.168.43.32:8080/Chat/ProcesadorServlet";
    // private static final String URL = "http://192.168.0.13:8080/Chat/ProcesadorServlet";
    private static final String CODIGO_ID = "codigoID";
    private static final String NICK = "nick";
    private static final String CONTRASENA = "contrasena";
    private static final String TOKEN = "token";

    @Override
    public DTO seleccionar(Object llave) {
        if (!(llave instanceof String)) {
            throw new IllegalArgumentException("La llave debe ser una cadena de texto");
        }

        String nick = String.valueOf(llave);

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "seleccionar");
        parametros.put("nick", nick);

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String objStr = HttpConnection.sendRequest(configuracion);

        Usuario objUsuario = null;
        try {
            objUsuario = obtenerUsuarioDeObjJson(new JSONObject(objStr));
        } catch (JSONException e) {
            Log.e(Contexto.APP_TAG, e.getMessage());
        }

        return objUsuario;
    }

    @Override
    public void insertar(DTO obj) {
        if (obj == null) {
            throw new IllegalArgumentException("El objeto a insertar no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) obj;

		if (objUsuario.getNick().trim().isEmpty()) {
		    throw new IllegalArgumentException("El nick no puede estar vacio");
		}

		if (objUsuario.getContrasena().trim().isEmpty()) {
		    throw new IllegalArgumentException("El contraseña no puede estar vacia");
		}

		if (objUsuario.getToken().trim().isEmpty()) {
		    throw new IllegalArgumentException("El token no puede estar vacio");
		}

		Gson gson = new Gson();

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "insertar");
        parametros.put("obj", gson.toJson(objUsuario));

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String respuesta = HttpConnection.sendRequest(configuracion);

        if (respuesta == null) {
            Log.e(Contexto.APP_TAG, "No se pudo establecer la conexion con el servidor");
        }

        try {
            objUsuario.setCodigoID(Integer.parseInt(respuesta));
        } catch (NumberFormatException e) {
            Log.e(Contexto.APP_TAG, "Hubo un error al colocar el ID al usuario... El servidor no " +
                    "devolvió un numero entero");
        }
    }

    @Override
    public void actualizar(DTO obj) {
        if (obj == null) {
            throw new IllegalArgumentException("El objeto a actualizar no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) obj;

		if (objUsuario.getCodigoID() <= 0) {
		    throw new IllegalArgumentException("El ID no puede ser menor o igual que cero");
		}

		if (objUsuario.getNick().trim().isEmpty()) {
		    throw new IllegalArgumentException("El nick no puede estar vacio");
		}

		if (objUsuario.getContrasena().trim().isEmpty()) {
		    throw new IllegalArgumentException("La contraseña no puede estar vacia");
		}

		if (objUsuario.getToken().trim().isEmpty()) {
		    throw new IllegalArgumentException("El token no puede estar vacio");
		}

        Gson gson = new Gson();

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "actualizar");
        parametros.put("obj", gson.toJson(objUsuario));

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        HttpConnection.sendRequest(configuracion);
    }

    @Override
    public void eliminar(DTO obj) {
        if (obj == null) {
            throw new IllegalArgumentException("El objeto no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) obj;
        if (objUsuario.getCodigoID() < 0) {
            throw new IllegalArgumentException("No se puede eliminar un usuario con ID <= 0");
        }

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "eliminar");
        parametros.put("id", Integer.toString(objUsuario.getCodigoID()));

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        HttpConnection.sendRequest(configuracion);
    }

    @Override
    public List<Usuario> seleccionarTodos() {
        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "todos");

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String usuarios = HttpConnection.sendRequest(configuracion);

        List<Usuario> lista = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(usuarios);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Usuario usuario = obtenerUsuarioDeObjJson(jsonObject);

                lista.add(usuario);
            }
        } catch (JSONException e) {
            Log.e(Contexto.APP_TAG, "Hubo un error al obtener los usuarios de un JSON Array");
        }

        return lista;
    }

    @Override
    public String ingresar(DTO obj) {
        if (obj == null) {
            throw new IllegalArgumentException("El objeto a ingresar no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) obj;

        if (objUsuario.getNick().trim().isEmpty()) {
            throw new IllegalArgumentException("El nick no puede estar vacio");
        }

        if (objUsuario.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("El contraseña no puede estar vacia");
        }

        if (objUsuario.getToken().trim().isEmpty()) {
            throw new IllegalArgumentException("El token no puede estar vacio");
        }

        Gson gson = new Gson();

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "ingresar");
        parametros.put("obj", gson.toJson(objUsuario));

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        return HttpConnection.sendRequest(configuracion);
    }

    @Override
    public List<Usuario> cargarChats(DTO obj) {
        if (obj == null) {
            throw new IllegalArgumentException("El objeto no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) obj;

        if (objUsuario.getNick().trim().isEmpty()) {
            throw new IllegalArgumentException("El nick no puede estar vacio");
        }

        if (objUsuario.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("El contraseña no puede estar vacia");
        }

        if (objUsuario.getToken().trim().isEmpty()) {
            throw new IllegalArgumentException("El token no puede estar vacio");
        }

        Gson gson = new Gson();

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "cargar_chats");
        parametros.put("obj", gson.toJson(objUsuario));

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String usuarios = HttpConnection.sendRequest(configuracion);

        if (usuarios == null) {
            Log.e(Contexto.APP_TAG, "No se pudo establecer la conexion con el servidor");
            return null;
        }

        List<Usuario> lista = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(usuarios);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Usuario usuario = obtenerUsuarioDeObjJson(jsonObject);

                lista.add(usuario);
            }
        } catch (JSONException e) {
            Log.e(Contexto.APP_TAG, "Hubo un error al obtener los usuarios de un JSON Array");
        }

        return lista;
    }

    @Override
    public List<String> cargarUltimosMensajes(DTO obj) {
        if (obj == null) {
            throw new IllegalArgumentException("El objeto no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) obj;

        if (objUsuario.getNick().trim().isEmpty()) {
            throw new IllegalArgumentException("El nick no puede estar vacio");
        }

        if (objUsuario.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("El contraseña no puede estar vacia");
        }

        if (objUsuario.getToken().trim().isEmpty()) {
            throw new IllegalArgumentException("El token no puede estar vacio");
        }

        Gson gson = new Gson();

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "cargar_ultimos_mensajes");
        parametros.put("obj", gson.toJson(objUsuario));

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String mensajes = HttpConnection.sendRequest(configuracion);

        if (mensajes == null) {
            Log.e(Contexto.APP_TAG, "No se pudo establecer la conexion con el servidor");
            return null;
        }

        List<String> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(mensajes);

            for (int i = 0; i < jsonArray.length(); i++) {
                lista.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(Contexto.APP_TAG, "Hubo un error al obtener los mensajes mediante un JSON Array");
        }

        return lista;
    }

    @Override
    public String enviarMensaje(DTO usuario, DTO destinatario, Object llave) {
        if (usuario == null) {
            throw new IllegalArgumentException("El objeto a insertar no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) usuario;

        if (objUsuario.getNick().trim().isEmpty()) {
            throw new IllegalArgumentException("El nick no puede estar vacio");
        }

        if (objUsuario.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("El contraseña no puede estar vacia");
        }

        if (objUsuario.getToken().trim().isEmpty()) {
            throw new IllegalArgumentException("El token no puede estar vacio");
        }

        if (destinatario == null) {
            throw new IllegalArgumentException("El objeto a insertar no puede ser nulo");
        }

        Usuario objDestinatario = (Usuario) destinatario;

        if (objDestinatario.getNick().trim().isEmpty()) {
            throw new IllegalArgumentException("El nick no puede estar vacio");
        }

        if (!(llave instanceof String)) {
            throw new IllegalArgumentException("El mensaje debe ser una cadena de texto");
        }

        String mensaje = String.valueOf(llave);

        Gson gson = new Gson();

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "enviar_mensaje");
        parametros.put("obj", gson.toJson(objUsuario));
        parametros.put("destinatario", gson.toJson(objDestinatario));
        parametros.put("mensaje", mensaje);

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String respuesta = HttpConnection.sendRequest(configuracion);

        return respuesta;
    }

    @Override
    public List<String> cargarMensajes(DTO usuario, DTO destinatario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El objeto a insertar no puede ser nulo");
        }

        Usuario objUsuario = (Usuario) usuario;

        if (objUsuario.getNick().trim().isEmpty()) {
            throw new IllegalArgumentException("El nick no puede estar vacio");
        }

        if (objUsuario.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("El contraseña no puede estar vacia");
        }

        if (objUsuario.getToken().trim().isEmpty()) {
            throw new IllegalArgumentException("El token no puede estar vacio");
        }

        if (destinatario == null) {
            throw new IllegalArgumentException("El objeto a insertar no puede ser nulo");
        }

        Usuario objRemitente = (Usuario) destinatario;

        if (objRemitente.getNick().trim().isEmpty()) {
            throw new IllegalArgumentException("El nick no puede estar vacio");
        }

        Gson gson = new Gson();

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "cargar_mensajes");
        parametros.put("obj", gson.toJson(objUsuario));
        parametros.put("remitente", gson.toJson(objRemitente));

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String mensajes = HttpConnection.sendRequest(configuracion);

        if (mensajes == null) {
            Log.e(Contexto.APP_TAG, "No se pudo establecer la conexion con el servidor");
            return null;
        }

        List<String> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(mensajes);

            for (int i = 0; i < jsonArray.length(); i++) {
                lista.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(Contexto.APP_TAG, "Hubo un error al obtener los mensajes mediante un JSON Array");
        }

        return lista;
    }

    @Override
    public List<String> cargarUsuarios(Object llave) {
        if (!(llave instanceof String)) {
            throw new IllegalArgumentException("El parametro debe ser una cadena de texto");
        }

        String nombre = (String) llave;

        Hashtable<String, String> parametros = new Hashtable<>();
        parametros.put("evento", "cargar_usuarios");
        parametros.put("nombre", nombre);

        RequestConfiguration configuracion =
                new StandarRequestConfiguration(URL, MethodType.GET, parametros);

        String usuarios = HttpConnection.sendRequest(configuracion);

        if (usuarios == null) {
            Log.e(Contexto.APP_TAG, "No se pudo establecer la conexion con el servidor");
            return null;
        }

        List<String> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(usuarios);

            for (int i = 0; i < jsonArray.length(); i++) {
                lista.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            Log.e(Contexto.APP_TAG, "Hubo un error al obtener los usuarios mediante un JSON Array");
        }

        return lista;
    }

    private Usuario obtenerUsuarioDeObjJson(JSONObject jsonObject) throws JSONException {
        Usuario objUsuario = new Usuario();

        objUsuario.setCodigoID(jsonObject.getInt(CODIGO_ID));
        objUsuario.setNick(jsonObject.getString(NICK));
        objUsuario.setContrasena(jsonObject.getString(CONTRASENA));
        objUsuario.setToken(jsonObject.getString(TOKEN));

        return objUsuario;
    }

}