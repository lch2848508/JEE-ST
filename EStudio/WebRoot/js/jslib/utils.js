var userAgentStr = navigator.userAgent.toLowerCase();
function isIE() {
    var rMsie = /(msie\s|trident.*rv:)([\w.]+)/;
    return rMsie.exec(userAgentStr) != null;
}

function isFirefor() {
    var rFirefox = /(firefox)\/([\w.]+)/;
    return rFirefox.exec(userAgentStr) != null;
}

function isChrome() {
    return /chrome\/(\d+\.\d)/i.test(userAgentStr);
}

// /////////////////////////////////////////////////////////////////////////////////////////
// 字符串辅助函数
var StringUtils = {};
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.before = function(InputStr, SubStr) {
    var index = InputStr.indexOf(SubStr);
    if (index == -1)
        return "";
    else
        return InputStr.substr(0, index);
}
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.after = function(InputStr, SubStr) {
    var index = InputStr.indexOf(SubStr);
    if (index == -1)
        return "";
    else
        return InputStr.substr(index + SubStr.length);
}
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.between = function(InputStr, SubStr1, SubStr2) {
    var str = StringUtils.after(InputStr, SubStr1);
    str = StringUtils.before(str, SubStr2);
    return str;
}
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.lTrim = function(InputStr) {
    return InputStr.replace(/(^\s*)/g, "");
}
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.rTrim = function(InputStr) {
    return InputStr.replace(/(\s*$)/g, "")
}
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.trim = function(InputStr) {
    return InputStr.replace(/(^\s*)|(\s*$)/g, "");
}
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.replaceAll = function(str, oldStr, newStr) {
    if (StringUtils.isEmpty(str))
        return "";
    return str.split(oldStr).join(newStr);
}
// /////////////////////////////////////////////////////////////////////////////////////////
StringUtils.isEmpty = function(str) {
    if (typeof(str) == "number")
        return false;
    return (str == undefined || str == null || str === "");
}

StringUtils.escapeHtml = function(str) {
    return encodeHtml(str);
}
var REGX_HTML_ENCODE = /"|&|'|<|>|[\x00-\x20]|[\x7F-\xFF]|[\u0100-\u2700]/g;
function encodeHtml(s) {
    return (typeof s != "string") ? s : s.replace(REGX_HTML_ENCODE, function($0) {
                var c = $0.charCodeAt(0), r = ["&#"];
                c = (c == 0x20) ? 0xA0 : c;
                r.push(c);
                r.push(";");
                return r.join("");
            });
}
// /////////////////////////////////////////////////////////////////////////////////////////
// 数组辅助函数
var ARRAY = {};
ARRAY.indexOf = function(arr, obj) {
    return $.inArray(obj, arr);
}
// /////////////////////////////////////////////////////////////////////////////////////////
// 文件辅助函数
var FILEUTILS = {};
FILEUTILS.includeJs = include_js;
FILEUTILS.includeCss = include_css;
// /////////////////////////////////////////////////////////////////////////////////////////
function include_js(path) {
    var sobj = document.createElement('script');
    sobj.type = "text/javascript";
    sobj.src = path;
    var headobj = document.getElementsByTagName('head')[0];
    headobj.appendChild(sobj);
}

// /////////////////////////////////////////////////////////////////////////////////////////
function include_css(path) {
    var fileref = document.createElement("link")
    fileref.rel = "stylesheet";
    fileref.type = "text/css";
    fileref.href = path;
    var headobj = document.getElementsByTagName('head')[0];
    headobj.appendChild(fileref);
}
// /////////////////////////////////////////////////////////////////////////////////////////
var Convert = {};

Convert.try2Int = function(str, defaultValue) {
    if (defaultValue == undefined)
        defaultValue = 0;

    var result = defaultValue;
    try {
        result = parseInt(str);
        if (isNaN(result))
            result = defaultValue;
    } catch (e) {
    }
    return result;
}

Convert.try2Number = function(str, defaultValue) {
    if (defaultValue == undefined)
        defaultValue = 0.0;

    var result = defaultValue;
    try {
        result = parseFloat(str);
        if (isNaN(result))
            result = defaultValue;
    } catch (e) {
    }
    return result;
}

Convert.try2Date = function(str, defaultValue) {
    var result = defaultValue;
    var r = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
    if (r != null) {
        var d = new Date(r[1], r[3] - 1, r[4]);
        if (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4])
            result = d;
    }
    return result;
}
// ///////////////////////////////////////////////////////////////////////////////////////////////
var DateUtils = {};

DateUtils.addDay = function(date, day) {
    var result = new Date();
    result.setTime(date.getTime() + day * 24 * 3600 * 1000);
    return result;
}

DateUtils.addHour = function(date, hour) {
    var result = new Date();
    result.setTime(date.getTime() + hour * 3600 * 1000);
    return result;
}

DateUtils.addMinute = function(date, minute) {
    var result = new Date();
    result.setTime(date.getTime() + minute * 60 * 1000);
    return result;
}

DateUtils.addSecond = function(date, second) {
    var result = new Date();
    result.setTime(date.getTime() + second * 1000);
    return result;
}

DateUtils.toDate = function(str) {
    var date = null;
    var tempStrs = str.split(" ");
    var dateStrs = tempStrs[0].split("-");
    var year = parseInt(dateStrs[0], 10);
    var month = parseInt(dateStrs[1], 10) - 1;
    var day = parseInt(dateStrs[2], 10);
    if (tempStrs.length == 2) {
        var timeStrs = tempStrs[1].split("-");
        var hour = parseInt(timeStrs[0], 10);
        var minute = parseInt(timeStrs[1], 10) - 1;
        var second = parseInt(timeStrs[2], 10);
        date = new Date(year, month, day, hour, minute, second);
    } else {
        date = new Date(year, month, day);
    }
    return date;
}

DateUtils.compare = function(d1, d2) {
    return d1.getTime() - d2.getTime();
}

DateUtils.today = function() {
    return new Date();
}

DateUtils.todayStr = function() {
    return DateUtils.toString(new Date(), false);
}

DateUtils.toString = function(date, includeTime) {
    var seperator1 = "-";
    var year = date.getFullYear();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month < 10)
        month = "0" + month;
    if (strDate < 10)
        strDate = "0" + strDate;
    var currentdate = year + seperator1 + month + seperator1 + strDate;
    if (includeTime) {
        currentdate += " ";
        seperator1 = ":";
        var h = date.getHours();
        currentdate += (h < 10 ? "0" + h : h) + seperator1;
        var m = date.getMinutes();
        currentdate += (m < 10 ? "0" + m : m) + seperator1;
        var s = date.getSeconds();
        currentdate += (s < 10 ? "0" + s : s);
    }
    return currentdate;
}
// ////////////////////////////////////////////////////////////////////////////////////////////////
function getClipboard() {
    if (window.clipboardData) {
        return (window.clipboardData.getData('Text'));
    } else if (window.netscape) {
        netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
        var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
        if (!clip)
            return;
        var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
        if (!trans)
            return;
        trans.addDataFlavor('text/unicode');
        clip.getData(trans, clip.kGlobalClipboard);
        var str = new Object();
        var len = new Object();
        try {
            trans.getTransferData('text/unicode', str, len);
        } catch (error) {
            return null;
        }
        if (str) {
            if (Components.interfaces.nsISupportsWString)
                str = str.value.QueryInterface(Components.interfaces.nsISupportsWString);
            else if (Components.interfaces.nsISupportsString)
                str = str.value.QueryInterface(Components.interfaces.nsISupportsString);
            else
                str = null;
        }
        if (str) {
            return (str.data.substring(0, len.value / 2));
        }
    }
    return null;
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////
function copyToClipboard(txt) {
    if (window.clipboardData) {
        window.clipboardData.clearData();
        window.clipboardData.setData("Text", txt);
    } else if (navigator.userAgent.indexOf("Opera") != -1) {
        window.location = txt;
    } else if (window.netscape) {
        try {
            netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
        } catch (e) {
            alert("被浏览器拒绝！\n请在浏览器地址栏输入'about:config'并回车\n然后将'signed.applets.codebase_principal_support'设置为'true'");
        }
        var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
        if (!clip)
            return;
        var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
        if (!trans)
            return;
        trans.addDataFlavor('text/unicode');
        var str = new Object();
        var len = new Object();
        var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);
        var copytext = txt;
        str.data = copytext;
        trans.setTransferData("text/unicode", str, copytext.length * 2);
        var clipid = Components.interfaces.nsIClipboard;
        if (!clip)
            return false;
        clip.setData(trans, null, clipid.kGlobalClipboard);
    }
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////
function fixFlexArray(obj) {
    var result = [];
    for (var k in obj)
        result[k * 1] = obj[k];
    return result;
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////

// /////////////////////////////////////////////////////////////////////
// ----------------------------------------------------------
// 功能：根据身份证号获得出生日期
// 参数：身份证号 psidno
// 返回值：
// 出生日期
// ----------------------------------------------------------
function getBirthday(psidno) {
    var birthdayno, birthdaytemp
    if (psidno.length == 18) {
        birthdayno = psidno.substring(6, 14)
    } else if (psidno.length == 15) {
        birthdaytemp = psidno.substring(6, 12)
        birthdayno = "19" + birthdaytemp
    } else {
        return "";
    }
    var birthday = birthdayno.substring(0, 4) + "-" + birthdayno.substring(4, 6) + "-" + birthdayno.substring(6, 8)
    return birthday
}

// ----------------------------------------------------------
// 功能：根据身份证号获得性别
// 参数：身份证号 psidno
// 返回值：
// 性别
// ----------------------------------------------------------
function getSex(psidno) {
    var sexno, sex
    if (psidno.length == 18) {
        sexno = psidno.substring(16, 17)
    } else if (psidno.length == 15) {
        sexno = psidno.substring(14, 15)
    } else {
        return "";
    }
    var tempid = sexno % 2;
    if (tempid == 0) {
        sex = '女';
    } else {
        sex = '男';
    }
    return sex
}
// --------------------------------------------------------------
function isBrowseScale() {
    var ratio = 0, screen = window.screen, ua = navigator.userAgent.toLowerCase();

    if (isFirefor()) {
        if (window.devicePixelRatio !== undefined) {
            ratio = window.devicePixelRatio;
        }
    } else if (isIE()) {
        if (screen.deviceXDPI && screen.logicalXDPI) {
            ratio = screen.deviceXDPI / screen.logicalXDPI;
        }
    } else if (window.outerWidth !== undefined && window.innerWidth !== undefined) {
        ratio = window.outerWidth / window.innerWidth;
    }

    if (ratio) {
        ratio = Math.round(ratio * 100);
    }

    // 360安全浏览器下的innerWidth包含了侧边栏的宽度
    if (ratio !== 100) {
        if (ratio >= 98 && ratio <= 102) {
            ratio = 100;
        }
    }
    return ratio != 100;
};
// ///////////////////////////////////////////////////////////////////////////////////////////////////////
function openWindow(winname, content) {
    var win = window.open("", winname);
    var doc = win.document;
    doc.write(content);
    doc.close();
}
// ///////////////////////////////////////////////////////////////////////////////////////////////////////

function flashChecker() {
    var hasFlash = 0; // 是否安装了flash
    var flashVersion = 0; // flash版本
    if (isIE()) {
        var swf = new ActiveXObject('ShockwaveFlash.ShockwaveFlash');
        if (swf) {
            hasFlash = 1;
            VSwf = swf.GetVariable("$version");
            flashVersion = parseInt(VSwf.split(" ")[1].split(",")[0]);
        }
    } else {
        if (navigator.plugins && navigator.plugins.length > 0) {
            var swf = navigator.plugins["Shockwave Flash"];
            if (swf) {
                hasFlash = 1;
                var words = swf.description.split(" ");
                for (var i = 0; i < words.length; ++i) {
                    if (isNaN(parseInt(words[i])))
                        continue;
                    flashVersion = parseInt(words[i]);
                }
            }
        }
    }
    return {
        f : hasFlash,
        v : flashVersion
    };
}

// ---------------------------------------------------------------------------------
// 添加到收藏夹
function favorite_home() {
    var str = window.location.href;
    str = str.substr(0, str.lastIndexOf("/"));
    str = str.substr(0, str.lastIndexOf("/"));
    str = str + "/index.jsp";
    window.external.addFavorite(str, VAR_DOCUMENT_TITLE);
}
