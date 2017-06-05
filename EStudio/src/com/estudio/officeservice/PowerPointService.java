package com.estudio.officeservice;

import com.aspose.slides.Presentation;
import com.aspose.slides.SaveFormat;

public class PowerPointService {
    private Presentation ppt = null;

    public void dispose() {
        if (ppt != null)
            ppt = null;
    }

    /**
     * 保存为PDF文件
     * 
     * @param fileName
     * @throws Exception
     */
    public PowerPointService saveToPDF(String fileName) throws Exception {
        ppt.save(fileName, SaveFormat.PdfNotes);
        return this;
    }

    public PowerPointService saveToHTML(String fileName) throws Exception {
        ppt.save(fileName, SaveFormat.Html);
        return this;
    }

    /**
     * 保存到文件
     * 
     * @param fileName
     * @throws Exception
     */
    public void save(String fileName) throws Exception {
        ppt.save(fileName, SaveFormat.Ppt);
    }

    private PowerPointService() {
    }

    /**
     * 获取实例
     * 
     * @return
     * @throws Exception
     */
    public static PowerPointService getInstance() throws Exception {
        return new PowerPointService().newDocument();
    }

    /**
     * 新建文件
     * 
     * @return
     * @throws Exception
     */
    private PowerPointService newDocument() throws Exception {
        ppt = new Presentation();
        return this;
    }

    /**
     * 打开文件
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public static PowerPointService getInstance(String fileName) throws Exception {
        return new PowerPointService().openDocument(fileName);
    }

    /**
     * 打开文件
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    private PowerPointService openDocument(String fileName) throws Exception {
        ppt = new Presentation(fileName);
        return this;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
