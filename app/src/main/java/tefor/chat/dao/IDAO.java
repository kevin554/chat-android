package tefor.chat.dao;

import tefor.chat.dto.DTO;

/**
 * Una interfaz generica con las operaciones basicas (Insertar,
 * Actualizar, Eliminar, Seleccionar) en una base de datos.
 * 
 * Los objetos que necesiten usar esta interfaz, deben extender
 * de la clase abstracta DTO
*/
interface IDAO {

    /**
     * Selecciona un registro de la base de datos
     * @param llave El ID (llave primaria) del registro
     */
    DTO seleccionar(Object llave);

    /**
     * Inserta un registro a la base de datos
     * @param obj El objeto a insertar
     */
    void insertar(DTO obj);

    /**
     * Actualiza un regitro de la base de datos
     * @param obj El objeto a actualizar
     */
    void actualizar(DTO obj);

    /**
     * Elimina un registro de la base de datos
     * @param obj El objeto a eliminar
     */
    void eliminar(DTO obj);

}