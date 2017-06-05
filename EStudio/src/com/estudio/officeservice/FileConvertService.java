package com.estudio.officeservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.io.FilenameUtils;

public final class FileConvertService {
    private ArrayBlockingQueue<String> taskFileList = new ArrayBlockingQueue<String>(4096);
    private List<String> supportFileExt = new ArrayList<String>();
    {
        supportFileExt.add("doc");
        supportFileExt.add("docx");
        supportFileExt.add("xls");
        supportFileExt.add("xlsx");
        supportFileExt.add("ppt");
        supportFileExt.add("pptx");
    }

    /**
     * 是否支持文件转换
     * 
     * @param fileName
     * @return
     */
    private boolean isSupportFileType(String fileName) {
        return supportFileExt.indexOf(FilenameUtils.getExtension(fileName).toLowerCase()) != -1;
    }

    /**
     * 转换文件
     * 
     * @param fileName
     */
    private void convertFile(String fileName) {
        String fileExt = FilenameUtils.getExtension(fileName).toLowerCase();
        String basePath = FilenameUtils.getFullPath(fileName);
        String baseName = FilenameUtils.getBaseName(fileName);
        try {
            if ("doc".equals(fileExt) || "docx".equals(fileExt)) {
                WordService.getInstance(fileName).saveToSWF(basePath + baseName + ".swf").dispose();
            } else if ("xls".equals(fileExt) || "xlsx".equals(fileExt)) {
                ExcelService.getInstance(fileName).saveToHTML(basePath + baseName + ".html").dispose();
            } else if ("ppt".equals(fileExt) || "pptx".equals(fileExt)) {

            }
        } catch (Exception e) {

        }

    }

    /**
     * 添加任务
     * 
     * @param filename
     */
    public void addTask(String filename) {
        if (isSupportFileType(filename))
            taskFileList.add(filename);
    }

    public void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        String fileName = taskFileList.take();
                        convertFile(fileName);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        LicenseService.loadOfficeLicense();
        PowerPointService.getInstance("E:\\1.ppt").saveToPDF("E:\\1.pdf").dispose();
    }

    public static final FileConvertService instance = new FileConvertService();

    private FileConvertService() {
    }
}
