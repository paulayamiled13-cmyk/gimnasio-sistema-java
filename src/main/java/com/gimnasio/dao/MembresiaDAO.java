package com.gimnasio.dao;

import com.gimnasio.config.Db;
import com.gimnasio.model.Membresia;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembresiaDAO {

    public void registrar(Membresia m) throws SQLException {
        String sql = """
                INSERT INTO membresia
                (id_socio, tipo_membresia, fecha_inicio, fecha_vencimiento, costo, estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, m.getIdSocio());
            ps.setString(2, m.getTipoMembresia());
            ps.setDate(3, Date.valueOf(m.getFechaInicio()));
            ps.setDate(4, Date.valueOf(m.getFechaVencimiento()));
            ps.setBigDecimal(5, m.getCosto());
            ps.setString(6, m.getEstado());

            ps.executeUpdate();
        }
    }

    public void actualizarEstadosVencidos() throws SQLException {
        String sql = """
                UPDATE membresia
                SET estado = 'Vencida'
                WHERE fecha_vencimiento < CURRENT_DATE
                AND estado = 'Vigente'
                """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.executeUpdate();
        }
    }

    public Membresia membresiaActivaPorSocio(int idSocio) throws SQLException {
        actualizarEstadosVencidos();

        String sql = """
                SELECT id_membresia, id_socio, tipo_membresia, fecha_inicio,
                       fecha_vencimiento, costo, estado
                FROM membresia
                WHERE id_socio = ?
                AND estado = 'Vigente'
                AND fecha_vencimiento >= CURRENT_DATE
                ORDER BY id_membresia DESC
                LIMIT 1
                """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, idSocio);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }

        return null;
    }

    public List<Object[]> listarConSocios() throws SQLException {
        actualizarEstadosVencidos();

        List<Object[]> lista = new ArrayList<>();

        String sql = """
                SELECT
                    m.id_membresia,
                    s.dni,
                    CONCAT(s.nombres, ' ', s.apellidos) AS socio,
                    m.tipo_membresia,
                    m.fecha_inicio,
                    m.fecha_vencimiento,
                    m.costo,
                    m.estado
                FROM membresia m
                INNER JOIN socio s ON s.id_socio = m.id_socio
                ORDER BY m.id_membresia DESC
                """;

        try (Connection cn = Db.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                        rs.getInt("id_membresia"),
                        rs.getString("dni"),
                        rs.getString("socio"),
                        rs.getString("tipo_membresia"),
                        rs.getDate("fecha_inicio"),
                        rs.getDate("fecha_vencimiento"),
                        rs.getBigDecimal("costo"),
                        rs.getString("estado")
                });
            }
        }

        return lista;
    }

    private Membresia map(ResultSet rs) throws SQLException {
        return new Membresia(
                rs.getInt("id_membresia"),
                rs.getInt("id_socio"),
                rs.getString("tipo_membresia"),
                rs.getDate("fecha_inicio").toLocalDate(),
                rs.getDate("fecha_vencimiento").toLocalDate(),
                rs.getBigDecimal("costo"),
                rs.getString("estado")
        );
    }
}