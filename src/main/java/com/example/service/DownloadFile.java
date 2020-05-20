package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class DownloadFile {

    @ResponseBody
    public void Download(String fileName, String folderPath, HttpServletResponse response) {

        if (fileName.indexOf(".doc") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".docx") > -1) response.setContentType("application/msword");
        if (fileName.indexOf(".xls") > -1) response.setContentType("application/vnd.ms-excel");
        if (fileName.indexOf(".csv") > -1) response.setContentType("application/vnd.ms-excel");
        if (fileName.indexOf(".ppt") > -1) response.setContentType("application/ppt");
        if (fileName.indexOf(".pdf") > -1) response.setContentType("application/pdf");
        if (fileName.indexOf(".zip") > -1) response.setContentType("application/zip");
        if (fileName.indexOf(".html") > -1) response.setContentType("application/html");
        if (fileName.indexOf(".jpg") > -1) response.setContentType("application/jpg");
        if (fileName.indexOf(".png") > -1) response.setContentType("application/png");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        response.setHeader("Content-Transfer-Encoding", "binary");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
            FileInputStream fis = new FileInputStream(folderPath + "/" + fileName);
            int len;
            byte[] buf = new byte[1024];
            while ((len = fis.read(buf)) > 0) {
                bos.write(buf, 0, len);
            }
            bos.close();
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
