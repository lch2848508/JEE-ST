package com.estudio.web.servlet.external;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.minidev.json.JSONObject;

import com.estudio.context.RuntimeContext;
import com.estudio.define.ResultInfo;
import com.estudio.define.sercure.ClientLoginInfo;
import com.estudio.web.servlet.BaseServlet;

public class DciDataManageServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3065418457160492653L;

	@Override
	protected void doRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		final JSONObject json = new ResultInfo().toJSON();
		String o=request.getParameter("o");
		if("saveservices".equals(o)){
			String msg=saveServices(request,response);
			if("true".equals(msg)){
				json.put("r", "true");
				response.getWriter().println(json);
			}else{
				json.put("r", "false");
				json.put("msg", msg);
				response.getWriter().println(json);
			}
		}else{
			json.put("r", "false");
			json.put("msg", "操作（o）方法错误！");
			response.getWriter().println(json);
		}
	}
	
	private String saveServices(final HttpServletRequest request,final HttpServletResponse response) throws Exception{
		String isok="";
		String user=request.getParameter("u");
		String pwd=request.getParameter("p");
		final String userType = request.getParameter("userType");
        final String userArea = request.getParameter("userArea");
        final String orgId = request.getParameter("orgId");
        DciDataManage dcidatamange=new DciDataManage();
        
		final ClientLoginInfo loginInfo = RuntimeContext.getClientLoginService().login(user, pwd, userType, userArea, orgId, request.getSession().getId());
		if(loginInfo!=null){
			String serviceName=request.getParameter("name")!=null?request.getParameter("name"):"地图服务";
			String serviceType=request.getParameter("type")!=null?request.getParameter("type"):"ArcGISDynamicMapService";
			String serviceUrl=request.getParameter("url")!=null?request.getParameter("url"):"";
			isok=dcidatamange.saveServices(serviceName, serviceType, serviceUrl);
		}else{
			isok="用户名或密码错误！";
		}
		return isok;
	}
	
	/**
	 * Constructor of the object.
	 */
	public DciDataManageServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
