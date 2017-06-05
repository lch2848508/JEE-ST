// ///////////////////////////////////////////////////////////////////////////////////////////
// 身份证验证接口
var IDCARD = {
    isInitialized : false,
    isFindPort : false,
    isIE : (!!window.ActiveXObject || "ActiveXObject" in window)
};
// 初始化身份证读卡器
IDCARD.init = function() {
    if (!IDCARD.isIE) {
        GLOBAL.popupMessage("身份证读卡器", "身份证读卡器只能在IE浏览器下运行。", true);
        return;
    }

    if (!IDCARD.isInitialized) {
        var html = '<object classid="clsid:46E4B248-8A41-45C5-B896-738ED44C1587" id="SynCardOcx" width="0" height="0" ></object>';
        $(document.body).append(html);
        IDCARD.isInitialized = true;
    }
}

// 读取身份证
IDCARD.read = function(callfun) {
    var str = SynCardOcx.FindReader() * 1;
    if (str <= 0) {
        GLOBAL.popupMessage("身份证读卡器", "请正确连接身份证读卡器。", true);
        return;
    }

    var nRet = SynCardOcx.ReadCardMsg();
    if (nRet == 0) {
        var cardInfo = {};
        cardInfo.name = SynCardOcx.NameA;
        cardInfo.sex = SynCardOcx.Sex;
        cardInfo.nation = SynCardOcx.Nation;
        cardInfo.born = SynCardOcx.Born;
        cardInfo.address = SynCardOcx.Address;
        cardInfo.cardNo = SynCardOcx.CardNo;
        cardInfo.userLifeB = SynCardOcx.UserLifeB;
        cardInfo.userLifeE = SynCardOcx.UserLifeE;
        cardInfo.police = SynCardOcx.Police;
        cardInfo.photo = SynCardOcx.PhotoName;
        if (callfun)
            callfun(cardInfo);
    } else {
        GLOBAL.popupMessage("身份证读卡器", "读取身份证信息出现错误，代码：" + nRet, true);
        return;
    }
}
