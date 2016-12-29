package io.bold.sfe.web.react;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ReactBridgeTest {

  @Test public void errorReporting() {
    try {
      ScriptEngine manager = new ScriptEngineManager().getEngineByMimeType("application/javascript");
      manager.eval("function error() { bar; }\n");
      manager.eval("var foo = 1;\nerror();");
    } catch (Exception e) {
      // Do nothing
    }
  }
}
