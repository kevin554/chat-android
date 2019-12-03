package tefor.chat.dao.web;

import tefor.chat.dao.UsuarioDAO;

/**
 * Devuelve implementaciones DAO para el SGBD SQLite
 */
public class FactoryDAO extends tefor.chat.dao.FactoryDAO {

    @Override
    public UsuarioDAO newUsuarioDAO() {
		return new tefor.chat.dao.web.UsuarioDAO();
    }

}