package com.estudio.tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.estudio.utils.JSCompress;

public class ReleaseTools {

    public static String getRootDirectory() {
        final String result = ReleaseTools.class.getResource("/").getPath();
        return result.substring(1).replace("WEB-INF/classes/", "");
    }

    public static void main(final String[] args) throws IOException {
        compressJavaScript();
    }

    private static void compressJavaScript() throws IOException {
        final Map<String, String[]> jsList = new HashMap<String, String[]>();
        jsList.put("js/release/jquery.min.js", new String[] { "js/jslib/jquery/jquery.js", "js/jslib/jquery/jquery.cookie.js", "js/jslib/jquery/jquery.json.js" });
        jsList.put("js/release/flexclient.index.min.js", new String[] { "js/jslib/utils.js", "js/javascript/flexclient/index.js", "js/javascript/flexclient/intf_global.js", "js/javascript/flexclient/intf_grid.js", "js/javascript/flexclient/intf_gridex.js", "js/javascript/flexclient/intf_form.js", "js/javascript/flexclient/intf_form_extend.js", "js/javascript/flexclient/intf_workflow.js",
                "js/javascript/flexclient/intf_message.js", "js/javascript/flexclient/intf_third_interface.js", "js/javascript/flexclient/extensionswf.js", "js/javascript/flexclient/intf_iframe4flex.js", "js/javascript/flexclient/intf_workflow.js", "js/javascript/flexclient/intf_query.js" });
        jsList.put("js/release/modal.form.min.js", new String[] { "js/jslib/utils.js", "js/javascript/client/formui_api.js", });
        jsList.put("js/release/modal.portal.min.js", new String[] { "js/jslib/utils.js", "js/javascript/client/showgrid_api.js" });
        jsList.put("js/release/modal.portalex.min.js", new String[] { "js/jslib/utils.js", "js/javascript/client/showgridex_api.js" });
        jsList.put("js/release/modal.query.min.js", new String[] { "js/jslib/utils.js", "js/javascript/client/query_api.js" });

        final String rootDir = getRootDirectory();
        for (final Map.Entry<String, String[]> entry : jsList.entrySet()) {
            System.out.println("process:" + entry.getKey());
            final StringBuilder sb = new StringBuilder();
            for (final String fileName : entry.getValue()) {
                sb.append(FileUtils.readFileToString(new File(rootDir + fileName), "utf-8"));
                sb.append("\n");
            }
            FileUtils.writeStringToFile(new File(rootDir + entry.getKey()), JSCompress.getInstance().compress(sb.toString()), "utf-8");
        }
        System.out.println("OK!");
    }
}
