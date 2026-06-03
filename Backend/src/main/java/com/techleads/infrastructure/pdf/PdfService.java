package com.techleads.infrastructure.pdf;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.techleads.application.dto.response.InventarioResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    public byte[] generarInventarioPdf(List<InventarioResponse> inventario) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
             Document document = new Document(pdf, PageSize.A4)) {

            document.add(new Paragraph("REPORTE DE INVENTARIO")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(18));

            document.add(new Paragraph("Generado: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10));

            document.add(new Paragraph(" "));

            float[] columnWidths = {2, 3, 2, 3, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            addHeaderCell(table, "Empresa NIT");
            addHeaderCell(table, "Empresa");
            addHeaderCell(table, "Código Producto");
            addHeaderCell(table, "Producto");
            addHeaderCell(table, "Cantidad");

            for (InventarioResponse item : inventario) {
                table.addCell(new Cell().add(new Paragraph(item.getEmpresaNit())));
                table.addCell(new Cell().add(new Paragraph(item.getEmpresaNombre() != null ? item.getEmpresaNombre() : "")));
                table.addCell(new Cell().add(new Paragraph(item.getProductoCodigo())));
                table.addCell(new Cell().add(new Paragraph(item.getProductoNombre() != null ? item.getProductoNombre() : "")));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getCantidad()))));
            }

            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total registros: " + inventario.size())
                    .setBold());
        }

        return baos.toByteArray();
    }

    private void addHeaderCell(Table table, String text) {
        Cell cell = new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
    }
}
