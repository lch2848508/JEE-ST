package com.estudio.flex
{
    import com.estudio.flex.utils.JSFunUtils;

    public final class RUNTIME_GLOBAL
    {
        public function RUNTIME_GLOBAL()
        {
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        //获取服务器唯一表示号
        public static function getServerUniqueID():String
        {
            return String (JSFunUtils.JSFun ("getServerUniqueid" , null));
        }
    }
}
