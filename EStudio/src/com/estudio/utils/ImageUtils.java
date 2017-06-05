package com.estudio.utils;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;

public final class ImageUtils {

    private String grapicsImagePath = "";

    public String getGrapicsImagePath() {
        return grapicsImagePath;
    }

    public void setGrapicsImagePath(final String grapicsImagePath) {
        this.grapicsImagePath = grapicsImagePath;
        ProcessStarter.setGlobalSearchPath(grapicsImagePath);
    }

    private ImageUtils() {
        super();
    }

    private static final ImageUtils INSTANCE = new ImageUtils();

    public static ImageUtils getInstance() {
        return INSTANCE;
    }

    public Boolean resizeImage(final String fileName, final String outFileName, final int resizeWidth, final int resizeHeight) {
        boolean result = false;
        try {
            final ConvertCmd cmd = new ConvertCmd(true);
            final IMOperation op = new IMOperation();
            op.addImage();
            op.antialias();
            op.scale(resizeWidth, resizeHeight);
            op.gravity("center");
            op.extent(resizeWidth, resizeHeight);
            op.quality(85.0);
            op.addImage();
            cmd.run(op, fileName, outFileName);
            result = true;
        } catch (final Exception e) {
        	e.printStackTrace();
        }
        return result;
    }

}
