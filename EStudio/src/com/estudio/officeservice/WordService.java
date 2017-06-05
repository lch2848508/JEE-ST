package com.estudio.officeservice;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;

public class WordService {
    private Document doc = null;

    public void dispose() {
        if (doc != null)
            doc = null;
    }

    /**
     * 保存为PDF文件
     * 
     * @param fileName
     * @throws Exception
     */
    public WordService saveToPDF(String fileName) throws Exception {
        doc.save(fileName, SaveFormat.PDF);
        return this;
    }

    public WordService saveToSWF(String fileName) throws Exception {
        doc.save(fileName, SaveFormat.SWF);
        return this;
    }

    /**
     * 保存到文件
     * 
     * @param fileName
     * @throws Exception
     */
    public void save(String fileName) throws Exception {
        doc.save(fileName);
    }

    private WordService() {
    }

    /**
     * 获取实例
     * 
     * @return
     * @throws Exception
     */
    public static WordService getInstance() throws Exception {
        return new WordService().newDocument();
    }

    /**
     * 新建文件
     * 
     * @return
     * @throws Exception
     */
    private WordService newDocument() throws Exception {
        doc = new Document();
        return this;
    }

    /**
     * 打开文件
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    public static WordService getInstance(String fileName) throws Exception {
        return new WordService().openDocument(fileName);
    }

    /**
     * 打开文件
     * 
     * @param fileName
     * @return
     * @throws Exception
     */
    private WordService openDocument(String fileName) throws Exception {
        doc = new Document(fileName);
        return this;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
