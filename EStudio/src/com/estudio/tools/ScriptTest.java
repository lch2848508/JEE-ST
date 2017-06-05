package com.estudio.tools;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptTest {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String jsFun = "function test(a,b){return a+b;} test(x,y);";
        StartCommonEval(jsFun);
        StartComplerEval(jsFun);
        startNativEval();
    }

    private static void startNativEval() {
        long startTime = System.currentTimeMillis();
        int a = 100;
        int b = 200;
        for (int i = 0; i < 1000; i++) {
            int c = a + b;
        }
        System.out.println("Total Time:" + (System.currentTimeMillis() - startTime));
    }

    private static void StartComplerEval(String jsFun) throws Exception {
        ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
        final Bindings bind = jsEngine.createBindings();
        bind.put("x", 1);
        bind.put("y", 2);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Object o = jsEngine.eval(jsFun, bind);
        }
        System.out.println("Total Time:" + (System.currentTimeMillis() - startTime));
    }

    private static void StartCommonEval(String jsFun) throws Exception {
        ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("JavaScript");
        Compilable compEngine = (Compilable) jsEngine;
        CompiledScript script = compEngine.compile(jsFun);
        final Bindings bind = jsEngine.createBindings();
        bind.put("x", 1);
        bind.put("y", 2);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Object o = script.eval(bind);
        }
        System.out.println("Total Time:" + (System.currentTimeMillis() - startTime));

    }

}
