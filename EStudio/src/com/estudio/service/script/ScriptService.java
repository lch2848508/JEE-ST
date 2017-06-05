package com.estudio.service.script;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.estudio.utils.Convert;

public final class ScriptService {

    private ScriptEngine jsEngine = null;

    // 初始化脚本引擎
    private void initScript() {
        jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");

        // 字符串处理函数
        jsEngine.getBindings(ScriptContext.GLOBAL_SCOPE).put("StringUtils", StringUtilsHelper.instance);
        jsEngine.getBindings(ScriptContext.GLOBAL_SCOPE).put("Convert", Convert.instance);
    }

    /**
     * 执行脚本
     * 
     * @param script
     * @return
     * @throws Exception
     */
    public Object eval(final String script) throws Exception {
        return jsEngine.eval(script);
    }

    /**
     * 执行脚本
     * 
     * @param script
     * @param content
     * @return
     * @throws Exception
     */
    public Object eval(final String script, final Map<String, Object> content) throws Exception {
        final Bindings bind = jsEngine.createBindings();
        if (content != null)
            for (final Map.Entry<String, Object> entry : content.entrySet())
                bind.put(entry.getKey(), entry.getValue());
        return jsEngine.eval(script, bind);
    }

    /**
     * 构造函数
     */
    private ScriptService() {
        initScript();
    }

    private static final ScriptService INSTANCE = new ScriptService();

    /**
     * 全局脚本服务
     * 
     * @return
     */
    public static ScriptService getInstance() {
        return INSTANCE;
    }
}
