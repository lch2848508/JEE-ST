function createIFrame_ByFlex(frameID, overflowAssignment) {
    var html = "<div id='" + frameID + "' style='position:absolute;z-index:1;background-color:#FFF; border:solid 0px #FF0066; display:none; overflow:" + overflowAssignment + "'></div>"
    $(document.body).append(html);
}

function moveIFrame_ByFlex(frameID, iframeID, x, y, w, h, objectID) {
    var s = $("#" + objectID)[0];
    $("#" + frameID).css({
                left : x +  'px',
                top : y + 'px',
                width : w + 'px',
                height : h + 'px'
            });    
    $("#" + iframeID).css({
        width : w + 'px',
        height : h + 'px'
    });    
}

function hideIFrame_ByFlex(frameID, iframeID) {
    $("#" + frameID).hide();
    $("#" + iframeID).hide();

}

function showIFrame_ByFlex(frameID, iframeID) {
    $("#" + frameID).show();
    $("#" + iframeID).show();
}
function showDiv_ByFlex(frameID, iframeID) {
    $("#" + frameID).show();
    $("#" + iframeID).show();
}

function loadIFrame_ByFlex(frameID, iframeID, url, embedID, scrollPolicy) {
    var html = "<iframe  id='" + iframeID + "' src='" + url + "'" + " name='" + iframeID + "'" + " onLoad=executePredefineHookfunction('" + iframeID + "')" + " scrolling='" + scrollPolicy
            + "' frameborder='0'></iframe>";
    $("#" + frameID).html(html);
}

function loadDivContent_ByFlex(frameID, iframeID, content) {
    var html = "<div id='" + iframeID + "' frameborder='0'>" + content + "</div>";
    $("#" + frameID).html(html);
}

function callIFrameFunction_ByFlex(iframeID, functionName, args) {
    var iframeRef = $("#" + iframeID)[0];// document.getElementById(iframeID);
    var iframeWin;
    if (iframeRef.contentWindow) {
        iframeWin = iframeRef.contentWindow;
    } else if (iframeRef.contentDocument) {
        iframeWin = iframeRef.contentDocument.window;
    } else if (iframeRef.window) {
        iframeWin = iframeRef.window;
    }
    if (iframeWin.wrappedJSObject != undefined) {
        iframeWin = iframeDoc.wrappedJSObject;
    }
    return iframeWin[functionName](args);
}

function hideDiv_ByFlex(frameID) {
    $("#" + frameID).hide();
}

function removeIFrame_ByFlex(frameID) {
    $("#" + frameID).remove();
}

function bringIFrameToFront_ByFlex(frameID) {
    var frameRef = $("#" + frameID)[0];// document.getElementById(frameID);
    if (oldFrame != frameRef) {
        if (oldFrame) {
            oldFrame.style.zIndex = "99";
        }
        frameRef.style.zIndex = "100";
        oldFrame = frameRef;
    }
}

function askForEmbedObjectId_ByFlex(randomString) {
    try {
        var embeds = document.getElementsByTagName('embed');
        for (var i = 0; i < embeds.length; i++) {
            var isTheGoodOne = embeds[i].checkObjectId(embeds[i].getAttribute('id'), randomString);
            if (isTheGoodOne) { return embeds[i].getAttribute('id'); }
        }
        var objects = document.getElementsByTagName('object');
        for (i = 0; i < objects.length; i++) {
            var isTheGoodOne = objects[i].checkObjectId(objects[i].getAttribute('id'), randomString);
            if (isTheGoodOne) { return objects[i].getAttribute('id'); }
        }
    } catch (e) {
    }
    return null;
}

function getBrowserMeasuredWidth_ByFlex(objectID) {
    return document.getElementById(objectID).offsetWidth;
}

function printIFrame_ByFlex(iframeID) {
    try {
        if (navigator.appName.indexOf('Microsoft') != -1) {
            document[iframeID].focus();
            document[iframeID].print();
        } else {
            for (var i = 0; i < window.frames.length; i++) {
                if (window.frames[i].name == iframeID) {
                    window.frames[i].focus();
                    window.frames[i].print();
                }
            }
        }
    } catch (e) {
        alert(e.name + ': ' + e.message);
    }
}

function historyBack_ByFlex(iframeID) {
    frames[iframeID].history.go(-1);
}

function historyForward_ByFlex(iframeID) {
    frames[iframeID].history.go(1);
}

var flexIFrameList = [];
function setupResizeEventListener_ByFlex(iframeID) {
    flexIFrameList.push(iframeID);
}

$(window).resize(function() {
            window.setTimeout(notifyWindowResize4IFrame, 10);
        });

function notifyWindowResize4IFrame() {
    for (i = 0; i < flexIFrameList.length; i++) {
        var iframeID = flexIFrameList[i];
        var obj = isIE() ? window["MainForm"] : document["MainFormEx"];;
        obj[iframeID + "_resize"]();
    }
}

