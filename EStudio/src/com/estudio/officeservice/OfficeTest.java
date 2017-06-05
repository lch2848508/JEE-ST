package com.estudio.officeservice;

public class OfficeTest {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        ExcelService.getInstance("E:\\1234.xls").selectSheet("≤‚ ‘").setRowColor(0, ExcelServiceConst.COLOR_RED, ExcelServiceConst.COLOR_GRAY).
        save("E:\\1234.xls").dispose();
    }

}
