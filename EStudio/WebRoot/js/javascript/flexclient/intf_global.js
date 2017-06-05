// //////////////////////////////////////////////////////////////////////////////////////////////////////////
var LISTORTREEDATASETCACHE = {};
var GLOBAL = {
    _cache : {},
    UTILS : {},
    WIN : {},
    DB : {},
    IFRAME : {}
};
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
var AJAX_ERROR_MSG = {
    r : false,
    msg : "警告：同服务器的连接超时，请检查网络是否正常(一般原因为网速太慢)。"
};
// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.getCache = function(key) {
    return GLOBAL._cache[key];
};

GLOBAL.setCache = function(key, obj) {
    GLOBAL._cache[key] = obj;
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.DB.getServerUniqueid = function() {
    var result = null;
    $.get("../client/uniqueid?o=get&cached=1", function(text) {
                var json = getObjectAjaxValue(text);
                result = json["ids"][0];
            });
    return result;
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
// 获取系列号
GLOBAL.DB.getSerialCode = function(template) {
    var result = null;
    $.post("../client/uniqueid?o=getSerialNumber", {
                f : template
            }, function(text) {
                var json = $.evalJSON(text);
                if (json && json.r)
                    result = json.serial;
            });
    return result;
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.DB.getLookupDatasetJson = function(portalID, datasetName, params) {
    var result = null;
    params["portal_id"] = portalID;
    params["pageable"] = false;
    params["multiselect"] = false;
    params["dataset"] = datasetName;
    params["o"] = "getlookupdatasetjson";

    var url = "../client/dataservice";
    $.post(url, params, function(text) {
                result = getObjectAjaxValue(text, true);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

GLOBAL.DB.executeSQL = function(portalID, sqlName, params) {
    var result = false;
    params["portal_id"] = portalID;
    params["sqlname"] = sqlName;
    params["o"] = "executesql";
    $.post("../client/dataservice", filterParams(params), function(text) {
                result = getBooleanAjaxValue(text);
            }).fail(function() {
                result = false;
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
            });
    return result;
};

GLOBAL.DB.batchExecuteSQL = function(portalID, sqlName, params) {
    var result = false;
    params = {
        params : $.toJSON(WrapObjectToArray(params))
    };
    params["portal_id"] = portalID;
    params["sqlname"] = sqlName;
    params["o"] = "batchexecutesql";
    $.post("../client/dataservice", filterParams(params), function(text) {
                result = getBooleanAjaxValue(text);
            }).fail(function() {
                result = false;
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
            });
    return result;
};

GLOBAL.DB.gridDataSet = function(portalID, datasetName, params, callbackFunname, width, height, pageAble, multiSelect, iframeid) {
    if (!pageAble)
        pageAble = false;
    if (!multiSelect)
        multiSelect = false;
    params = __JS_OBJECT_2_FLEX_OBJECT__(params);
    params["portalID"] = portalID;
    params["pageable"] = pageAble;
    params["multiselect"] = multiSelect;
    params["dataset"] = datasetName;
    params["iframeid"] = iframeid;
    params["callbackfunname"] = callbackFunname;
    params["width"] = width;
    params["height"] = height;
    MainFormFlash.Callback_gridDataSet(filterParams(params));
};

GLOBAL.DB.treeDataSet = function(portalID, datasetName, params, callbackFun, width, height, multiSelect, iframeID, labelField, groupField, extParams, isClientCache) {
    var key = datasetName + "_" + portalID;
    if (isClientCache && LISTORTREEDATASETCACHE[key]) {
        MainFormFlash.Callback_treeDataSet(LISTORTREEDATASETCACHE[key], multiSelect, callbackFun, iframeID, width, height, extParams);
    } else {
        if (!labelField)
            labelField = "";
        if (!groupField)
            groupField = "";

        if (!multiSelect)
            multiSelect = false;
        params["portal_id"] = portalID;
        params["multiselect"] = multiSelect;
        params["dataset"] = datasetName;
        params["labelField"] = labelField;
        params["groupField"] = groupField;
        var url = "../client/listortreedataset?o=tree";
        $.post(url, params, function(text) {
                    if (text != "") {
                        result = $.evalJSON(text);
                        if (result && result["r"]) {
                            var temp = __JS_OBJECT_2_FLEX_OBJECT__(result.rows);
                            MainFormFlash.Callback_treeDataSet(temp, multiSelect, callbackFun, iframeID, width, height, extParams);
                            if (isClientCache)
                                LISTORTREEDATASETCACHE[key] = temp;
                        } else {
                            flexAlert("后台数据库发生错误，错误原因如下：\n" + result["msg"], 2);
                        }
                    }
                });
    }
};

GLOBAL.DB.getDataSetGridDefine = function(params) {
    params = params[0];
    portalID = params[0];
    datasetName = params[1];
    pageable = params[2];
    var result = null;
    var extParams = params[3];
    extParams.portal_id = portalID;
    extParams.dataset = datasetName;
    extParams.pageable = pageable;

    $.post("../client/listortreedataset?o=list", extParams, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};

GLOBAL.DB.getDataSetGridDatas = function(params) {
    params = params[0];
    portalID = params[0];
    datasetName = params[1];
    pageable = params[2];
    page = params[3];
    var extParams = params[4];

    params = {
        o : "getDataSetGridDatas",
        portal_id : portalID,
        dataset : datasetName,
        pageable : pageable,
        p : page
    };

    for (var k in extParams) {
        if (!(k in params))
            params[k] = extParams[k];
    }

    pageable = params["pageable"];
    if (pageable * 1 == 1)
        params["pageable"] = "true";

    var result = null;
    $.post("../client/listortreedataset", params, function(text) {
                result = getObjectAjaxValue(text);
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
};
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.WIN.gridDataSet = GLOBAL.DB.gridDataSet;
GLOBAL.WIN.treeDataSet = GLOBAL.DB.treeDataSet;
var MODALEDIALOG2CALLBACKFUN = {};

GLOBAL.WIN.CloseFlexModalDialog = function(winid) {
    MainFormFlash.Callback_closeModalDialog(winid);
};

// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.WIN.modalDialog = function(formids, width, height, caption, readonly, params, callbackFun, closeCallbackFun, frameid, isSilent) {
    MainFormFlash.Callback_modalForms(WrapObjectToArray(formids), __JS_OBJECT_2_FLEX_OBJECT__(caption), readonly, __JS_OBJECT_2_FLEX_OBJECT__(params), callbackFun, closeCallbackFun, frameid, isSilent);
};
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.WIN.closeDialog = function(formids) {
    MainFormFlash.Callback_closeModalForms(WrapObjectToArray(formids));
};

GLOBAL.WIN.saveFormDialog = function(formids) {
    MainFormFlash.Callback_saveModalForms(WrapObjectToArray(formids));
};

// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.WIN.moduleSWFDialog = function(caption, width, height, url, params, iframeId, callFunctionName) {
    MainFormFlash.Callback_moduleSWFDialog(caption, width, height, url, params, iframeId, callFunctionName);
}
// //////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 杂项函数
GLOBAL.UTILS.flexAlert = function(str, type) {
    if (isNaN(type))
        type = 0;
    MainFormFlash.Callback_alert(__JS_OBJECT_2_FLEX_OBJECT__(str), type);
};

GLOBAL.UTILS.flexConfirm = function(msg, funname, cancelFunname, iframeid) {
    MainFormFlash.Callback_confirm(__JS_OBJECT_2_FLEX_OBJECT__(msg), funname, cancelFunname, iframeid);
};

GLOBAL.UTILS.getEnvironmentValue = function(params) {
    var name = params[0];
    var defaultValue = params[1];
    if (name == "NULL")
        return null;
    else if (name == "REQ.USER_ID")
        return -1;
    return defaultValue;
};

GLOBAL.UTILS.sendMobileMessage = function(mobiles, content) {
    var formids = [250410];
    MainFormFlash.Callback_modalForms(formids, 480, 240, __JS_OBJECT_2_FLEX_OBJECT__("发送手机短信!"), false, {
                mobiles : mobiles,
                content : content
            });
};

GLOBAL.UTILS.sendEmail = function(address, subject, content) {
    GLOBAL.UTILS.flexAlert(__JS_OBJECT_2_FLEX_OBJECT__("尚未实现此功能!"));
};

GLOBAL.UTILS.OpenURL = function(url) {
    MainFormFlash.Callback_goURL(__JS_OBJECT_2_FLEX_OBJECT__(url));
};

GLOBAL.UTILS.popupMessage = function(category, message, iserror) {
    MainFormFlash.Callback_popupMSNMessage(__JS_OBJECT_2_FLEX_OBJECT__(category), __JS_OBJECT_2_FLEX_OBJECT__(message), iserror);
};
GLOBAL.popupMessage = GLOBAL.UTILS.popupMessage;
// //////////////////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.IFRAME.create = function(params) {
    var frameid = params[0];
    var htmlContent = params[1];

    $("#" + frameid).remove();
    var html = "<iframe id='" + frameid + "' name='" + frameid + "' style='display:none'></iframe>";

    var ifrdoc = $(html).appendTo(document.body)[0].contentWindow.document;
    ifrdoc.designMode = "on"; // 文档进入可编辑模式
    ifrdoc.open(); // 打开流
    ifrdoc.write(htmlContent);
    ifrdoc.close(); // 关闭流
    ifrdoc.designMode = "off"; // 文档进入非可编辑模式

    return true;
};

GLOBAL.IFRAME.createBySrc = function(params) {
    var frameid = params[0];
    var src = params[1];
    $("#" + frameid).remove();
    var html = "<iframe id='" + frameid + "' name='" + frameid + "' style='display:none' src='" + src + "'></iframe>";
    var ifrdoc = $(html).appendTo(document.body)[0].contentWindow.document;
    return true;
};

GLOBAL.IFRAME.remove = function(params) {
    var frameid = params[0];
    $("#" + frameid).remove();
};

GLOBAL.IFRAME.executeFunction = function(params) {
    var frameID = params[0];
    var funNames = params[1].split(".");
    var funParams = params[2];
    var iframe = $("#" + frameID)[0];
    if (iframe) {
        var win = document.getElementById(frameID).contentWindow; // iframe.contentWindow;
        var fun = win[funNames[0]];
        if (fun && typeof(fun) == "function")
            return fun(funParams);
    }
};

function executeFunctionAgain(params) {
    // copyToClipboard(params);
    // alert(params);
    params = StringUtils.replaceAll(params, "	", "");
    params = $.evalJSON(params);
    var frameID = params[0];
    var funNames = params[1].split(".");
    var funParams = params[2];
    var iframe = $("#" + frameID)[0];
    if (iframe) {
        var win = iframe.contentWindow;
        var fun = win[funNames[0]];
        if (typeof(fun) == "function")
            return fun(funParams);
        else
            return fun;
    }
};

// //////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.PRINT = {};
GLOBAL.PRINT.print = function(id, params) {
    var url = "../report/report.jsp?templateid=" + id + "&" + $.param(params) + "&nobutton=0";
    GLOBAL.UTILS.OpenURL(url);
};

GLOBAL.PRINT.floatTemplate = function(id, params, caption, w, h) {
    var url = "../report/reportTemplate.jsp?templateid=" + id + "&" + $.param(params);
    var width = $(document.body).width() - 60;
    var height = $(document.body).height() - 120;
    if (w)
        width = Math.min(width, w);
    if (h)
        height = Math.min(height, h);
    if (!caption)
        caption = "详细内容";

    top.openFlatWindow(caption, url, width, height);
};

GLOBAL.PRINT.printTemplate = function(id, params) {
    var url = "../report/reportOffice.jsp?templateid=" + id + "&" + $.param(params);
    GLOBAL.UTILS.OpenURL(url);
};

GLOBAL.PRINT.printOfficeReport = GLOBAL.PRINT.printTemplate;

// //////////////////////////////////////////////////////////////////////////////////////////////
GLOBAL.gotoPortal = function(caption, newCaption, params) {
    if (!newCaption || newCaption == "")
        newCaption = caption;
    return MainFormFlash.Callback_gotoPortal(__JS_OBJECT_2_FLEX_OBJECT__(caption), __JS_OBJECT_2_FLEX_OBJECT__(newCaption), __JS_OBJECT_2_FLEX_OBJECT__(params));
};

GLOBAL.closePortal = function(caption) {
    return MainFormFlash.Callback_closePortal(__JS_OBJECT_2_FLEX_OBJECT__(caption));
};

GLOBAL.executePortalFunction = function(portalName, funName, params, newCaption) {
    if (!newCaption || newCaption == "")
        newCaption = portalName;
    return MainFormFlash.Callback_executePortalFunction(portalName, funName, __JS_OBJECT_2_FLEX_OBJECT__(params), __JS_OBJECT_2_FLEX_OBJECT__(newCaption));
};

GLOBAL.commonWords = function(items, iframeId, funname, initStr) {
    if (!initStr)
        initStr = "";
    return MainFormFlash.Callback_commonWords(__JS_OBJECT_2_FLEX_OBJECT__(items), iframeId, funname, initStr);
};

var callPortalFunctionParams = {};
function getCallPortalFunctionParams(frameId) {
    var result = callPortalFunctionParams[frameId];
    callPortalFunctionParams[frameId] = null;
    return result;
}

if (!window.console) {
    window.console = {
        log : function() {
        }
    };
}

function registerCallPortalFunctionParams(params) {
    console.log($.toJSON(params));
    callPortalFunctionParams[params[0]] = [params[1], params[2]];
}

function executePredefineHookfunction(frameId) {
    var params = getCallPortalFunctionParams(frameId);
    if (params) {
        GLOBAL.IFRAME.executeFunction([frameId, params[0], params[1]]);
    }
}
// //////////////////////////////////////////////////////////////////////////////////////////////

GLOBAL.STORAGE = {};
GLOBAL.STORAGE.___cache___ = {};
GLOBAL.STORAGE.set = function(key, value) {
    return GLOBAL.STORAGE.___cache___[key] = value;
};
GLOBAL.STORAGE.get = function(key) {
    var result = GLOBAL.STORAGE.___cache___[key];
    GLOBAL.STORAGE.___cache___[key] = null;
    return result;
};
// /////////////////////////////////////////////////////////////////////////////////////////////
var getDataSetGridDatas = GLOBAL.DB.getDataSetGridDatas;
var getDataSetGridDefine = GLOBAL.DB.getDataSetGridDefine;

var getEnvironmentValue = GLOBAL.UTILS.getEnvironmentValue;
var selectDate = GLOBAL.UTILS.selectDate;

var createIFrame = GLOBAL.IFRAME.create;
var createIFrameBySrc = GLOBAL.IFRAME.createBySrc;
var removeIFrame = GLOBAL.IFRAME.remove;
var executeFrameFunction = GLOBAL.IFRAME.executeFunction;

var CloseFlexModalDialog = GLOBAL.WIN.CloseFlexModalDialog;
var modalDialog = GLOBAL.WIN.modalDialog;
var getServerUniqueid = GLOBAL.DB.getServerUniqueid;

var flexAlert = GLOBAL.UTILS.flexAlert;

var sendMobileMessage = GLOBAL.UTILS.sendMobileMessage;

var sendEmail = function(email, subject) {
    flexAlert("发送电子邮件功能正在开发中...");
};

var SendSMS = sendMobileMessage;

function filterParams(params) {
    var result = {};
    for (var k in params) {
        var v = params[k];
        if (!StringUtils.isEmpty(v))
            result[k] = v;
    }
    return result;
};

// ------------------------------------------------------------------------------
// 判断IFrame是否存在
function isIFrameExists(frameId) {
    return $("#" + frameId)[0] ? true : false;
}
// -------------------------------------------------------------------------------
function ProcErrorMessage(msg) {
    if (msg)
        MainFormFlash.Callback_loggerErrorMessage(__JS_OBJECT_2_FLEX_OBJECT__(msg));
}
// --------------------------------------------------------------------------------
function getWorkFlowUiIdByPortalName(portalName) {
    return MainFormFlash.Callback_getWorkFlowUiIdByPortalName(__JS_OBJECT_2_FLEX_OBJECT__(portalName));
}
// ---------------------------------------------------------------------------------
function gridDataToTreeData(datas, labelField, groupField) {
    var result = [];
    var map = {};
    for (var i = 0; i < datas.length; i++) {
        var record = datas[i];
        var groupValue = record[groupField];
        if (!groupValue || groupValue == "")
            groupValue = "无分组";
        var groupRecord = map[groupValue];
        if (!groupRecord) {
            groupRecord = {
                label : groupValue,
                children : []
            };
            map[groupValue] = groupRecord;
            result.push(groupRecord);
        }
        record.label = record[labelField];
        groupRecord.children.push(record);
    }
    return result;
}
// --------------------------------------------------------------------------------------
function WrapObjectToArray(value, isForce) {
    var result = value;
    if (value) {
        var type = Object.prototype.toString.apply(value);
        if (type === "[object Array]") {
            result = [];
            for (var i = 0; i < value.length; i++)
                result[i] = WrapObjectToArray(value[i], false);
        } else if (type === "[object Object]") {
            var length = value.hasOwnProperty("length") ? value.length * 1 : propNumbers(value);
            var tempResult = [];
            for (var i = 0; i < length; i++) {
                if (value.hasOwnProperty(i + ""))
                    tempResult.push(value[i]);
                else
                    break;
            }
            result = tempResult.length == length && length ? tempResult : value;
            if (result instanceof Array)
                for (var k in result)
                    result[k * 1] = WrapObjectToArray(result[k * 1], false);
            else
                for (var k in result)
                    result[k] = WrapObjectToArray(result[k], false);
        }
    }
    if (isForce && !(result instanceof Array))
        result = [result];
    return result;
}

// ---------------------------------------------------------------------------------------
function propNumbers(obj) {
    var result = 0;
    for (var k in obj)
        result++;
    return result;
}
// ---------------------------------------------------------------------------------------
GLOBAL.DB.getServerDate = function(formatStr) {
    var result = null;
    $.post("../client/uniqueid?o=getSysdate", {
                "f" : formatStr
            }, function(text) {
                var json = getObjectAjaxValue(text);
                if (json)
                    result = json.sysdate;
            }).fail(function() {
                GLOBAL.popupMessage("系统", "同服务器连接超时(网速太慢),请检查网络后重试！", true);
                result = AJAX_ERROR_MSG;
            });
    return result;
}
// --------------------------------------------------------------------------------------
function getBooleanAjaxValue(text) {
    var result = false;
    var json = $.evalJSON(text);
    if (json) {
        if (json.r)
            result = true;
        else if (json.errorCode * 1 == -65535)
            procNoLoginMessage();
        else
            ProcErrorMessage(json.msg);
    }
    return result;
}
// ---------------------------------------------------------------------------------------
function getObjectAjaxValue(text, disabledSerial) {
    var result = null;
    var json = $.evalJSON(text);
    if (json) {
        if (json.r)
            result = json;
        else if (json.errorCode * 1 == -65535)
            procNoLoginMessage();
        else
            ProcErrorMessage(json.msg);
    }
    return result;
}
// ----------------------------------------------------------------------------------------
// session 会话丢失处理函数
function procNoLoginMessage() {
    MainFormFlash.Callback_raiseSessionMissError();
}
// -----------------------------------------------------------------------------------------
function procGoLoginPage() {
    window.location.href = "../index.jsp";
}
// ------------------------------------------------------------------------------------------
function serialExternalInterfaceCall(funName, params) {
    var fun = window[funName];
    return __JS_OBJECT_2_FLEX_OBJECT__(fun(__FLEX_OBJECT_2_JS_OBJECT__(params)));
}
// --------------------------------------------------------------------------------------------
function __FLEX_OBJECT_2_JS_OBJECT__(obj) {

    var result = obj;
    var type = Object.prototype.toString.apply(obj);
    if (type === '[object Array]') {
        result = [];
        for (var i = 0; i < obj.length; i++)
            result.push(__FLEX_OBJECT_2_JS_OBJECT__(obj[i]));
    } else if (type === '[object Object]' && obj !== null) {
        result = {};
        for (var k in obj)
            result[k] = __FLEX_OBJECT_2_JS_OBJECT__(obj[k]);
    }
    return result;
}
// ---------------------------------------------------------------------------------------------
function __JS_OBJECT_2_FLEX_OBJECT__(obj) {
    var result = obj;
    var type = Object.prototype.toString.apply(obj);
    if (type === '[object Array]') {
        result = [];
        for (var i = 0; i < obj.length; i++)
            result.push(__JS_OBJECT_2_FLEX_OBJECT__(obj[i]));
    } else if (type === '[object Object]' && obj !== null) {
        result = {};
        for (var k in obj)
            result[k] = __JS_OBJECT_2_FLEX_OBJECT__(obj[k]);
    }
    return result;
}
// ----------------------------------------------------------------------------------------------
function testClick() {
    // alert($.toJSON(selectRecord));
}
// ////////////////////////////////////////////////////////////////////////////////////////////
function navigator_by_area() {
    // alert("navigator_by_area");
}
// ////////////////////////////////////////////////////////////////////////////////////////////
function __str2json__(str) {
    return $.evalJSON(str);
}
// ////////////////////////////////////////////////////////////////////////////////////////////
function __dynamicCreateJSFunction__(str) {
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.text = str;
    head.appendChild(script);
}
// ////////////////////////////////////////////////////////////////////////////////////////////
var DHTML4FLEX = {
    _content_ : []
};
DHTML4FLEX.init = function() {
    DHTML4FLEX._content_ = [];
    return DHTML4FLEX;
}

DHTML4FLEX.beginParagraph = function(leftMargin, rightMargin, leading) {
    leftMargin = Convert.try2Number(leftMargin, 0);
    rightMargin = Convert.try2Number(rightMargin, 0);
    leading = Convert.try2Number(leading, 2);
    DHTML4FLEX._content_.push("<textformat leftmargin='" + leftMargin + "' rightmargin='" + rightMargin + "' leading='" + leading + "'>");
    return DHTML4FLEX;
}

DHTML4FLEX.endParagraph = function() {
    DHTML4FLEX._content_.push("</textformat>");
    return DHTML4FLEX;
}

DHTML4FLEX.beginFontStyle = function(fontName, color, size) {
    DHTML4FLEX._content_.push("<font");
    if (!StringUtils.isEmpty(fontName))
        DHTML4FLEX._content_.push(" face='" + fontName + "'");
    if (!StringUtils.isEmpty(color))
        DHTML4FLEX._content_.push(" color='" + color + "'");
    if (!StringUtils.isEmpty(size))
        DHTML4FLEX._content_.push(" size='" + size + "'");
    DHTML4FLEX._content_.push(">");
    return DHTML4FLEX;
}

DHTML4FLEX.endFontStyle = function() {
    DHTML4FLEX._content_.push("</font>");
    return DHTML4FLEX;
}

DHTML4FLEX.beginBold = function() {
    DHTML4FLEX._content_.push("<b>");
    return DHTML4FLEX;
}

DHTML4FLEX.endBold = function() {
    DHTML4FLEX._content_.push("</b>");
    return DHTML4FLEX;
}

DHTML4FLEX.addContent = function(text) {
    text = StringUtils.escapeHtml(StringUtils.trim(StringUtils.replaceAll(text, "\r\n", "\n")));
    DHTML4FLEX._content_.push(text);
    return DHTML4FLEX;
}

DHTML4FLEX.enter = function() {
    DHTML4FLEX._content_.push("<br>");
    return DHTML4FLEX;
}

DHTML4FLEX.toString = function() {
    return DHTML4FLEX._content_.join("");
}

function dynamicCreateJS(jsCode) {
    var js = "<script type='text/javascript' language='javascript'>" + jsCode + "</script>";
    $("head").append(js);
}