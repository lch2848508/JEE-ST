// -------------------------------------------------------------------------------------------------------
var MESSAGE_ABLE = true;
var MESSAGE_URL = "../client/message?o=connection";
var QUIT_MESSAGE_URL = "../client/message?o=close";
// -------------------------------------------------------------------------------------------------------
var comet = {
    connection : false,
    iframediv : false,

    initialize : function() {
        if (navigator.appVersion.indexOf("MSIE") != -1) {
            comet.connection = new ActiveXObject("htmlfile");
            comet.connection.open();
            comet.connection.write("<html>");
            comet.connection.write("<script>document.domain = '" + document.domain + "'");
            comet.connection.write("</html>");
            comet.connection.close();
            comet.iframediv = comet.connection.createElement("div");
            comet.connection.appendChild(comet.iframediv);
            comet.connection.parentWindow.comet = comet;
            comet.iframediv.innerHTML = "<iframe id='comet_iframe' src='" + MESSAGE_URL + "'></iframe>";
        } else if (navigator.appVersion.indexOf("KHTML") != -1 || navigator.userAgent.indexOf('Opera') >= 0) {
            comet.connection = document.createElement('iframe');
            comet.connection.setAttribute('id', 'comet_iframe');
            comet.connection.setAttribute('src', MESSAGE_URL);
            with (comet.connection.style) {
                position = "absolute";
                left = top = "-100px";
                height = width = "1px";
                visibility = "hidden";
            }
            document.body.appendChild(comet.connection);
        } else {
            comet.connection = document.createElement('iframe');
            comet.connection.setAttribute('id', 'comet_iframe');
            with (comet.connection.style) {
                left = top = "-100px";
                height = width = "1px";
                visibility = "hidden";
                display = 'none';
            }
            comet.iframediv = document.createElement('iframe');
            comet.iframediv.setAttribute('onLoad', 'comet.frameDisconnected()');
            comet.iframediv.setAttribute('src', MESSAGE_URL);
            comet.connection.appendChild(comet.iframediv);
            document.body.appendChild(comet.connection);
        }
    },
    frameDisconnected : function() {
        comet.connection = false;
        $('#comet_iframe').remove();
        // setTimeout("chat.showConnect();",100);
    },
    processContent : function(data) {
        processMessage(data);
    },
    timeout : function() {
        var url = MESSAGE_URL + "&time=" + new Date().getTime();
        if (navigator.appVersion.indexOf("MSIE") != -1) {
            comet.iframediv.childNodes[0].src = url;
        } else if (navigator.appVersion.indexOf("KHTML") != -1 || navigator.userAgent.indexOf('Opera') >= 0) {
            document.getElementById("comet_iframe").src = url;
        } else {
            comet.connection.removeChild(comet.iframediv);
            document.body.removeChild(comet.connection);
            comet.iframediv.setAttribute('src', MESSAGE_URL);
            comet.connection.appendChild(comet.iframediv);
            document.body.appendChild(comet.connection);
        }
    },
    onUnload : function() {
        if (comet.connection) {
            comet.connection = false;
            $.get(QUIT_MESSAGE_URL);
        }
    }
};
// -------------------------------------------------------------------------------------------------------
$(window).load(function() {
            // if (MESSAGE_ABLE)
            // comet.initialize();
        });
// -------------------------------------------------------------------------------------------------------
$(window).unload(function() {
            // if (MESSAGE_ABLE)
            // comet.onUnload();
        });
// -------------------------------------------------------------------------------------------------------
// 处理消息
function processMessage(msg) {
    MainFormFlash.Callback_popupMessage(__JS_OBJECT_2_FLEX_OBJECT__(msg));
}
//
// -------------------------------------------------------------------------------------------------------
/*
 * json = new JSONObject(); json.put("time", timestamp); json.put("type", type ==
 * MessageItemType.STRING ? 0 : 1); json.put("sendId", sendUserId);
 * json.put("sendName", sendUserName); json.put("content", content);
 */
