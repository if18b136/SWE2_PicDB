package main.Service;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public class TagReport extends Reporting {
    final Logger repLogger = LogManager.getLogger("Tag Report");
    public TagReport(String file) {
        super(file);
    }

    @Override
    public void create() {
        try {
            if(!isError()) {
                getPdf().addTitle("Picture Report");
                getPdf().addAuthor("Maximilian Rotter");
                getPdf().addCreationDate();

                getPdf().add(new Paragraph("Picture Report", getTitleFont()));

                addTagTable();
                addTagChart();

                getPdf().close();
            }
        } catch (DocumentException de) {
            setError();
            repLogger.error(de.getMessage());
        }
    }

    private void addTagTable() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(10.0f);
        PdfPCell cell =
                new PdfPCell(new Paragraph("Tags"));
        cell.setColspan(2);
        table.addCell(cell);
        BusinessLayer bl = BusinessLayer.getInstance();
        HashMap<String,Integer> tagMap = bl.getTagMap();
        for(Map.Entry<String,Integer> tag : tagMap.entrySet()) {
            table.addCell(tag.getKey());
            table.addCell(String.valueOf(tag.getValue()));
        }
        getPdf().add(table);
    }

    private void addTagChart() {
        // Chart erzeugen
        JFreeChart chart = createTagChart();

        float width = 400; float height = 300;

        // Graphics 2d erzeugen
        PdfContentByte content = getWriter().getDirectContent();
        PdfTemplate template = content.createTemplate(width,height);
        //PdfTemplate template = content.createTemplate(width, height);
        Graphics2D g2d = template.createGraphics(width, height, new DefaultFontMapper());

        // Zeichnen
        Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
        chart.draw(g2d, r2d);
        g2d.dispose();

        // zum PDF Hinzufügen
        content.addTemplate(template, 100, 300);

    }

    private JFreeChart createTagChart() {
        JFreeChart barChart = ChartFactory.createBarChart(
                "Tag Distribution",
                "Tag",
                "Amount",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false
        );
        return barChart;
    }

    private CategoryDataset createDataset() {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        BusinessLayer bl = BusinessLayer.getInstance();
        HashMap<String,Integer> tagMap = bl.getTagMap();
        for(Map.Entry<String,Integer> tag : tagMap.entrySet()) {
            dataset.addValue(tag.getValue(),  tag.getKey(),"Amount");
        }
        return dataset;
    }
}
