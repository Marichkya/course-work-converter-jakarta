package com.example.web;

import com.example.service.Converter;
import com.example.service.DownloadFile;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class UploadFileController {

    @Autowired
    private DownloadFile downloadFile;

    @Autowired
    private Converter converter;

    private static String uploadDirectory = System.getProperty("user.dir") + "/uploads";

    @RequestMapping(value = "/jpgtopng/index", method = RequestMethod.GET)
    public String jpgToPNG() {
        return "JPGtoPNG";
    }

    @RequestMapping(value = "/pdffromppt/index", method = RequestMethod.GET)
    public String pdffromppt() {
        return "PPTtoPDF";
    }



    @RequestMapping(value = "/jpgtopng/upload", method = RequestMethod.POST)
    @ResponseBody
    public void jpgtoPNG(Model model, @RequestParam("files") MultipartFile[] files, HttpServletResponse response) throws IOException {
        String fileNames = this.upload(model, files, response);
        String extension = "." + getExtensionByFileName(fileNames);
        converter.jpgToPNG(fileNames, extension);
        downloadFile.Download(fileNames.replaceFirst(extension,"") + ".png", uploadDirectory, response);
    }



    @RequestMapping(value = "/pdffromppt/upload", method = RequestMethod.POST)
    @ResponseBody
    public void pptFromPDF(Model model, @RequestParam("files") MultipartFile[] files, HttpServletResponse response) throws Exception {
        String fileNames = this.upload(model, files, response);
        String extension = "." + getExtensionByFileName(fileNames);
        converter.convertPPToPDF(fileNames, extension);
        downloadFile.Download(fileNames.replaceFirst(extension, "") + ".pdf", uploadDirectory, response);
        deleteFile(fileNames);
        deleteFile(fileNames.replaceFirst(extension, "") + ".pdf");
    }

    @RequestMapping(value = "/docfrompdf/index", method = RequestMethod.GET)
    public String showdocfrompdf() {

        return "DocFromPDF";
    }

    @RequestMapping(value = "/docfrompdf/upload", method = RequestMethod.POST)
    @ResponseBody
    public void docfrompdf(Model model, @RequestParam("files") MultipartFile[] files, HttpServletResponse response) {
        String fileNames = this.upload(model, files, response);
        String extension = "." + getExtensionByFileName(fileNames);
        converter.generateDocFromPdf(fileNames, extension);
        downloadFile.Download(fileNames + ".docx", uploadDirectory, response);
    }

    @RequestMapping(value = "/pdffromtxt/index", method = RequestMethod.GET)
    public String showpdffromtxt() {
        return "PDFFromTxt";
    }

    @RequestMapping(value = "/pdffromimage/index", method = RequestMethod.GET)
    public String showpdffromimage() {

        return "PDFFromImage";
    }

    @RequestMapping(value = "/imagefrompdf/index", method = RequestMethod.GET)
    public String showimagefrompdf() {

        return "ImageFromPDF";
    }

    @RequestMapping(value = "/pdffromtxt/upload", method = RequestMethod.POST)
    @ResponseBody
    public void pdffromtxtConverte(Model model, @RequestParam("files") MultipartFile[] files, HttpServletResponse response) {
        String fileNames = this.upload(model, files, response);
        converter.generatePDFFromTxt(fileNames);
        downloadFile.Download(fileNames + ".pdf", uploadDirectory, response);
    }

    @RequestMapping(value = "/pdffromimage/upload", method = RequestMethod.POST)
    @ResponseBody
    public void imagefrompdfConverte(Model model, @RequestParam("files") MultipartFile[] files, HttpServletResponse response) throws IOException, DocumentException {
        String fileNames = this.upload(model, files, response);
        String extension = "." + String.valueOf(getExtensionByFileName(fileNames));
        converter.generatePDFFromImage(fileNames, extension);
        downloadFile.Download(fileNames.replaceFirst(extension, "") + ".pdf", uploadDirectory, response);
    }

    public String getExtensionByFileName(String filename) {
        return getExtension(filename);
    }

    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        } else {
            int index = indexOfExtension(filename);
            return index == -1 ? "" : filename.substring(index + 1);
        }
    }

    public static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int extensionPos = filename.lastIndexOf(46);
            int lastSeparator = indexOfLastSeparator(filename);
            return lastSeparator > extensionPos ? -1 : extensionPos;
        }
    }
    public static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        } else {
            int lastUnixPos = filename.lastIndexOf(47);
            int lastWindowsPos = filename.lastIndexOf(92);
            return Math.max(lastUnixPos, lastWindowsPos);
        }
    }


    @RequestMapping(value = "/imagefrompdf/upload", method = RequestMethod.POST)
    @ResponseBody
    public void pdffromimageConverte(Model model, @RequestParam("files") MultipartFile[] files, HttpServletResponse response) {
        String fileNames = this.upload(model, files, response);
        String extension = "." + String.valueOf(getExtensionByFileName(fileNames));
        converter.generateImageFromPDF(fileNames, extension);
        downloadFile.Download(fileNames.replaceFirst(extension, "") + ".jpg", uploadDirectory, response);
    }


    public String upload(Model model, @RequestParam("files") MultipartFile[] files, HttpServletResponse response) {
        StringBuilder fileNames = new StringBuilder();
        for (MultipartFile file : files) {
            Path fileNameAndPath = Paths.get(uploadDirectory, file.getOriginalFilename());
            fileNames.append(file.getOriginalFilename());
            try {
                Files.write(fileNameAndPath, file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.addAttribute("msg", "Successfully uploaded files " + fileNames.toString());
        return fileNames.toString();
    }


    public void deleteFile(String filename) {
        try {
            File file = new File(uploadDirectory + "/" + filename);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
