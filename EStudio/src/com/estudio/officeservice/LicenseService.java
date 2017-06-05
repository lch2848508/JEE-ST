package com.estudio.officeservice;

import java.io.InputStream;

public class LicenseService {
    private LicenseService() {

    };

    public static void loadOfficeLicense() {
        try {
            com.aspose.cells.License excelLicense = new com.aspose.cells.License();
            com.aspose.words.License wordLicense = new com.aspose.words.License();
            com.aspose.slides.License pptLicense = new com.aspose.slides.License();

            InputStream licenseStream = LicenseService.class.getClassLoader().getResourceAsStream("/excellicense.xml");
            excelLicense.setLicense(licenseStream);
            licenseStream = LicenseService.class.getClassLoader().getResourceAsStream("/excellicense.xml");
            wordLicense.setLicense(licenseStream);
            licenseStream = LicenseService.class.getClassLoader().getResourceAsStream("/excellicense.xml");
            pptLicense.setLicense(licenseStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
