package com.gimnasio.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportePdfUtil {

    private ReportePdfUtil() {
    }

    public static File generarReportePagoMembresia(Object[] data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("No se encontraron datos para generar el reporte.");
        }

        int idPago = Integer.parseInt(data[0].toString());
        String dni = valor(data[1]);
        String socio = valor(data[2]);
        String tipoMembresia = valor(data[3]);
        String fechaInicio = valor(data[4]);
        String fechaVencimiento = valor(data[5]);
        String costo = valor(data[6]);
        String estadoMembresia = valor(data[7]);
        String fechaPago = valor(data[8]);
        String monto = valor(data[9]);
        String metodoPago = valor(data[10]);
        String estadoPago = valor(data[11]);

        Path carpeta = Path.of(System.getProperty("user.home"), "Documents", "Reportes_Gimnasio");
        Files.createDirectories(carpeta);

        String fechaArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo = "Reporte_Pago_Membresia_" + idPago + "_" + fechaArchivo + ".pdf";

        File archivo = carpeta.resolve(nombreArchivo).toFile();

        Document document = new Document(PageSize.A4, 40, 40, 40, 40);
        PdfWriter.getInstance(document, new FileOutputStream(archivo));

        document.open();

        Font tituloFont = new Font(Font.HELVETICA, 18, Font.BOLD, new Color(30, 30, 30));
        Font subtituloFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(220, 38, 38));
        Font textoFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        Font labelFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.BLACK);
        Font blancoFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

        Paragraph titulo = new Paragraph("POWER GYM", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("REPORTE DE PAGO DE MEMBRESÍA", subtituloFont);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(18);
        document.add(subtitulo);

        Paragraph info = new Paragraph(
                "Este reporte muestra el detalle del pago realizado por un socio, incluyendo los datos de la membresía asociada.",
                textoFont);
        info.setAlignment(Element.ALIGN_JUSTIFIED);
        info.setSpacingAfter(15);
        document.add(info);

        PdfPTable tablaDatos = new PdfPTable(2);
        tablaDatos.setWidthPercentage(100);
        tablaDatos.setWidths(new float[] { 35, 65 });
        tablaDatos.setSpacingAfter(15);

        agregarTituloSeccion(tablaDatos, "DATOS DEL SOCIO", blancoFont);
        agregarFila(tablaDatos, "DNI", dni, labelFont, textoFont);
        agregarFila(tablaDatos, "Socio", socio, labelFont, textoFont);

        agregarTituloSeccion(tablaDatos, "DATOS DE LA MEMBRESÍA", blancoFont);
        agregarFila(tablaDatos, "Tipo de membresía", tipoMembresia, labelFont, textoFont);
        agregarFila(tablaDatos, "Fecha de inicio", fechaInicio, labelFont, textoFont);
        agregarFila(tablaDatos, "Fecha de vencimiento", fechaVencimiento, labelFont, textoFont);
        agregarFila(tablaDatos, "Costo", "S/ " + costo, labelFont, textoFont);
        agregarFila(tablaDatos, "Estado de membresía", estadoMembresia, labelFont, textoFont);

        agregarTituloSeccion(tablaDatos, "DATOS DEL PAGO", blancoFont);
        agregarFila(tablaDatos, "ID de pago", String.valueOf(idPago), labelFont, textoFont);
        agregarFila(tablaDatos, "Fecha de pago", fechaPago, labelFont, textoFont);
        agregarFila(tablaDatos, "Monto pagado", "S/ " + monto, labelFont, textoFont);
        agregarFila(tablaDatos, "Método de pago", metodoPago, labelFont, textoFont);
        agregarFila(tablaDatos, "Estado del pago", estadoPago, labelFont, textoFont);

        document.add(tablaDatos);

        Paragraph pie = new Paragraph(
                "Reporte generado automáticamente por el Sistema de Gestión Interno para Gimnasio.",
                new Font(Font.HELVETICA, 9, Font.ITALIC, new Color(90, 90, 90)));
        pie.setAlignment(Element.ALIGN_CENTER);
        pie.setSpacingBefore(20);
        document.add(pie);

        document.close();

        return archivo;
    }

    private static void agregarTituloSeccion(PdfPTable table, String texto, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setColspan(2);
        cell.setBackgroundColor(new Color(25, 25, 25));
        cell.setPadding(8);
        table.addCell(cell);
    }

    private static void agregarFila(PdfPTable table, String etiqueta, String valor, Font labelFont, Font textoFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(etiqueta, labelFont));
        c1.setPadding(7);
        c1.setBackgroundColor(new Color(245, 245, 245));

        PdfPCell c2 = new PdfPCell(new Phrase(valor, textoFont));
        c2.setPadding(7);

        table.addCell(c1);
        table.addCell(c2);
    }

    private static String valor(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}