package com.davidbyttow.sfe.web;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.Resources;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import com.davidbyttow.sfe.environment.ResourceLoader;
import com.davidbyttow.sfe.service.Env;
import io.dropwizard.servlets.assets.ResourceURL;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import static com.google.common.base.Preconditions.checkArgument;

public class StaticAssetsServlet extends HttpServlet {

  private static final Logger log = LoggerFactory.getLogger(StaticAssetsServlet.class);

  private static final CharMatcher SLASHES = CharMatcher.is('/');

  private static class CachedAsset {
    private final byte[] resource;
    private final String eTag;
    private final long lastModifiedTime;

    private CachedAsset(byte[] resource, long lastModifiedTime) {
      this.resource = resource;
      this.eTag = '"' + Hashing.murmur3_128().hashBytes(resource).toString() + '"';
      this.lastModifiedTime = lastModifiedTime;
    }

    public byte[] getResource() {
      return resource;
    }

    public String getETag() {
      return eTag;
    }

    public long getLastModifiedTime() {
      return lastModifiedTime;
    }
  }

  private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.HTML_UTF_8;

  private final String filePath;
  private final String uriPath;
  private final ResourceLoader resourceLoader;
  private final Env env;

  public StaticAssetsServlet(String filePath, String uriPath, ResourceLoader resourceLoader, Env env) {
    this.resourceLoader = resourceLoader;
    String trimmedPath = SLASHES.trimFrom(filePath);
    this.filePath = trimmedPath.isEmpty() ? trimmedPath : trimmedPath + '/';

    String trimmedUri = SLASHES.trimTrailingFrom(uriPath);
    this.uriPath = trimmedUri.isEmpty() ? "/" : trimmedUri;

    this.env = env;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    StringBuilder builder = new StringBuilder(req.getServletPath());
    try {
      if (req.getPathInfo() != null) {
        builder.append(req.getPathInfo());
      }

      byte[] outputData;
      if (env.isLocalDevelopment()) {
        String path = getRelativeRequestedResourcePath(builder.toString());
        InputStream stream = resourceLoader.open(path);
        if (stream == null) {
          resp.sendError(HttpServletResponse.SC_NOT_FOUND);
          return;
        }
        outputData = IOUtils.toByteArray(stream);
      } else {
        CachedAsset cachedAsset = loadAsset(builder.toString());
        if (cachedAsset == null) {
          resp.sendError(HttpServletResponse.SC_NOT_FOUND);
          return;
        }

        if (isCachedClientSide(req, cachedAsset)) {
          resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
          return;
        }
        resp.setDateHeader(HttpHeaders.LAST_MODIFIED, cachedAsset.getLastModifiedTime());
        resp.setHeader(HttpHeaders.ETAG, cachedAsset.getETag());
        outputData = cachedAsset.getResource();
      }

      final String mimeTypeOfExtension = req.getServletContext().getMimeType(req.getRequestURI());
      MediaType mediaType = DEFAULT_MEDIA_TYPE;

      if (mimeTypeOfExtension != null) {
        try {
          mediaType = MediaType.parse(mimeTypeOfExtension);
          if (mediaType.is(MediaType.ANY_TEXT_TYPE)) {
            mediaType = mediaType.withCharset(Charsets.UTF_8);
          }
        } catch (IllegalArgumentException ignore) {}
      }

      resp.setContentType(mediaType.type() + '/' + mediaType.subtype());

      if (mediaType.charset().isPresent()) {
        resp.setCharacterEncoding(mediaType.charset().get().toString());
      }

      try (ServletOutputStream output = resp.getOutputStream()) {
        output.write(outputData);
      }
    } catch (RuntimeException | URISyntaxException e) {
      log.warn("Couldn't load asset: " + builder.toString(), e);
      resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
  }

  private String getAbsoluteRequestedResourcePath(String key) {
    return "/" + getRelativeRequestedResourcePath(key);
  }

  private String getRelativeRequestedResourcePath(String key) {
    checkArgument(key.startsWith(uriPath));
    String requestedResourcePath = SLASHES.trimFrom(key.substring(uriPath.length()));
    return SLASHES.trimFrom(this.filePath + requestedResourcePath);
  }

  private CachedAsset loadAsset(String key) throws URISyntaxException, IOException {
    String absoluteRequestedResourcePath = getAbsoluteRequestedResourcePath(key);

    URL resourceUrl = getClass().getResource(absoluteRequestedResourcePath);
    if (ResourceURL.isDirectory(resourceUrl)) {
      return null;
    }

    long lastModified = ResourceURL.getLastModified(resourceUrl);
    if (lastModified < 1) {
      // Something went wrong trying to get the last modified time: just use the current time
      lastModified = System.currentTimeMillis();
    }

    byte[] bytes = Resources.toByteArray(resourceUrl);

    // Zero out the millis since the date we get back from If-Modified-Since will not have them
    lastModified = (lastModified / 1000) * 1000;
    return new CachedAsset(bytes, lastModified);
  }

  private boolean isCachedClientSide(HttpServletRequest req, CachedAsset cachedAsset) {
    return cachedAsset.getETag().equals(req.getHeader(HttpHeaders.IF_NONE_MATCH)) ||
        (req.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE) >= cachedAsset.getLastModifiedTime());
  }
}
