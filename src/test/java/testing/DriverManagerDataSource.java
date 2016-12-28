package com.davidbyttow.sfe.testing;

import io.dropwizard.db.ManagedDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

/** {@link DataSource} that goes directly to the {@link DriverManager}.  Good only for testing */
public class DriverManagerDataSource implements DataSource, ManagedDataSource {
  private String driverClassName;
  private String url;
  private String username;
  private String password;
  private Properties connectionProperties;

  public DriverManagerDataSource() {}

  public DriverManagerDataSource(String driverClassName, String url, String username, String password) {
    setDriverClassName(driverClassName);
    setUrl(url);
    setUsername(username);
    setPassword(password);
  }

  public DriverManagerDataSource(String url, String username, String password) {
    setUrl(url);
    setUsername(username);
    setPassword(password);
  }

  public DriverManagerDataSource(String url) {
    setUrl(url);
  }

  public void setDriverClassName(String driverClassName) {
    this.driverClassName = driverClassName.trim();
    try {
      Class.forName(this.driverClassName, true, ClassLoader.getSystemClassLoader());
    } catch (ClassNotFoundException ex) {
      IllegalStateException ise =
          new IllegalStateException("Could not load JDBC driver class [" + this.driverClassName + "]");
      ise.initCause(ex);
      throw ise;
    }
  }

  public String getDriverClassName() {
    return this.driverClassName;
  }

  public void setUrl(String url) {
    this.url = url.trim();
  }

  public String getUrl() {
    return this.url;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return this.username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return this.password;
  }

  public void setConnectionProperties(Properties connectionProperties) {
    this.connectionProperties = connectionProperties;
  }

  public Properties getConnectionProperties() {
    return this.connectionProperties;
  }

  public Connection getConnection() throws SQLException {
    return getConnectionFromDriverManager();
  }

  public Connection getConnection(String username, String password) throws SQLException {
    return getConnectionFromDriverManager(username, password);
  }

  protected Connection getConnectionFromDriverManager() throws SQLException {
    return getConnectionFromDriverManager(getUsername(), getPassword());
  }

  protected Connection getConnectionFromDriverManager(String username, String password) throws SQLException {
    Properties props = new Properties(getConnectionProperties());
    if (username != null) {
      props.setProperty("user", username);
    }
    if (password != null) {
      props.setProperty("password", password);
    }
    return getConnectionFromDriverManager(getUrl(), props);
  }

  protected Connection getConnectionFromDriverManager(String url, Properties props) throws SQLException {
    return DriverManager.getConnection(url, props);
  }

  @Override public PrintWriter getLogWriter() throws SQLException { return null; }

  @Override public void setLogWriter(PrintWriter out) throws SQLException {}

  @Override public void setLoginTimeout(int seconds) throws SQLException { }

  @Override public int getLoginTimeout() throws SQLException { return 0; }

  @Override public Logger getParentLogger() throws SQLFeatureNotSupportedException { return null; }

  @Override public <T> T unwrap(Class<T> iface) throws SQLException { return null; }

  @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }

  @Override public void start() throws Exception {}

  @Override public void stop() throws Exception {}

}
