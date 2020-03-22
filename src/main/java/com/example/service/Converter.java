package com.example.service;

import com.example.web.UploadFileController;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.spire.presentation.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

@Service
public class Converter {

    private static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    public HttpServletResponse response;

    @Autowired
    private UploadFileController uploadFileController;

    @Autowired
    private DownloadFile downloadFile;


    Model model;

    @ResponseBody
    public void generatePDFFromImage(String filename, String extension) throws IOException, DocumentException {
        File root = new File(uploadDirectory + "/");
        String outputFile = filename.replaceFirst(extension, ".pdf");
        ArrayList<String> files = new ArrayList<String>();
        files.add(filename);
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(new File(root, outputFile)));
        document.open();
        for (String f : files) {
            document.newPage();
            Image image = Image.getInstance(new File(root, f).getAbsolutePath());
            image.setAbsolutePosition(0, 0);
            image.setBorderWidth(0);
            image.scaleAbsoluteHeight(PageSize.A4.getHeight());
            image.scaleAbsoluteWidth(PageSize.A4.getWidth());
            document.add(image);
        }
        document.close();
    }

    @ResponseBody
    public void convertPPToPDF(String filename, String extension) throws Exception {
        Presentation presentationToConvert = new Presentation();
        presentationToConvert.loadFromFile(uploadDirectory + "/" + filename);
                presentationToConvert.saveToFile(uploadDirectory + "/" + filename.replaceFirst(extension, "") + ".pdf", FileFormat.PDF);
                presentationToConvert.dispose();
    }


    @ResponseBody
    public void generateDocFromPdf(String filename, String extension) {
        try {
            XWPFDocument doc = new XWPFDocument();
            String pdf = uploadDirectory + "/" + filename;
            PdfReader reader = new PdfReader(pdf);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                TextExtractionStrategy strategy =
                        parser.processContent(i, new SimpleTextExtractionStrategy());
                String text = strategy.getResultantText();
                XWPFParagraph p = doc.createParagraph();
                XWPFRun run = p.createRun();
                run.setText(text);
                run.addBreak(BreakType.PAGE);
            }
            FileOutputStream out = new FileOutputStream(uploadDirectory + "/" + filename + ".docx");
            doc.write(out);
            uploadFileController.deleteFile(filename);
            downloadFile.Download(filename + ".docx", uploadDirectory, response);

        } catch (Exception e) {
        }


    }

    @ResponseBody
    public void jpgToPNG(String filename, String extension) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(uploadDirectory + "/" + filename));
// write the bufferedImage back to outputFile
        ImageIO.write(bufferedImage, "png", new File(uploadDirectory + "/" + filename.replaceFirst(extension,"") + ".png"));
// this writes the bufferedImage into a byte array called resultingBytes
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOut);
        byte[] resultingBytes = byteArrayOut.toByteArray();
    }

    @ResponseBody
    public void generatePDFFromTxt(String filename) {
        try {
            Document pdfDoc = new Document(PageSize.A4);
            PdfWriter.getInstance(pdfDoc, new FileOutputStream(uploadDirectory + "/" + filename + ".pdf"))
                    .setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            pdfDoc.open();

            Font myfont = new Font();
            myfont.setStyle(Font.NORMAL);
            myfont.setSize(11);
            pdfDoc.add(new Paragraph("\n"));
            BufferedReader br = new BufferedReader(new FileReader(uploadDirectory + "/" + filename));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                Paragraph para = new Paragraph(strLine + "\n", myfont);
                para.setAlignment(Element.ALIGN_JUSTIFIED);
                pdfDoc.add(para);
            }
            pdfDoc.close();
            br.close();
            uploadFileController.deleteFile(filename);
            downloadFile.Download(filename + ".pdf", uploadDirectory, response);

        } catch (Exception e) {

        }


    }

    public void generateImageFromPDF(String filename, String extension) {
        try (final PDDocument document = PDDocument.load(new File(uploadDirectory + "/" + filename))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String fileName = uploadDirectory + "image-" + page + "." + "jpg";
                ImageIOUtil.writeImage(bim, fileName, 300);
            }
            document.close();
        } catch (IOException e) {
            System.err.println("Exception while trying to create pdf document - " + e);
        }

    }


}
