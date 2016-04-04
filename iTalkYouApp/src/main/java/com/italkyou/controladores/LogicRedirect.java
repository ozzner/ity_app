package com.italkyou.controladores;

import android.content.Context;
import com.italkyou.beans.BeanRedirect;
import com.italkyou.dao.RedirectDAO;

/**
 * Created by rsantillanc on 19/08/2015.
 */
public class LogicRedirect {


    /**
     * @param bean Objeto con los datos a insertar.
     * @param c Contexto para BaseDAO.
     * @return Si es mayor de 0, se inserto el registro.
     */
    public static int insert(BeanRedirect bean, Context c){
        return new RedirectDAO(c).insertRedirect(bean);
    }



    /**
     * @param userId Es usado para el filtro where en la consulta
     * @param c Contexto para BaseDAO.
     * @return verdadero si existe el registros de redireccionamiento del usuario en question
     */
    public static boolean checkIfExist(String userId,Context c){
        return new RedirectDAO(c).checkIfExist(userId);
    }

    /**
     * Actualiza la tabla de anexos redireccionados correspondiente al ususario.
     * @param bean Objeto con los datos proporcioados.
     * @param c Contexto de la aplicación, para BaseDAO.
     * @return un numero positivo si es exitoso.
     */
    public static int update(BeanRedirect bean,Context c){
        return new RedirectDAO(c).updateRedirect(bean);
    }

    /**
     * Obtiene un objeto BeanRedirect con los datos que se requiere desde la base de
     * datos interna del dispositivo.
     *
     * @param userId Parámetro para realizar la búsqueda como campo unico.
     * @param c Contexto de la aplicacion, usado para BaseDAO.
     * @return Retorna BeanRedirect.
     */
    public static BeanRedirect getRedirect(String userId,Context c){
        return new RedirectDAO(c).getRedirect(userId);
    }


}
