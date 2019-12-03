package tefor.chat.dao;

import java.util.List;

import tefor.chat.dto.DTO;
import tefor.chat.dto.Usuario;

public abstract class UsuarioDAO implements IDAO {

    /**
     * Devuelve una lista con todos los registros de la tabla
     * Usuario
     */
    public abstract List<Usuario> seleccionarTodos();

    public abstract String ingresar(DTO obj);

    public abstract List<Usuario> cargarChats(DTO obj);

    public abstract List<String> cargarUltimosMensajes(DTO obj);

    public abstract String enviarMensaje(DTO usuario, DTO destinatario, Object llave);

    public abstract List<String> cargarMensajes(DTO usuario, DTO destinatario);

    public abstract List<String> cargarUsuarios(Object llave);

}