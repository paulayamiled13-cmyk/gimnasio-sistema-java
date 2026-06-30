package com.gimnasio.dao;

import com.gimnasio.config.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<Object[]> sociosActivos() throws SQLException {
        return query(
                "SELECT dni, CONCAT(nombres,' ',apellidos) socio, telefono, correo, estado FROM socio WHERE estado='Activo'");
    }

    public List<Object[]> membresiasVencidas() throws SQLException {
        return query(
                "SELECT s.dni, CONCAT(s.nombres,' ',s.apellidos) socio, m.tipo_membresia, m.fecha_vencimiento, m.estado "
                        +
                        "FROM membresia m JOIN socio s ON s.id_socio=m.id_socio WHERE m.estado='Vencida' OR m.fecha_vencimiento < CURRENT_DATE");
    }

    public List<Object[]> inventario() throws SQLException {
        return query("SELECT nombre_producto, descripcion, precio, stock, estado FROM producto ORDER BY stock ASC");
    }

    public List<Object[]> ingresos() throws SQLException {
        return query("SELECT DATE_FORMAT(fecha_pago, '%Y-%m') periodo, SUM(monto) total " +
                "FROM pago WHERE estado_pago='Registrado' GROUP BY DATE_FORMAT(fecha_pago, '%Y-%m') ORDER BY periodo DESC");
    }

    private List<Object[]> query(String sql) throws SQLException {
        List<Object[]> data = new ArrayList<>();

        try (Connection cn = Db.getConnection();
                PreparedStatement ps = cn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            int cols = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                Object[] row = new Object[cols];

                for (int i = 0; i < cols; i++) {
                    row[i] = rs.getObject(i + 1);
                }

                data.add(row);
            }
        }

        return data;
    }
}