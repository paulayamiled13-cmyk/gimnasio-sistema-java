package com.gimnasio.dao;

import com.gimnasio.config.Db;
import com.gimnasio.model.Producto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    public List<Producto> listar(String filtro) throws SQLException {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE nombre_producto LIKE ? OR descripcion LIKE ? ORDER BY id_producto DESC";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            String f = "%" + (filtro == null ? "" : filtro) + "%";
            ps.setString(1, f); ps.setString(2, f);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(map(rs));
            }
        }
        return lista;
    }

    public void insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO producto(id_usuario,nombre_producto,descripcion,precio,stock,estado) VALUES(?,?,?,?,?,?)";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdUsuario());
            ps.setString(2, p.getNombreProducto());
            ps.setString(3, p.getDescripcion());
            ps.setBigDecimal(4, p.getPrecio());
            ps.setInt(5, p.getStock());
            ps.setString(6, p.getEstado());
            ps.executeUpdate();
        }
    }

    public void actualizar(Producto p) throws SQLException {
        String sql = "UPDATE producto SET nombre_producto=?, descripcion=?, precio=?, stock=?, estado=? WHERE id_producto=?";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, p.getNombreProducto());
            ps.setString(2, p.getDescripcion());
            ps.setBigDecimal(3, p.getPrecio());
            ps.setInt(4, p.getStock());
            ps.setString(5, p.getEstado());
            ps.setInt(6, p.getIdProducto());
            ps.executeUpdate();
        }
    }

    public void desactivar(int idProducto) throws SQLException {
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement("UPDATE producto SET estado='Inactivo' WHERE id_producto=?")) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }

    private Producto map(ResultSet rs) throws SQLException {
        return new Producto(
            rs.getInt("id_producto"),
            rs.getInt("id_usuario"),
            rs.getString("nombre_producto"),
            rs.getString("descripcion"),
            rs.getBigDecimal("precio"),
            rs.getInt("stock"),
            rs.getString("estado")
        );
    }
}
