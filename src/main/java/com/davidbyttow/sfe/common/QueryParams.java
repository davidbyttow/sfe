package com.davidbyttow.sfe.common;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryParams {
  public static List<NameValuePair> createList(Map<String, ?> values) {
    List<NameValuePair> params = new ArrayList<>(values.size());
    for (Map.Entry<String, ?> param : values.entrySet()) {
      params.add(new BasicNameValuePair(param.getKey(), param.getValue().toString()));
    }
    return params;
  }

  public static String createString(Map<String, ?> values) throws UnsupportedEncodingException {
    List<String> parts = new ArrayList<>(values.size());
    for (Map.Entry<String, ?> param : values.entrySet()) {
      String key = URLEncoder.encode(param.getKey(), Charsets.UTF_8.name());
      String value = URLEncoder.encode(param.getValue().toString(), Charsets.UTF_8.name());
      parts.add(String.format("%s=%s", key, value));
    }
    return Joiner.on('&').join(parts);
  }
}
