package main.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Reporting {
    private Document pdf;
    private String filename;
    private boolean error = false;
    private PdfWriter writer;

    public Document getPdf() { return pdf; }
    public PdfWriter getWriter() { return writer; }
    public String getFilename() { return filename; }
    public boolean isError() { return error; }
    protected void setError() { error = true; }

    public void show() throws IOException {
        Desktop.getDesktop().open(new File(getFilename()));
    }

    public abstract void create();

    public Reporting(String file) {
        try{
            filename = file;
            pdf = new Document();
            writer = PdfWriter.getInstance(pdf,new FileOutputStream(getFilename()));
            pdf.open();
        } catch (FileNotFoundException | DocumentException ex) {
            setError();
        }
    }

    public Font getTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD,22);
    }
    public Font getSubtitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD,16);
    }
}
