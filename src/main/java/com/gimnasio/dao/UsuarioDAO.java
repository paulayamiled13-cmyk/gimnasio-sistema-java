package com.gimnasio.dao;

import com.gimnasio.config.Db;
import com.gimnasio.model.Usuario;
import com.gimnasio.util.PasswordUtil;
import java.sql.*;

public class UsuarioDAO {

    public Usuario login(String usuario, String clave) throws SQLException {

        /*
         * La contraseña NO se guarda en la base de datos.
         * El sistema solo valida:
         * 1. Que el usuario exista.
         * 2. Que el usuario esté activo.
         * 3. Que la contraseña ingresada cumpla las reglas de seguridad.
         */

        if (!PasswordUtil.validarPassword(clave)) {
            return null;
        }

        String sql = "SELECT id_usuario, nombre_usuario, rol, estado " +
                "FROM usuario " +
                "WHERE nombre_usuario = ? AND estado = 'Activo'";

        try (Connection cn = Db.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, usuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre_usuario"),
                            rs.getString("rol"),
                            rs.getString("estado"));
                }
            }
        }

        return null;
    }
}