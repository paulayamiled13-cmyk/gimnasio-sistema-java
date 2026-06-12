package com.gimnasio.dao;

import com.gimnasio.config.Db;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PagoDAO {

    public void registrar(int idSocio, int idMembresia, LocalDate fechaPago, BigDecimal monto, String metodo) throws SQLException {
        String sql = """
                INSERT INTO pago
                (id_socio, id_membresia, fecha_pago, monto, metodo_pago, estado_pago)
                VALUES (?, ?, ?, ?, ?, 'Registrado')
                """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idSocio);
            ps.setInt(2, idMembresia);
            ps.setDate(3, Date.valueOf(fechaPago));
            ps.setBigDecimal(4, monto);
            ps.setString(5, metodo);

            ps.executeUpdate();
        }
    }

    public void anular(int idPago) throws SQLException {
        String sql = """
                UPDATE pago
                SET estado_pago = 'Anulado'
                WHERE id_pago = ?
                """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idPago);
            ps.executeUpdate();
        }
    }

    public List<Object[]> listar() throws SQLException {
        List<Object[]> lista = new ArrayList<>();

        String sql = """
                SELECT
                    p.id_pago,
                    s.dni,
                    CONCAT(s.nombres, ' ', s.apellidos) AS socio,
                    p.fecha_pago,
                    p.monto,
                    p.metodo_pago,
                    p.estado_pago
                FROM pago p
                INNER JOIN socio s ON s.id_socio = p.id_socio
                ORDER BY p.id_pago DESC
                """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_pago"),
                        rs.getString("dni"),
                        rs.getString("socio"),
                        rs.getDate("fecha_pago"),
                        rs.getBigDecimal("monto"),
                        rs.getString("metodo_pago"),
                        rs.getString("estado_pago")
                });
            }
        }

        return lista;
    }
}