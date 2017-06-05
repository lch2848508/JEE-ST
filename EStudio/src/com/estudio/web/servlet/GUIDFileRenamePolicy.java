package com.estudio.web.servlet;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.oreilly.servlet.multipart.FileRenamePolicy;

public class GUIDFileRenamePolicy implements FileRenamePolicy {

    /**
     * 
     * @param file
     * @return
     */
    private static synchronized File getFileName(final File file) {
        if (!file.exists())
            return file;
        String fileName = file.getName();
        final String filePath = file.getParent();
        String fileExt = "";
        if (fileName.lastIndexOf(".") != -1) {
            final String tempStr = fileName;
            fileExt = tempStr.substring(tempStr.lastIndexOf("."));
        }
        fileName = StringUtils.replaceEach(UUID.randomUUID().toString(), new String[] { "{", "}", "-" }, new String[] { "", "", "" });
        return new File((new StringBuffer().append(filePath).append(File.separator).append(fileName).append(fileExt)).toString());
    }

    private File savedFile = null;

    /**
     * @return the oldFile
     */
    public File getSavedFile() {
        return savedFile;
    }

    @Override
    public File rename(final File arg0) {
        savedFile = getFileName(arg0);
        return savedFile;
    }
}
