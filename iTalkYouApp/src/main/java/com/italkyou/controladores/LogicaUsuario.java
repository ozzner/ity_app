package com.italkyou.controladores;

import android.content.Context;

import com.italkyou.beans.BeanUsuario;
import com.italkyou.dao.UsuarioDAO;
import com.italkyou.utils.Const;

public class LogicaUsuario {

    /**
     * Metodo para almacenar,actualizar datos del usuario y del perfil.
     *
     * @param contexto contexto de la aplicacion.
     * @param usuario  es la entidad del usuario.
     * @param tipo     es una cadena que indica que operacion se va a realizar.
     */
    public static void guardarUsuario(Context contexto, BeanUsuario usuario, String tipo) {

        UsuarioDAO daoUsuario = new UsuarioDAO(contexto);

        if (tipo.equals(Const.BD_INSERTAR))
            daoUsuario.insertar(usuario);
        else if (tipo.equals(Const.BD_MODIFICARIS))
            daoUsuario.modificarInicioSesion(usuario);
        else if (tipo.equals(Const.BD_MODIFICARPU))
            daoUsuario.modificarPerfil(usuario);

    }

    public static void modificarDato(Context contexto, String columna, String dato, String anexo) {

        UsuarioDAO usu = new UsuarioDAO(contexto);
        usu.setDato(columna, dato, anexo);
//        usu.cerrar();
    }

    /**
     * Obtiene los datos del usuario conectado.
     *
     * @param contexto es el contexto de la aplicacion.
     * @return la entidad del Usuario.
     */
    public static BeanUsuario obtenerUsuario(Context contexto) {
        UsuarioDAO daoUsuario = new UsuarioDAO(contexto);
        BeanUsuario usuario = daoUsuario.obtenerUsuario();
//        daoUsuario.cerrar();
        return usuario;

    }

    public static boolean existeUsuarioActivo(Context contexto) {

        boolean existe = false;
        UsuarioDAO daoUsuario = new UsuarioDAO(contexto);
        int cont = daoUsuario.existeUsuarioActivo();
//        daoUsuario.cerrar();

        if (cont > 0)
            existe = true;

        return existe;
    }

    public static boolean existeUsuario(Context contexto, String anexo) {

        boolean existe = false;

        UsuarioDAO usu = new UsuarioDAO(contexto);
        int cont = usu.existeUsuario(anexo);

        if (cont > 0) existe = true;
//        usu.cerrar();

        return existe;

    }

    public static void borrarDatos(Context contexto) {
        UsuarioDAO usu = new UsuarioDAO(contexto);
        usu.borrarDatos();
//        usu.cerrar();
    }

    public static void actualizarSaldo(Context contexto, String anexo, String nuevoSaldo) {
        UsuarioDAO daoUsuario = new UsuarioDAO(contexto);
        daoUsuario.actualizarSaldo(anexo, nuevoSaldo);
//        daoUsuario.cerrar();
    }

    public static Double getLocalBalance(Context ctx, String userID) {
        UsuarioDAO dao = new UsuarioDAO(ctx);
        Double balance = dao.getBalance(userID);
        return balance;
    }

}
