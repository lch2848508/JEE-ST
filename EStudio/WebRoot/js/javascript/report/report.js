//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//WORD操作
var WORD = {
	instance : null,
	init : function() {
		if (WORD.instance == null)
			WORD.instance = new Object(WebOffice.ActiveDocument);
	},
	TABLE : {}
};

WORD.TABLE.add = function(row, col) {
	var table = WORD.instance.Tables.Add(WORD.instance.Characters.last, row, col);
	table.Borders.InsideLineStyle = 1;
	table.Borders.OutsideLineStyle = 1;
	table.Range.Cells.VerticalAlignment = 1;
	table.AllowAutoFit = 1;
	return table;
};

WORD.TABLE.get = function(index) {
	return WORD.instance.Tables.Item(index);
};

WORD.TABLE.merge = function(table, rect) {
	table.Cell(rect[0], rect[1]).Merge(table.Cell(rect[2], rect[3]));
};

WORD.TABLE.split = function(table, row, col, toRowNum, toColNum) {
	table.cell(row, col).split(toRowNum, toColNum);
};

WORD.TABLE.setValue = function(table, row, col, text) {
	table.cell(row, col).Range.Text = text;
};

WORD.TABLE.getValue = function(table, row, col) {
	var result = table.cell(row, col).Range.Text;
	result = result.substr(0, result.length - 2);
	return result;
};

WORD.TABLE.getCell = function(table, row, col) {
	return table.cell(row, col);
};

WORD.getDocument = function() {
	return instance;
};

WORD.getSelection = function() {
	return WORD.instance.Application.Selection;
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var EXCEL = {
	instance : null,
	init : function() {
		if (EXCEL.instance == null)
			EXCEL.instance = new Object(WebOffice.ActiveDocument);
	},
	STYLE : {
		FONT : {},
		BORDER : {}
	},
	ALIGN : {},
	SIZE : {},
	CELLS : {}
};
/**
 * 设置单元格值 row 行 注:行列开始值为1 col 列 value 值
 */
EXCEL.CELLS.setValue = function(row, col, value) {
	EXCEL.instance.ActiveSheet.Cells(row, col).Value = value;
};

/**
 * 批量填充单元格的值 startRow 还是行 startCol 开始列 records 将要填充的值 fields 字段列表
 */
EXCEL.CELLS.fills = function(startRow, startCol, records, fields) {
	if (!EXCEL.instance)
		return;
	for ( var i = 0; i < records.length; i++) {
		var record = records[i];
		for ( var j = 0; j < fields.length; j++) {
			var value = record[fields[j]];
			EXCEL.CELLS.setValue(startRow + i, startCol + j, value);
		}
	}
};

/**
 * 设置行高
 */

/**
 * 合并单元格
 */
EXCEL.CELLS.merge = function(rect) {
	if (!EXCEL.instance)
		return;
	var range = EXCEL.instance.ActiveSheet.Range(EXCEL.instance.ActiveSheet.Cells(rect[0], rect[1]), EXCEL.instance.ActiveSheet.Cells(rect[2], rect[3]));
	range.MergeCells = true;
};

/**
 * 拆分单元格
 */
EXCEL.CELLS.split = function(rect) {
	if (!EXCEL.instance)
		return;
	var range = EXCEL.instance.ActiveSheet.Range(EXCEL.instance.ActiveSheet.Cells(rect[0], rect[1]), EXCEL.instance.ActiveSheet.Cells(rect[2], rect[3]));
	range.MergeCells = false;
};

/**
 * 行对齐
 */
EXCEL.ALIGN.rowsAlign = function(startRow, endRow, align, valign) {
	if (!EXCEL.instance)
		return;
	for ( var i = startRow; i <= endRow; i++) {
		var range = EXCEL.instance.ActiveSheet.Rows(i);
		range.HorizontalAlignment = align;
		range.VerticalAlignment = valign;
	}
};

/**
 * 列对齐
 */
EXCEL.ALIGN.colsAlign = function(startCol, endCol, align, valign) {
	if (!EXCEL.instance)
		return;
	for ( var i = startCol; i <= endCol; i++) {
		var range = EXCEL.instance.ActiveSheet.Columns(i);
		range.HorizontalAlignment = align;
		range.VerticalAlignment = valign;
	}
};

/**
 * 单行对齐
 */
EXCEL.ALIGN.rowAlign = function(row, align, valign) {
	EXCEL.rowsAlign(row, row, align, valign);
};

/**
 * 多行对齐
 */
EXCEL.ALIGN.colAlign = function(col, align, valign) {
	EXCEL.colsAlign(col, col, align, valign);
};
/**
 * 单元格对齐
 */
EXCEL.ALIGN.cellAlign = function(row, col, align, valign) {
	if (!EXCEL.instance)
		return;
	var cell = EXCEL.instance.ActiveSheet.Cells(row, col);
	cell.HorizontalAlignment = align;
	cell.VerticalAlignment = valign;
};

/**
 * 单元格范围对齐
 */
EXCEL.ALIGN.cellsAlign = function(rect, align, valign) {
	if (!EXCEL.instance)
		return;
	var range = EXCEL.instance.ActiveSheet.Range(EXCEL.instance.ActiveSheet.Cells(rect[0], rect[1]), EXCEL.instance.ActiveSheet.Cells(rect[2], rect[3]));
	range.HorizontalAlignment = align;
	range.VerticalAlignment = valign;
};

/**
 * 常量定义
 */
EXCEL.CONST = {
	HALIGN : {
		LEFT : -4131,
		CENTER : -4108,
		RIGHT : -4152
	},
	VALIGN : {
		TOP : -4160,
		MIDDLE : -4108,
		BOTTOM : -4107
	}
};

// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

EXCEL.SIZE.rowsHeight = function(startRow, endRow, height) {
	if (!EXCEL.instance)
		return;
	for ( var i = startRow; i <= endRow; i++) {
		EXCEL.instance.ActiveSheet.Rows(i).RowHeight = height;
	}
};

EXCEL.SIZE.rowHeight = function(row, height) {
	EXCEL.SIZE.rowsHeight(row, row, height);
};

EXCEL.SIZE.colsWidth = function(startCol, endCol, width) {
	if (!EXCEL.instance)
		return;
	for ( var i = startCol; i <= endCol; i++) {
		EXCEL.instance.ActiveSheet.Columns(i).ColumnWidth = width;
	}
};

EXCEL.SIZE.colWidth = function(col, width) {
	EXCEL.SIZE.colsWidth(col, col, width);
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 字体
EXCEL.STYLE.FONT.cellsFont = function(rect, fontName, fontSize) {
	if (!EXCEL.instance)
		return;
	var range = EXCEL.instance.ActiveSheet.Range(EXCEL.instance.ActiveSheet.Cells(rect[0], rect[1]), EXCEL.instance.ActiveSheet.Cells(rect[2], rect[3]));
	if (fontName != undefined && fontName != null)
		range.Font.Name = fontName;
	if (fontSize != undefined && fontSize != null)
		range.Font.Size = fontSize;
};

EXCEL.STYLE.FONT.cellFont = function(row, col, fontName, fontSize) {
	EXCEL.STYLE.FONT.cellsFont([ row, col, row, col ], fontName, fontSize);
};

/**
 * 设置多行字体
 */
EXCEL.STYLE.FONT.rowsFont = function(startRow, endRow, fontName, fontSize) {
	if (!EXCEL.instance)
		return;
	for ( var i = startRow; i <= endRow; i++) {
		var range = EXCEL.instance.ActiveSheet.Rows(i);
		if (fontName != undefined && fontName != null)
			range.Font.Name = fontName;
		if (fontSize != undefined && fontSize != null)
			range.Font.Size = fontSize;
	}
};

/**
 * 设置行字体
 */
EXCEL.STYLE.FONT.rowFont = function(row, fontName, fontSize) {
	EXCEL.rowsFont(row, row, fontName, fontSize);
};

/**
 * 设置多列字体
 */
EXCEL.STYLE.FONT.colsFont = function(startCol, endCol, fontName, fontSize) {
	if (!EXCEL.instance)
		return;
	for ( var i = startCol; i <= endCol; i++) {
		var range = EXCEL.instance.ActiveSheet.Columns(i);
		if (fontName != undefined && fontName != null)
			range.Font.Name = fontName;
		if (fontSize != undefined && fontSize != null)
			range.Font.Size = fontSize;
	}
};

/**
 * 设置单列字体
 */
EXCEL.STYLE.FONT.colFont = function(col, fontName, fontSize) {
	EXCEL.STYLE.FONT.colsFont(col, col, fontName, fontSize);
};
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 边框
EXCEL.STYLE.BORDER.cellsBorder = function(rect, left, top, right, bottom) {
	if (!EXCEL.instance)
		return;
	var range = EXCEL.instance.ActiveSheet.Range(EXCEL.instance.ActiveSheet.Cells(rect[0], rect[1]), EXCEL.instance.ActiveSheet.Cells(rect[2], rect[3]));

	if (left)
		range.Borders(1).Weight = 2;
	if (right)
		range.Borders(2).Weight = 2;
	if (top)
		range.Borders(3).Weight = 2;
	if (bottom)
		range.Borders(4).Weight = 2;
};

EXCEL.STYLE.BORDER.cellBorder = function(row, col, left, top, right, bottom) {
	EXCEL.STYLE.BORDER.cellsBorder([ row, col, row, col ], left, top, right, bottom);
};

EXCEL.STYLE.BORDER.rowsBorder = function(startRow, endRow, left, top, right, bottom) {
	if (!EXCEL.instance)
		return;
	for ( var i = startRow; i <= endRow; i++) {
		var range = EXCEL.instance.ActiveSheet.Rows(i);
		if (left)
			range.Borders(1).Weight = 2;
		if (right)
			range.Borders(2).Weight = 2;
		if (top)
			range.Borders(3).Weight = 2;
		if (bottom)
			range.Borders(4).Weight = 2;
	}
};

EXCEL.STYLE.BORDER.rowBorder = function(row, left, top, right, bottom) {
	EXCEL.STYLE.BORDER.rowsBorder(row, row, left, top, right, bottom);
};

EXCEL.STYLE.BORDER.colsBorder = function(startCol, endCol, left, top, right, bottom) {
	if (!EXCEL.instance)
		return;
	for ( var i = startCol; i <= endCol; i++) {
		var range = EXCEL.instance.ActiveSheet.Columns(i);
		if (left)
			range.Borders(1).Weight = 2;
		if (right)
			range.Borders(2).Weight = 2;
		if (top)
			range.Borders(3).Weight = 2;
		if (bottom)
			range.Borders(4).Weight = 2;
	}
};

EXCEL.STYLE.BORDER.colBorder = function(col, left, top, right, bottom) {
	EXCEL.STYLE.BORDER.colsBorder(col, col, left, top, right, bottom);
};
// //////////////////////////////////////////////////////////////////////////////////////////////////////////

