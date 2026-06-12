package com.gimnasio.dao;

import com.gimnasio.config.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaDAO {
    public void registrar(int idSocio, String resultado) throws SQLException {
        String sql = "INSERT INTO asistencia(id_socio,fecha,hora_ingreso,resultado_validacion) VALUES(?,CURRENT_DATE,CURRENT_TIME,?)";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            ps.setString(2, resultado);
            ps.executeUpdate();
        }
    }

    public List<Object[]> listar() throws SQLException {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT a.id_asistencia, s.dni, CONCAT(s.nombres,' ',s.apellidos) socio, a.fecha, a.hora_ingreso, a.resultado_validacion " +
                     "FROM asistencia a JOIN socio s ON s.id_socio=a.id_socio ORDER BY a.id_asistencia DESC";
        try (Connection cn = Db.getConnection(); PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getTime(5), rs.getString(6)});
        }
        return lista;
    }
}
