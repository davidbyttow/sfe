package io.bold.sfe.config;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.dropwizard.configuration.ConfigurationSourceProvider;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

/** {@link io.dropwizard.configuration.ConfigurationSourceProvider} that can do variable substitution within the configuration file */
public class HandlebarsConfigSourceProvider implements ConfigurationSourceProvider {
  private final Object scope;
  private final Handlebars handlebars = new Handlebars(new CompositeTemplateLoader(
      new ClassPathTemplateLoader("/", ""),

      // Support relative paths
      new FileTemplateLoader("", "") {
        @Override
        public String resolve(String uri) {
          return uri;
        }

        @Override protected String normalize(String location) {
          return location;
        }

        @Override
        protected URL getResource(String location) throws IOException {
          File file = new File(location).getAbsoluteFile();
          return file.exists() ? file.toURI().toURL() : null;
        }
      }));


  public HandlebarsConfigSourceProvider(Map<String, Object> vars) {
    this.scope = Preconditions.checkNotNull(vars);
  }

  @Override public InputStream open(String path) throws IOException {
    Template template = handlebars.compile(path);
    StringWriter sw = new StringWriter();

    Context context = Context.newBuilder(scope)
        .resolver(new FunctionValueResolver(), MapValueResolver.INSTANCE, new JavaBeanValueResolver())
        .build();
    template.apply(context, sw);
    return new ByteArrayInputStream(sw.toString().getBytes(Charsets.UTF_8));
  }
}
