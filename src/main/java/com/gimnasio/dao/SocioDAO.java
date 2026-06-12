package com.gimnasio.dao;

import com.gimnasio.config.Db;
import com.gimnasio.model.Socio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {
    public List<Socio> listar(String filtro) throws SQLException {
        List<Socio> lista = new ArrayList<>();
        String sql = "SELECT * FROM socio WHERE dni LIKE ? OR nombres LIKE ? OR apellidos LIKE ? ORDER BY id_socio DESC";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            String f = "%" + (filtro == null ? "" : filtro) + "%";
            ps.setString(1, f); ps.setString(2, f); ps.setString(3, f);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    public Socio buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT * FROM socio WHERE dni = ?";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    public void insertar(Socio s) throws SQLException {
        String sql = "INSERT INTO socio(id_usuario,nombres,apellidos,dni,telefono,correo,direccion,estado) VALUES(?,?,?,?,?,?,?,?)";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, s.getIdUsuario());
            ps.setString(2, s.getNombres());
            ps.setString(3, s.getApellidos());
            ps.setString(4, s.getDni());
            ps.setString(5, s.getTelefono());
            ps.setString(6, s.getCorreo());
            ps.setString(7, s.getDireccion());
            ps.setString(8, s.getEstado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Socio s) throws SQLException {
        String sql = "UPDATE socio SET nombres=?, apellidos=?, dni=?, telefono=?, correo=?, direccion=?, estado=? WHERE id_socio=?";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, s.getNombres());
            ps.setString(2, s.getApellidos());
            ps.setString(3, s.getDni());
            ps.setString(4, s.getTelefono());
            ps.setString(5, s.getCorreo());
            ps.setString(6, s.getDireccion());
            ps.setString(7, s.getEstado());
            ps.setInt(8, s.getIdSocio());
            ps.executeUpdate();
        }
    }

    public void darBaja(int idSocio) throws SQLException {
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement("UPDATE socio SET estado='Inactivo' WHERE id_socio=?")) {
            ps.setInt(1, idSocio);
            ps.executeUpdate();
        }
    }

    private Socio map(ResultSet rs) throws SQLException {
        return new Socio(
                rs.getInt("id_socio"),
                rs.getInt("id_usuario"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("dni"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("direccion"),
                rs.getString("estado")
        );
    }
}
