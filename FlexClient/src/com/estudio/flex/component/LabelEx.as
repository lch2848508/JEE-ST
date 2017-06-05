package com.estudio.flex.component
{
    import com.estudio.flex.module.EditableControlParams;
    import com.estudio.flex.module.FormDataService;
    import com.estudio.flex.module.InterfaceEditableControl;
    import com.estudio.flex.utils.StringUtils;

    import spark.components.Label;

    public class LabelEx extends Label implements InterfaceEditableControl
    {
        public function LabelEx()
        {
            super ();
        }

        /////////////////////////////////////////////////////////////////////////////////////////////
        //实现接口 InterfceEditableControl
        private var controlParams:EditableControlParams = new EditableControlParams ();

        public function get databaseName():String
        {
            return controlParams.databaseName;
        }

        public function set databaseName(value:String):void
        {
            controlParams.databaseName = value;
        }

        public function get fieldName():String
        {
            return controlParams.fieldName;
        }

        public function set fieldName(value:String):void
        {
            controlParams.fieldName = value;
        }

        public function get extFieldName():String
        {
            return controlParams.extFieldName;
        }

        public function set extFieldName(value:String):void
        {
            controlParams.extFieldName = value;
        }

        public function get controlValue():String
        {
            return this.text;
        }

        public function get controlExtValue():String
        {
            return controlValue;
        }

        public function setControlValue(value:String , extValue:String , isSettingDataservice:Boolean):void
        {
            this.text = value;
            if (isSettingDataservice && isBindDatasource)
                controlParams.dataservice.setDataSetValue (controlParams.databaseName , controlParams.fieldName , value);
        }


        //是否只读
        public function get readonly():Boolean
        {
            return controlParams.readonly;
        }

        public function set readonly(value:Boolean):void
        {
            if (controlParams.readonly != value)
            {
                controlParams.readonly = value;
            }
        }

        public function get defaultReadonly():Boolean
        {
            return controlParams.defaultReadOnly;
        }

        public function set defaultReadonly(value:Boolean):void
        {
            controlParams.defaultReadOnly = value;
        }

        public function get controlType():int
        {
            return EditableControlParams.CONST_LABEL;
        }

        public function reset():void
        {
			if(this.formInstance)
            this.readonly = this.defaultReadonly || this.formInstance.readonly;
            //this.setControlValue("", "", false);
        }

        public function set dataservice(value:FormDataService):void
        {
            controlParams.dataservice = value;
        }

        public function get dataservice():FormDataService
        {
            return controlParams.dataservice;
        }

        public function setDataBindParams(formDataService:FormDataService , databaseName:String , fieldName:String , extFieldName:String):void
        {
			controlParams.formInstance = formDataService;
            controlParams.dataservice = formDataService;
            controlParams.databaseName = databaseName;
            controlParams.fieldName = fieldName;
            controlParams.extFieldName = extFieldName;
            controlParams.isBindDatasource = !(StringUtils.isEmpty (databaseName) && !StringUtils.isEmpty (fieldName));
        }

        public function get isBindDatasource():Boolean
        {
            return controlParams.isBindDatasource;
        }

        /////////////////////////////////////////////////////////////////////////////////////////////
    }
}
