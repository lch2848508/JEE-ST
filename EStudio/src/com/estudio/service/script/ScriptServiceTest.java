package com.estudio.service.script;

public class ScriptServiceTest {

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        ScriptService.getInstance().eval("StringUtils.Test(null)");
        ScriptService.getInstance().eval("StringUtils.Test(1)");
        ScriptService.getInstance().eval("StringUtils.Test(\"123\")");
        ScriptService.getInstance().eval("StringUtils.Test(new Date())");
        ScriptService.getInstance().eval("StringUtils.Test([])");
        ScriptService.getInstance().eval("StringUtils.Test({a:1,b:2})");

    }

}
