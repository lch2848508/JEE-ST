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
     * ����ΪPDF�ļ�
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
     * ���浽�ļ�
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
     * ��ȡʵ��
     * 
     * @return
     * @throws Exception
     */
    public static PowerPointService getInstance() throws Exception {
        return new PowerPointService().newDocument();
    }

    /**
     * �½��ļ�
     * 
     * @return
     * @throws Exception
     */
    private PowerPointService newDocument() throws Exception {
        ppt = new Presentation();
        return this;
    }

    /**
     * ���ļ�
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public static PowerPointService getInstance(String fileName) throws Exception {
        return new PowerPointService().openDocument(fileName);
    }

    /**
     * ���ļ�
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
