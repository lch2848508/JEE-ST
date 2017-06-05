import com.estudio.flex.module.component.PortalGridExControl;
import com.estudio.flex.module.component.PortalGridExGrid;
import com.estudio.flex.module.component.PortalGridExTree;
import com.estudio.flex.utils.StringUtils;

// ActionScript file
private function fullDataToControls(parentControlName:String, data:Object):void
{
	var controlNames:Array=this.portalControlNames;
	if (!StringUtils.isEmpty(parentControlName))
		controlNames=this.portalControlName2ControlInstance[portalControlNames].childControls;
	for (var i:int=0; i < controlNames.length; i++)
	{
		var control:PortalGridExControl=portalControlName2ControlInstance[controlNames[i]];
		if (control == null)
			continue;
		if (control is PortalGridExGrid)
			PortalGridExGrid(control).initData(data[controlNames[i]]);
		else if (control is PortalGridExTree)
			PortalGridExTree(control).initData(data[controlNames[i]]);
	}
	initFormControlReqParams();
}
