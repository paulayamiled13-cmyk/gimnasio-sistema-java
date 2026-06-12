package com.gimnasio.dao;

import com.gimnasio.config.Db;
import com.gimnasio.model.Usuario;
import com.gimnasio.util.PasswordUtil;
import java.sql.*;

public class UsuarioDAO {
    public Usuario login(String usuario, String clave) throws SQLException {
        String sql = "SELECT id_usuario, nombre_usuario, rol, estado FROM usuario " +
                     "WHERE nombre_usuario = ? AND contrasena_hash = ? AND estado = 'Activo'";
        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, PasswordUtil.sha256(clave));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id_usuario"),
                            rs.getString("nombre_usuario"),
                            rs.getString("rol"),
                            rs.getString("estado")
                    );
                }
            }
        }
        return null;
    }
}
