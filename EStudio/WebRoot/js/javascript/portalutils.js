// 定义Portal相关的一些辅助函数
// 回复父窗体的内容
function RestoreParentWindow(needDeleteGridRow) {
    var href = window.location.href;
    var topDetailShowType = top["DETAILSHOWTYPE"];
    if (!topDetailShowType) return;
    var topDetailHistory = top["DETAILPARENTHISTORY"];
    var topTabSheetParent = top["URL2TABSHEETPARENT"];

    for (var k in topDetailShowType) {
        if (href.indexOf(k) != -1) {
            if (topDetailShowType[k] == 0) { // 顶替父窗体
                topDetailHistory[k] = null;
                window.history.back();
            } else if (topDetailShowType[k] == 1) {
                var parentTabsheetID = topTabSheetParent[k];
                if (top.TabSheet.cells(parentTabsheetID) != null) {
                    top.TabSheet.setTabActive(parentTabsheetID);
                    if (needDeleteGridRow) {
                        var iframe = $("iframe", top.TabSheet.cells(parentTabsheetID));
                        var win = iframe[0].contentWindow;
                        if (win["DeletePortalGridRow"]) win["DeletePortalGridRow"]();
                    }
                } else top.TabSheet.setTabActive(top.LAST_TABSHEET_ID);
                topTabSheetParent[k] = null;
                top.TabSheet.removeTab("MAIN_COMMON_TABSHEET");
            } else if (topDetailShowType[k] == 2) {
                var parentTabsheetID = topTabSheetParent[k];
                if (needDeleteGridRow) {
                    var iframe = $("iframe", top.TabSheet.cells(parentTabsheetID));
                    var win = iframe[0].contentWindow;
                    if (win["DeletePortalGridRow"]) win["DeletePortalGridRow"]();
                }
                topTabSheetParent[k] = null;
                top.WindowUtils.closeModalDialog(2);
            }
            topDetailShowType[k] = null;
            break;
        } else {
            var parentTabsheetID = topTabSheetParent[k];
            if (parent.TabSheet.cells(parentTabsheetID) != null) {
                parent.TabSheet.setTabActive(parentTabsheetID);
                if (needDeleteGridRow) {
                    var iframe = $("iframe", parent.TabSheet.cells(parentTabsheetID));
                    var win = iframe[0].contentWindow;
                    if (win["DeletePortalGridRow"]) win["DeletePortalGridRow"]();
                }
            } else parent.TabSheet.setTabActive(top.LAST_TABSHEET_ID);
            topTabSheetParent[k] = null;
            parent.TabSheet.removeTab("MAIN_COMMON_TABSHEET");
        }
    }

}

// ---------------------------------------------------------------------------------------------------------------
