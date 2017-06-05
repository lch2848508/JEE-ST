// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
function checkControlValue(params) {

    var controlValue = params[0];
    var validParams = params[1];
    var controlAlias = params[2]["Alias"];

    // 检查必填项
    if (validParams.Require == "True" && controlValue == "") return {
        r : false,
        msg : validParams.ValidatorMsg == "" ? controlAlias + "值不能为空!" : validParams.ValidatorMsg
    };

    // 检查数据格式
    if (validParams.DataFormat != "" && controlValue != "") {
        var test = true;
        switch (validParams.DataFormat) {
            case "INT" :
            case "INT1" :
            case "INT2" :
            case "FLOAT" :
            case "FLOAT1" :
            case "FLOAT2" :
            case "EMAIL" :
            case "URL" :
            case "ZIPCODE" :
            case "MOBILE" :
            case "QQ" :
            case "TEL" :
                test = (new RegExp(regexEnum[validParams.DataFormat])).test(controlValue);
                break;
            case "IDCAED" :
                test = isCardID(controlValue);
                break;
            case "DATE" :
                test = isDate(controlValue);
                break;
            case "DATETIME" :
                test = isDateTime(controlValue);
                break;
            default : // 正则表达式
                test = (new RegExp(validParams.DataFormat)).test(controlValue);
                break;
        }
        if (!test) return {
            r : false,
            msg : validParams.ValidatorMsg == "" ? controlAlias + "格式不正确!" : validParams.ValidatorMsg
        };
    }

    // 检查长度 最小长度
    if (controlValue != "" && validParams.MinLength * 1 != 0 && validParams.MinLength * 1 > controlValue.length) return {
        r : false,
        msg : validParams.ValidatorMsg == "" ? controlAlias + "至少需要 " + validParams.MinLength + " 个字符" : validParams.ValidatorMsg
    };

    // 检查长度 最大长度
    if (controlValue != "" && validParams.MaxLength * 1 != 0 && validParams.MaxLength * 1 < controlValue.length) return {
        r : false,
        msg : validParams.ValidatorMsg == "" ? controlAlias + "最多只能输入 " + validParams.MaxLength + " 个字符" : validParams.ValidatorMsg
    };

    // 检查值范围 最小值
    if (controlValue != "" && validParams.MinValue) {
        if (isDate(controlValue) && isDate(validParams.MinValue)) { // 检查日期
            var r = controlValue.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
            var vd = new Date(r[1], r[3] - 1, r[4]);
            r = validParams.MinValue.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
            mind = new Date(r[1], r[3] - 1, r[4]);
            if (vd < mind) return {
                r : false,
                msg : validParams.ValidatorMsg == "" ? controlAlias + "必须大于 " + validParams.MinValue : validParams.ValidatorMsg
            };
        } else if (isNumber(controlValue) && isNumber(validParams.MinValue)) { // 检查数字
            var vd = controlValue * 1;
            mind = validParams.MinValue * 1;
            if (vd < mind) return {
                r : false,
                msg : validParams.ValidatorMsg == "" ? controlAlias + "必须大于 " + validParams.MinValue : validParams.ValidatorMsg
            };
        }
    }

    // 检查值范围 最大值
    if (controlValue != "" && validParams.MaxValue) {
        if (isDate(controlValue) && isDate(validParams.MaxValue)) { // 检查日期
            var r = controlValue.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
            var vd = new Date(r[1], r[3] - 1, r[4]);
            r = validParams.MaxValue.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
            maxd = new Date(r[1], r[3] - 1, r[4]);
            if (vd > maxd) return {
                r : false,
                msg : validParams.ValidatorMsg == "" ? controlAlias + "必须小于 " + validParams.MaxValue : validParams.ValidatorMsg
            };
        } else if (isNumber(controlValue) && isNumber(validParams.MaxValue)) { // 检查数字
            var vd = controlValue * 1;
            maxd = validParams.MaxValue * 1;
            if (vd > maxd) return {
                r : false,
                msg : validParams.ValidatorMsg == "" ? controlAlias + "必须小于 " + validParams.MaxValue : validParams.ValidatorMsg
            };
        }
    }

    if (validParams.Function && window[validParams.Function] && !window[validParams.Function]()) return {
        r : false,
        msg : validParams.ValidatorMsg
    };

    return {
        r : true,
        msg : ""
    };
}

// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
var regexEnum = {
    INT : "^-?[0-9]\\d*$", // 整数
    INT1 : "^[0-9]\\d*$", // 正整数
    INT2 : "^-[0-9]\\d*$", // 负整数
    NUMBER : "^([+-]?)\\d*\\.?\\d+$", // 数字
    NUMBER1 : "^[1-9]\\d*|0$", // 正数（正整数 + 0）
    NUMBER2 : "^-[1-9]\\d*|0$", // 负数（负整数 + 0）
    FLOAT : "^([+-]?)\\d*\\.\\d+$", // 浮点数
    decmal1 : "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$",// 正浮点数
    decmal2 : "^-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*)$",// 负浮点数
    decmal3 : "^-?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$",// 浮点数
    FLOAT1 : "^[1-9]\\d*.?\\d*|0.\\d*[1-9]\\d*|0?.0+|0$",// 非负浮点数（正浮点数 + 0）
                                                            // 修正匹配
    // 整数
    FLOAT2 : "^(-([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*))|0?.0+|0$",// 非正浮点数（负浮点数 +
                                                                // 0）

    EMAIL : "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$", // 邮件
    color : "^[a-fA-F0-9]{6}$", // 颜色
    URL : "^http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+([\\w-./?%&=]*)?$", // url
    chinese : "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$", // 仅中文
    ascii : "^[\\x00-\\xFF]+$", // 仅ACSII字符
    ZIPCODE : "^\\d{6}$", // 邮编
    MOBILE : "^(13|15)[0-9]{9}$", // 手机
    ip4 : "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$", // ip地址
    notempty : "^\\S+$", // 非空
    picture : "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$", // 图片
    rar : "(.*)\\.(rar|zip|7zip|tgz)$", // 压缩文件
    DATE : "^\\d{4}(\\-|\\/|\.)\\d{1,2}\\1\\d{1,2}$", // 日期
    QQ : "^[1-9]*[1-9][0-9]*$", // QQ号码
    TEL : "^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$", // 电话号码的函数(包括验证国内区号,国际区号,分机号)
    username : "^\\w+$", // 用来用户注册。匹配由数字、26个英文字母或者下划线组成的字符串
    letter : "^[A-Za-z]+$", // 字母
    letter_u : "^[A-Z]+$", // 大写字母
    letter_l : "^[a-z]+$", // 小写字母
    IDCARD : "^[1-9]([0-9]{14}|[0-9]{17})$" // 身份证
}

var aCity = {
    11 : "北京",
    12 : "天津",
    13 : "河北",
    14 : "山西",
    15 : "内蒙古",
    21 : "辽宁",
    22 : "吉林",
    23 : "黑龙江",
    31 : "上海",
    32 : "江苏",
    33 : "浙江",
    34 : "安徽",
    35 : "福建",
    36 : "江西",
    37 : "山东",
    41 : "河南",
    42 : "湖北",
    43 : "湖南",
    44 : "广东",
    45 : "广西",
    46 : "海南",
    50 : "重庆",
    51 : "四川",
    52 : "贵州",
    53 : "云南",
    54 : "西藏",
    61 : "陕西",
    62 : "甘肃",
    63 : "青海",
    64 : "宁夏",
    65 : "新疆",
    71 : "台湾",
    81 : "香港",
    82 : "澳门",
    91 : "国外"
}

function isCardID(sId) {
    var iSum = 0;
    var info = "";
    if (!/^\d{17}(\d|x)$/i.test(sId)) return false;
    sId = sId.replace(/x$/i, "a");
    if (aCity[parseInt(sId.substr(0, 2))] == null) return false;
    sBirthday = sId.substr(6, 4) + "-" + Number(sId.substr(10, 2)) + "-" + Number(sId.substr(12, 2));
    var d = new Date(sBirthday.replace(/-/g, "/"));
    if (sBirthday != (d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate())) return false;
    for (var i = 17; i >= 0; i--)
        iSum += (Math.pow(2, i) % 11) * parseInt(sId.charAt(17 - i), 11);
    if (iSum % 11 != 1) return false;
    return true; // aCity[parseInt(sId.substr(0,2))]+","+sBirthday+","+(sId.substr(16,1)%2?"男":"女")
}

function isNumber(str) {
    return (new RegExp(regexEnum["NUMBER"])).test(str);
}

// 短时间，形如 (13:04:06)
function isTime(str) {
    var a = str.match(/^(\d{1,2})(:)?(\d{1,2})\2(\d{1,2})$/);
    if (a == null) { return false; }
    if (a[1] > 24 || a[3] > 60 || a[4] > 60) { return false; }
    return true;
}

// 短日期，形如 (2003-12-05)
function isDate(str) {
    var r = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
    if (r == null) return false;
    var d = new Date(r[1], r[3] - 1, r[4]);
    return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4]);
}

// 长时间，形如 (2003-12-05 13:04:06)
function isDateTime(str) {
    var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/;
    var r = str.match(reg);
    if (r == null) return false;
    var d = new Date(r[1], r[3] - 1, r[4], r[5], r[6], r[7]);
    return (d.getFullYear() == r[1] && (d.getMonth() + 1) == r[3] && d.getDate() == r[4] && d.getHours() == r[5] && d.getMinutes() == r[6] && d.getSeconds() == r[7]);
}