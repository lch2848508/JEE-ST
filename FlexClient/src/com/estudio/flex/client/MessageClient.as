import com.estudio.flex.component.MSNPopupWindow;
import com.estudio.flex.utils.ObjectUtils;
import com.estudio.flex.utils.StringUtils;

import flash.display.DisplayObject;
import flash.events.DataEvent;
import flash.events.ErrorEvent;
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.MouseEvent;
import flash.system.Security;
import flash.utils.clearTimeout;
import flash.utils.setTimeout;

import mx.core.FlexGlobals;
import mx.effects.Move;
import mx.events.EffectEvent;
import mx.managers.PopUpManager;

private var CONST_MESSAGE_TYPE_STRING:int = 0;

//----------------------------------------------------------------------------------------
//将消息转化为Message
private function transMessageToHTML(data:Object):String
{
    var contentColor:String = data.isError ? "#FF0000" : "#0000FF";
    var message:String = "";
    var type:int = data.type;
    if (type == CONST_MESSAGE_TYPE_STRING) // 字符串信息
        message = "<P><FONT COLOR=\"#000000\"><B>" + data.time + " " + StringUtils.str2HTML (data.sendName) + " 发送:</B></FONT></P><P><FONT COLOR=\"" + contentColor + "\">" + StringUtils.str2HTML (data.content) + "</FONT></P>";

    return message;
}

//----------------------------------------------------------------------------------------
//弹出消息
public function popupMessage(data:Object):void
{
    data = ObjectUtils.unescape4flex (data);
    var message:String = transMessageToHTML (data);
    if (StringUtils.isEmpty (message))
        return;

    _msnWin.addMessage (message);
    if (!_MSN_WINDOW_IS_VISIBLE)
    {
        imgMsnWindowStatus_clickHandler (null);
    }
    else
    {
        if (MSN_TIMER_HANDLE != 0)
        {
            clearTimeout (MSN_TIMER_HANDLE);
            MSN_TIMER_HANDLE = 0;
        }
        MSN_TIMER_HANDLE = setTimeout (MSN_TIMER_HANDLE_FUN , 120 * 1000);
    }
}
//-----------------------------------------------------------------------------------------
private var _MSN_WINDOW_IS_VISIBLE:Boolean = false;
private var _msnWin:MSNPopupWindow = new MSNPopupWindow ();
private var _showEffect:Move = new Move ();

//-----------------------------------------------------------------------------------------
protected function imgMsnWindowStatus_clickHandler(event:MouseEvent):void
{

    var p:DisplayObject = FlexGlobals.topLevelApplication as DisplayObject;
    _MSN_WINDOW_IS_VISIBLE = !_MSN_WINDOW_IS_VISIBLE;
    //imgMsnWindowStatus.source = _MSN_WINDOW_IS_VISIBLE ? "../images/flex/show_msn.png" : "../images/flex/hide_msn.png";
    if (_MSN_WINDOW_IS_VISIBLE)
    {
        _msnWin.x = p.width - _msnWin.width;
        _msnWin.y = p.height; //-_msnWin.height;
        FlexGlobals.topLevelApplication.showPopupWindow (_msnWin);
        //_msnWin.ShowEffect();
        _msnWin.isPopUp = false;

        if (MSN_TIMER_HANDLE != 0)
        {
            clearTimeout (MSN_TIMER_HANDLE);
            MSN_TIMER_HANDLE = 0;
        }
        MSN_TIMER_HANDLE = setTimeout (MSN_TIMER_HANDLE_FUN , 120 * 1000);

        _showEffect.end ();
        _showEffect.yFrom = p.height;
        _showEffect.yTo = p.height - _msnWin.height;
        _showEffect.target = _msnWin;
        _showEffect.play ();
    }
    else
    {
        FlexGlobals.topLevelApplication.closePopupWindow (_msnWin);
    }
}
//---------------------------------------------------------------------------------------
private var MSN_TIMER_HANDLE:Number = 0;

private function MSN_TIMER_HANDLE_FUN():void
{
    if (_MSN_WINDOW_IS_VISIBLE)
        imgMsnWindowStatus_clickHandler (null);
    MSN_TIMER_HANDLE = 0;
}
//---------------------------------------------------------------------------------------


