package com.estudio.officeservice;

import com.aspose.cells.CellBorderType;
import com.aspose.cells.CellValueType;

public class ExcelServiceConst {

    // 边框
    public static final int BORDER_DASH_DOT = CellBorderType.DASH_DOT;
    public static final int BORDER_DASH_DOT_DOT = CellBorderType.DASH_DOT_DOT;
    public static final int BORDER_DASHED = CellBorderType.DASHED;
    public static final int BORDER_DOTTED = CellBorderType.DOTTED;
    public static final int BORDER_DOUBLE = CellBorderType.DOUBLE;
    public static final int BORDER_HAIR = CellBorderType.HAIR;
    public static final int BORDER_MEDIUM = CellBorderType.MEDIUM;
    public static final int BORDER_MEDIUM_DASH_DOT = CellBorderType.MEDIUM_DASH_DOT;
    public static final int BORDER_MEDIUM_DASH_DOT_DOT = CellBorderType.MEDIUM_DASH_DOT_DOT;
    public static final int BORDER_MEDIUM_DASHED = CellBorderType.MEDIUM_DASHED;
    public static final int BORDER_NONE = CellBorderType.NONE;
    public static final int BORDER_SLANTED_DASH_DOT = CellBorderType.SLANTED_DASH_DOT;
    public static final int BORDER_THICK = CellBorderType.THICK;
    public static final int BORDER_THIN = CellBorderType.THIN;
    
    //数据类型
    public static final int TYPE_NULL = CellValueType.IS_NULL;
    public static final int TYPE_NUMBER = CellValueType.IS_NUMERIC;
    public static final int TYPE_STRING = CellValueType.IS_STRING;
    public static final int TYPE_DATE = CellValueType.IS_DATE_TIME;
    

    // 颜色
    public static final int COLOR_BLACK = com.aspose.cells.Color.getBlack().toArgb();
    public static final int COLOR_WHITE = com.aspose.cells.Color.getWhite().toArgb();
    public static final int COLOR_RED = com.aspose.cells.Color.getRed().toArgb();
    public static final int COLOR_GREEN = com.aspose.cells.Color.getGreen().toArgb();
    public static final int COLOR_BLUE = com.aspose.cells.Color.getBlue().toArgb();
    public static final int COLOR_NAVY = com.aspose.cells.Color.getNavy().toArgb();
    public static final int COLOR_YELLOW = com.aspose.cells.Color.getYellow().toArgb();
    public static final int COLOR_GRAY = com.aspose.cells.Color.getGray().toArgb();

    private ExcelServiceConst() {
    };

    public static final ExcelServiceConst instance = new ExcelServiceConst();
}
