package com.simplethingsllc.framework.jersey.dagger2;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Singleton
public class DaggerFilter implements Filter {
  @Override public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override public void doFilter(ServletRequest request, ServletResponse response,
                                 FilterChain chain) throws IOException, ServletException {

  }

  @Override public void destroy() {

  }
}
