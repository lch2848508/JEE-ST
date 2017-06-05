package com.estudio.flex
{

    public class RUNTIME_COMMAND
    {
        public function RUNTIME_COMMAND()
        {
        }
        //----------------------------------------------------------------------------
        //工作流命令定义
        public static var COMMAND_WORKFLOW_NEW_CASE:String = "New";
        public static var COMMAND_WORKFLOW_EDIT_FORM:String = "EditForm";
        public static var COMMAND_WORKFLOW_VIEW_FORM:String = "ViewForm";
        public static var COMMAND_WORKFLOW_DELETE_CASE:String = "Delete";
        public static var COMMAND_WORKFLOW_BACK_CASE:String = "Back";
		public static var COMMAND_WORKFLOW_SEND_CASE:String = "Send";
		public static var COMMAND_WORKFLOW_SEND_CASE_SPECIAL:String = "SendSpecial";		
		public static var COMMAND_WORKFLOW_BACK_TO_CREATOR:String = "BackToFirstStep";		
        public static var COMMAND_WORKFLOW_VIEW_IDEA:String = "ViewIdea";
        public static var COMMAND_WORKFLOW_VIEW_DIAGRAM:String = "ViewDiagram";
        public static var COMMAND_WORKFLOW_VIEW_FILTER_PANEL:String = "ViewFilterPanel";
        public static var COMMAND_WORKFLOW_SIGN_ITEM:String = "SignWorkFlowItem";
        public static var COMMAND_WORKFLOW_REFRESH:String = "RefreshCaseList";
		public static var COMMAND_WORKFLOW_EDIT_MESSAGE:String = "EditMessage";
		public static var COMMAND_WORKFLOW_VIEW_MESSAGE:String = "ViewMessage";

    }
}
