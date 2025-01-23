package com.touchmind.utils;

import com.touchmind.core.HotFolderConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    final String baseDir = HotFolderConstants.DOM_TREES + File.separator;
    Logger ziputilsLogger = LoggerFactory.getLogger(ZipUtils.class);

    public void zipFiles(List<String> files, OutputStream outputStream, boolean deleteAfterZip) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            List<File> tempFiles = new ArrayList<>();
            files.forEach(filename -> {
                File file = new File(baseDir + filename);
                if (deleteAfterZip) {
                    tempFiles.add(file);
                }
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zos.write(buffer, 0, length);
                    }
                    zos.closeEntry();
                } catch (IOException e) {
                    ziputilsLogger.error(e.getMessage());
                }
            });
            if (deleteAfterZip) {
                deleteFilesAfterZip(tempFiles);
            }
        }
    }

    private void deleteFilesAfterZip(List<File> tempFiles) {
        tempFiles.forEach(File::delete);
    }
}
