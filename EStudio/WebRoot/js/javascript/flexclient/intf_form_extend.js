// ------------------------------------------------------------------------------
// 返回身份证出生年月,返回“1980-01-01”
function getIDCardDate(idcard) {
    var shengri;
    if (idcard.length == 15) {
        var nian, yue, ri;
        nian = idcard.substr(6, 2);
        yue = idcard.substr(8, 2);
        ri = idcard.substr(10, 2);
        shengri = "19" + nian + "-" + yue + "-" + ri;
    } else if (idcard.length == 18) {
        var nian, yue, ri;
        nian = idcard.substr(6, 4);
        yue = idcard.substr(10, 2);
        ri = idcard.substr(12, 2);
        shengri = nian + "-" + yue + "-" + ri;
    }
    return shengri;
}
// ------------------------------------------------------------------------------
// 返回身份证性别,返回“男”或“女”
function getIDCardGendar(idcard) {
    var sex;
    if (idcard.length == 15) {
        sex = (idcard.substr(14, 1) % 2) == 1 ? '男' : '女';
    } else if (idcard.length == 18) {
        sex = (idcard.substr(16, 1) % 2) == 1 ? '男' : '女';
    }
    return sex;
}
// ------------------------------------------------------------------------------
// 返回身份证周年年龄
function getIDCardAge(idcard) {
    var returnAge;
    var strBirthdayArr = getIDCardDate(idcard).split("-");
    var birthYear = strBirthdayArr[0];
    var birthMonth = strBirthdayArr[1];
    var birthDay = strBirthdayArr[2];

    var d = new Date();
    var nowYear = d.getYear();
    var nowMonth = d.getMonth() + 1;
    var nowDay = d.getDate();

    if (nowYear == birthYear) {
        returnAge = 0;// 同年 则为0岁
    } else {
        var ageDiff = nowYear - birthYear; // 年之差
        if (ageDiff > 0) {
            if (nowMonth == birthMonth) {
                var dayDiff = nowDay - birthDay;// 日之差
                if (dayDiff < 0) {
                    returnAge = ageDiff - 1;
                } else {
                    returnAge = ageDiff;
                }
            } else {
                var monthDiff = nowMonth - birthMonth;// 月之差
                if (monthDiff < 0) {
                    returnAge = ageDiff - 1;
                } else {
                    returnAge = ageDiff;
                }
            }
        } else {
            returnAge = -1;// 返回-1 表示出生日期输入错误 晚于今天
        }
    }
    return returnAge;// 返回周岁年龄
}
// ///////////////////////////////////////////////////////////////////////////////////////////
// validIdCard
function validIdCard(code) {
    if (!code || !(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(code))) { return false; }
    return true;
}
// ///////////////////////////////////////////////////////////////////////////////////////////
// 日期对比,返回false,即开始日期大于结束日期
function compare_time(startDateStr, endDateStr) {
    var arr = startDateStr.split("-");
    var startDate = new Date(arr[0], arr[1], arr[2]);
    var startTime = startDate.getTime();

    arr = endDateStr.split("-");
    var endDate = new Date(arr[0], arr[1], arr[2]);
    var endTime = endDate.getTime();

    if (startTime * 1 > endTime * 1) {
        return false;
    } else {
        return true;
    }
}
// ///////////////////////////////////////////////////////////////////////////////////////////
