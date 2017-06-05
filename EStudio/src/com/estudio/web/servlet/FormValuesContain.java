package com.estudio.web.servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.estudio.utils.Convert;
import com.estudio.utils.SecurityUtils;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.MultipartProgress;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

/**
 * 定义表单值集合
 * 
 * @author Administrator
 */
public class FormValuesContain {
    /**
     * 定义同文件附件相关的信息
     * 
     * @author Administrator
     */
    public class FormFile {
        String contentType = "";
        String fileName = "";
        String saveFileName = "";
        String name = "";
        long size = 0;

        /**
         * @param fileName
         * @param size
         * @param contentType
         */
        private FormFile(final String name, final String fileName, final long size, final String contentType, final String saveFileName) {
            super();
            this.name = name;
            this.saveFileName = saveFileName;
            this.fileName = fileName;
            this.size = size;
            this.contentType = contentType;
        }

        /**
         * @return the contentType
         */
        public String getContentType() {
            return contentType;
        }

        /**
         * @return the fileName
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the saveFileName
         */
        public String getSaveFileName() {
            return saveFileName;
        }

        /**
         * @return the size
         */
        public long getSize() {
            return size;
        }

        /**
         * @param contentType
         *            the contentType to set
         */
        public void setContentType(final String contentType) {
            this.contentType = contentType;
        }

        /**
         * @param fileName
         *            the fileName to set
         */
        public void setFileName(final String fileName) {
            this.fileName = fileName;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * @param saveFileName
         *            the saveFileName to set
         */
        public void setSaveFileName(final String saveFileName) {
            this.saveFileName = saveFileName;
        }

        /**
         * @param size
         *            the size to set
         */
        public void setSize(final long size) {
            this.size = size;
        }

    }

    /**
     * 表单 Input 项
     * 
     * @author Administrator
     */
    public class FormInput {
        String name;
        String value;

        /**
         * @param name
         * @param value
         */
        private FormInput(final String name, final String value) {
            super();
            this.name = name;
            this.value = value;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param name
         *            the name to set
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * @param value
         *            the value to set
         */
        public void setValue(final String value) {
            this.value = value;
        }
    }

    private static long currentID = 0;

    /**
     * @return the currentID
     */
    private static synchronized long getCurrentID() {
        return FormValuesContain.currentID++;
    }

    /**
     * 得到实例
     * 
     * @param request
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static FormValuesContain getInstance(final HttpServletRequest request, final String filePath, final int CacheSize, final String keyString) throws UnsupportedEncodingException, IOException {
        FormValuesContain result = null;
        try {
            result = new FormValuesContain(request, filePath, CacheSize, keyString);
        } catch (final Exception e) {

        }
        return result;
    }

    /**
     * 得到实例
     * 
     * @param request
     * @param filePath
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static FormValuesContain getInstance(final HttpServletRequest request, final String filePath, final String keyString) throws UnsupportedEncodingException, IOException {
        FormValuesContain result = null;
        try {
            result = new FormValuesContain(request, filePath, 512 * 1024 * 1024, keyString);
        } catch (final Exception e) {

        }
        return result;
    }

    /**
     * 得到实例
     * 
     * @param request
     * @param filePath
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static FormValuesContain getInstance(final HttpServletRequest request, final String filePath) throws UnsupportedEncodingException, IOException {
        final String keyString = UUID.randomUUID().toString();
        FormValuesContain result = null;
        try {
            result = new FormValuesContain(request, filePath, 256 * 1024 * 1024, keyString);
        } catch (final Exception e) {

            //
        }
        return result;
    }

    /**
     * 获取进度信息
     * 
     * @param id
     * @return
     */
    public static MultipartProgress getProgress(final HttpServletRequest request, final String progressKey) {
        if ((progressKey != null) && !progressKey.equals(""))
            return (MultipartProgress) request.getSession().getAttribute("MultipartProgress_" + progressKey);
        else return null;

    }

    private final ArrayList<FormFile> formFiles = new ArrayList<FormFile>();

    private final ArrayList<FormInput> formInputs = new ArrayList<FormInput>();

    private MultipartParser mp = null;

    private final long UniqueID = FormValuesContain.getCurrentID();

    /**
     * 构造函数
     * 
     * @param request
     * @param cacheSize
     * @param filePath
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private FormValuesContain(final HttpServletRequest request, final String filePath, final int cacheSize, final String progressKey) throws UnsupportedEncodingException, IOException {
        parserRequest(request, filePath, cacheSize, progressKey);
    }

    /**
     * 得到文件的迭代器
     * 
     * @return
     */
    public Iterator<FormFile> getFileIterator() {
        return formFiles.iterator();
    }

    /**
     * 得到表单中的FormFile对象
     * 
     * @param name
     * @return
     */
    public FormFile getFormFile(final String name) {
        for (int i = 0; i < formFiles.size(); i++) {
            final FormFile f = formFiles.get(i);
            if (f.getName().equals(name))
                return f;
        }
        return null;
    }

    /**
     * 得到二进制字节流
     * 
     * @param paramName
     * @return
     * @throws IOException
     */
    public byte[] getParamBytes(final String paramName) {
        byte[] result = null;
        for (int i = 0; i < formFiles.size(); i++) {
            final FormFile formFile = formFiles.get(i);
            if (formFile.getName().equals(paramName)) {
                try {
                    result = Convert.file2Bytes(formFile.getSaveFileName());
                } catch (final IOException e) {
                }
                break;
            }
        }

        if (result == null)
            result = Convert.str2Bytes(getParamValue(paramName));

        return result;
    }

    /**
     * 得到表单Input的迭代器
     * 
     * @return
     */
    public Iterator<FormInput> getParamIterator() {
        return formInputs.iterator();
    }

    /**
     * 根据参数名称取得参数值
     * 
     * @param paramName
     * @return
     */
    public String getParamValue(final String paramName) {
        for (int i = 0; i < formInputs.size(); i++) {
            final FormInput input = formInputs.get(i);
            if (input.name.equals(paramName))
                return input.value;
        }
        return null;
    }

    /**
     * @return the uniqueID
     */
    public long getUniqueID() {
        return UniqueID;
    }

    /**
     * 分析Request对象
     * 
     * @param request
     * @param cacheSize
     * @param filePath
     * @param progressKey
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private void parserRequest(final HttpServletRequest request, final String filePath, final int maxSize, final String progressKey) throws IOException, UnsupportedEncodingException {
        mp = new MultipartParser(request, maxSize);
        mp.setEncoding("utf-8");
        Map<String, String> fileName2NormalFile = new HashMap<String, String>();
        Part part;
        while ((part = mp.readNextPart()) != null) {
            final String name = part.getName();
            if (part.isParam()) {
                final ParamPart paramPart = (ParamPart) part;
                final String value = paramPart.getStringValue();
                registerParam(name, value);
                fileName2NormalFile.put(name, value);
            } else if (part.isFile()) {
                // it's a file part
                final FilePart filePart = (FilePart) part;
                final GUIDFileRenamePolicy policy = new GUIDFileRenamePolicy();
                filePart.setRenamePolicy(policy);
                String fileName = filePart.getFileName();
                if (fileName != null) {
                    mp.getProgress().setCurrentUploadFileName(fileName);
                    final long size = filePart.writeTo(new File(filePath));
                    mp.getProgress().addUploadedFileName(fileName);
                    registerFile(name, fileName, size, filePart.getContentType(), policy.getSavedFile().getAbsolutePath());
                }
            }
        }

    }

    /**
     * 登记文件
     * 
     * @param name
     * @param size
     * @param contentType
     */
    private void registerFile(final String name, final String fileName, final long size, final String contentType, final String saveFieldName) {
        formFiles.add(new FormFile(name, fileName, size, contentType, saveFieldName));
    }

    /**
     * 登记参数
     * 
     * @param name
     * @param value
     */
    private void registerParam(final String name, final String value) {
        formInputs.add(new FormInput(name, value));
    }

    public void addParam(final String name, final String value) {
        formInputs.add(new FormInput(name, value));

    }

    public static String decodeBase64(String str) {
        byte[] bs = SecurityUtils.decodeBase64(str);
        return Convert.bytes2Str(bs);
    }

}
