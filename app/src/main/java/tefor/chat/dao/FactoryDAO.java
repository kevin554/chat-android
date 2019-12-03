package tefor.chat.dao;

/**
 * La idea es que a la hora de migrar a otro SGBD (a futuro) no
 * se modifique ninguna linea de codigo de la aplicacion, sino,
 * simplemente agregar la conexion al nuevo SGBD, su respectiva
 * implementacion DAO y modificar la instancia que esta clase
 * devuelve
 */
public abstract class FactoryDAO {

    private static FactoryDAO instancia;

    public static FactoryDAO getOrCreate() {
		if (instancia == null) {
		    instancia = new tefor.chat.dao.web.FactoryDAO();
		}

		return instancia;
    }

	protected FactoryDAO() {
    }

    public abstract UsuarioDAO newUsuarioDAO();

}