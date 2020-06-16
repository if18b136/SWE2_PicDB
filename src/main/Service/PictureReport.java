package main.Service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BadPdfFormatException;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import main.Models.EXIF;
import main.Models.Picture;
import main.PresentationModels.EXIF_PM;
import main.PresentationModels.Picture_PM;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PictureReport extends Reporting{
    final Logger repLogger = LogManager.getLogger("Picture Report");
    private Picture_PM picturePm;

    public PictureReport(String fileName, Picture_PM picturePm) {
        super(fileName);
        this.picturePm = picturePm;
    }

    @Override
    public void create() {
        try{
            if(!isError()) {
                getPdf().addTitle("Picture Report");
                getPdf().addAuthor("Maximilian Rotter");
                getPdf().addCreationDate();
                getPdf().add(new Paragraph("Picture Report", getTitleFont()));
                getPdf().add(new Paragraph(picturePm.getName(), getSubtitleFont()));
                addImage();
                addPhotographer();
                addExif();
                addIptc();
                getPdf().close();
            }
        } catch (DocumentException de) {
            setError();
        }
    }

    private void addImage() {
        try{
            BusinessLayer bl = BusinessLayer.getInstance();
            String path = bl.getPath() + picturePm.getName();
            Image image;
            image = Image.getInstance(path);
            image.scaleAbsolute(500.0f,300.0f);
            getPdf().add(image);
        } catch (IOException | DocumentException ex) {
            repLogger.error(ex.getMessage());
        }
    }

    private void addPhotographer() {
        try {
            PdfPTable table = new PdfPTable(2);
            table.setSpacingBefore(10.0f);
            PdfPCell cell = new PdfPCell(new Paragraph("Photographer:"));
            table.addCell(cell);
            table.addCell(picturePm.getPhotographer().getFullName());
            getPdf().add(table);
        } catch (DocumentException ex) {
            repLogger.error(ex.getMessage());
        }
    }

    private void addExif() {
        try {
            PdfPTable table = new PdfPTable(2);
            table.setSpacingBefore(10.0f);
            PdfPCell cell = new PdfPCell(new Paragraph("EXIF:"));
            cell.setColspan(2);
            table.addCell(cell);
            for(EXIF_PM exif : picturePm.getExifList()) {
                table.addCell(exif.getName());
                table.addCell(exif.getDescription());
            }
            getPdf().add(table);
        } catch (DocumentException ex) {
            repLogger.error(ex.getMessage());
        }
    }

    private void addIptc() {
        try {
            PdfPTable table = new PdfPTable(2);
            table.setSpacingBefore(10.0f);
            PdfPCell cell = new PdfPCell(new Paragraph("IPTC:"));
            cell.setColspan(2);
            table.addCell(cell);
            table.addCell("Copyright");
            table.addCell(picturePm.getIptc().getCopyright());
            table.addCell("Tags");
            table.addCell(picturePm.getIptc().getTagList());
            getPdf().add(table);
        } catch (DocumentException ex) {
            repLogger.error(ex.getMessage());
        }
    }
}
