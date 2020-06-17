package main.Service;

import com.itextpdf.awt.DefaultFontMapper;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;

public class BankAccountReport extends Reporting {

    public BankAccountReport(String file) {
        super(file);
    }

    public void create() {
        try {
            if (!isError()) {
                getPdf().addTitle("Bank Account Report");
                getPdf().addAuthor("Arthur Zaczek");
                getPdf().addCreationDate();

                getPdf().add(new Paragraph("Bank Account Report", getTitleFont()));

                // Add current Amount
                getPdf().add(new Paragraph("Current Account: 15.548,79"));

                addAccountTable();

                addAccountChart();

                getPdf().close();
            }
        } catch (DocumentException ex) {
            setError();
        }
    }

    private void addAccountTable() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(10.0f);
        PdfPCell cell =
                new PdfPCell(new Paragraph("History"));
        cell.setColspan(2);
        table.addCell(cell);

        table.addCell("01.01.2008");
        table.addCell("1000.0");

        table.addCell("01.02.2008");
        table.addCell("1050.0");

        table.addCell("01.03.2008");
        table.addCell("1300.0");

        table.addCell("01.04.2008");
        table.addCell("1350.0");

        getPdf().add(table);

    }

    private JFreeChart createAccountChart() {
        TimeSeries series = new TimeSeries("Bank Account", "Domain",Month.class.toString());
        series.add(new Month(1, 2008), 1000.0);
        series.add(new Month(2, 2008), 1050);
        series.add(new Month(3, 2008), 1300);
        series.add(new Month(4, 2008), 1350);

        TimeSeries series2 = new TimeSeries("Bank Account 2", Month.class.toString(),"Range");
        series2.add(new Month(1, 2008), 1200.0);
        series2.add(new Month(2, 2008), 1250);
        series2.add(new Month(3, 2008), 1500);
        series2.add(new Month(4, 2008), 1550);

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        dataset.addSeries(series2);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Bank Account Chart", "Time", "Amount", dataset,
                true, true, false);

        DateAxis axis = (DateAxis)chart.getXYPlot().getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MMM-yyyy"));

        return chart;
    }

    private void addAccountChart() {

        // Chart erzeugen
        JFreeChart chart = createAccountChart();

        float width = 400; float height = 300;

        // Graphics 2d erzeugen
        PdfContentByte content = getWriter().getDirectContent();
        PdfTemplate template = content.createTemplate(width, height);
        Graphics2D g2d = template.createGraphics(width, height, new DefaultFontMapper());

        // Zeichnen
        Rectangle2D r2d = new Rectangle2D.Double(0, 0, width, height);
        chart.draw(g2d, r2d);
        g2d.dispose();

        // zum PDF Hinzufügen
        content.addTemplate(template, 100, 300);

    }
}
