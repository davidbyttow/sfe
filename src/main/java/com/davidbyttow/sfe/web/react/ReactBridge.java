package com.davidbyttow.sfe.web.react;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.inject.Provider;
import com.davidbyttow.sfe.environment.ResourceLoader;
import com.davidbyttow.sfe.service.Env;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReactBridge {

  private static final Logger log = LoggerFactory.getLogger(ReactBridge.class);

  private final Env env;
  private final ObjectMapper objectMapper;
  private final ScriptEngine js;
  private final FilesProvider filesProvider;
  private final String renderCodeTemplate;
  private String lastPrecompiledCode;

  private ReactBridge(ResourceLoader resourceLoader, Env env, ObjectMapper objectMapper, ScriptEngine js, FilesProvider filesProvider, String renderCodeTemplate) {
    this.env = env;
    this.objectMapper = objectMapper;
    this.js = js;
    this.filesProvider = filesProvider;
    this.renderCodeTemplate = renderCodeTemplate;
  }

  private static String load(ResourceLoader resourceLoader, String path) throws IOException {
    return IOUtils.toString(Preconditions.checkNotNull(resourceLoader.open(path)), Charsets.UTF_8);
  }

  private static String loadAll(ResourceLoader resourceLoader, List<String> paths) throws IOException {
    StringBuilder builder = new StringBuilder();
    for (String path : paths) {
      builder.append(load(resourceLoader, path));
    }
    return builder.toString();
  }

  static class FilesProvider implements Provider<String> {

    private final List<String> files;
    private final ResourceLoader resourceLoader;

    FilesProvider(List<String> files, ResourceLoader resourceLoader) {
      this.files = files;
      this.resourceLoader = resourceLoader;
    }

    @Override public String get() {
      try {
        return loadAll(resourceLoader, files);
      } catch (IOException e) {
        throw Throwables.propagate(e);
      }
    }
  }

  public static ReactBridge newReactBridge(ResourceLoader resourceLoader, Env env, ObjectMapper objectMapper, List<String> files) {
    log.info("Creating new react bridge");

    ScriptEngine js = new ScriptEngineManager().getEngineByMimeType("application/javascript");
    FilesProvider filesProvider = new FilesProvider(files, resourceLoader);
    js.put("filesProvider", filesProvider);

    String renderCodeTemplate;
    try {
      ImmutableList.Builder<String> reactFiles = ImmutableList.<String>builder()
          .add("/lib/nashorn/boilerplate.js");

      String boilerplateCode = loadAll(resourceLoader, reactFiles.build());
      js.eval(boilerplateCode);

      renderCodeTemplate = load(resourceLoader, "/lib/nashorn/render.js");

    } catch (IOException | ScriptException e) {
      throw Throwables.propagate(e);
    }
    ReactBridge bridge = new ReactBridge(resourceLoader, env, objectMapper, js, filesProvider, renderCodeTemplate);
    if (!env.isLocalDevelopment()) {
      bridge.precompileAll();
    }
    return bridge;
  }

  private Object evalSafe(String code) {
    try {
      return js.eval(code);
    } catch (ScriptException e) {
      throw propagateDetailedError(e);
    }
  }

  private RuntimeException propagateDetailedError(ScriptException cause) {
    String[] lines = lastPrecompiledCode.split("\n");
    int lineNumber = cause.getLineNumber();
    if (lines.length < lineNumber) {
      return Throwables.propagate(cause);
    }
    int from = Math.max(lineNumber - 4, 0);
    int to = Math.min(lineNumber + 4, lines.length);
    StringBuilder output = new StringBuilder();
    for (int i = from; i < to; ++i) {
      boolean isLine = i == lineNumber - 1;
      if (isLine) {
        output.append('\n');
      }
      output.append(String.format("%d: %s\n", i + 1, lines[i]));
      if (isLine) {
        output.append('\n');
      }
    }
    String error = cause.getMessage() + "\nPossible error location:\n" + output.toString();
    log.error(error);
    throw new RuntimeException(error, cause);
  }

  private void precompileAll() {
    lastPrecompiledCode = filesProvider.get();
    Stopwatch s = Stopwatch.createStarted();
    evalSafe(lastPrecompiledCode);
    log.info("Precompile (size={}) completed in {}ms", lastPrecompiledCode.length(), s.elapsed(TimeUnit.MILLISECONDS));
  }

  public String render(String componentClassName, Object props, @Nullable Object globalData) {
    try {
      if (env.isLocalDevelopment()) {
        precompileAll();
      }

      String executable = renderCodeTemplate.replace("@@componentClassName@@", componentClassName)
          .replace("@@glob@@", objectMapper.writeValueAsString(globalData))
          .replace("@@props@@", objectMapper.writeValueAsString(props));
      Stopwatch s = Stopwatch.createStarted();
      String ret = (String) evalSafe(executable);
      log.info("Rendering (size={}) completed in {}ms", executable.length(), s.elapsed(TimeUnit.MILLISECONDS));
      return ret;
    } catch (JsonProcessingException e) {
      throw Throwables.propagate(e);
    }
  }
}
