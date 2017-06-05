package com.estudio.utils;

import java.io.StringReader;
import java.io.StringWriter;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

public class JSCompress {

    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    private JSCompress() {

    }

    private class CompressErrorReporter implements ErrorReporter {
        @Override
        public void warning(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
        }

        @Override
        public void error(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
        }

        @Override
        public EvaluatorException runtimeError(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
            return new EvaluatorException(message);
        }
    }

    private final CompressErrorReporter compressErrorReporter = new CompressErrorReporter();

    /**
     * Ñ¹ËõJavaScript
     * 
     * @param inputStr
     * @return
     */
    public String compress(final String js) {
        String inputStr = js;
        if (enabled)
            try {
                final JavaScriptCompressor compress = new JavaScriptCompressor(new StringReader(inputStr), compressErrorReporter);
                final StringWriter strWriter = new StringWriter();
                compress.compress(strWriter, 4096, true, true, true, false);
                inputStr = strWriter.toString();
            } catch (final Exception e) {

            }
        return inputStr;
    }

    private static final JSCompress INSTANCE = new JSCompress();

    public static JSCompress getInstance() {
        return INSTANCE;
    }
}
