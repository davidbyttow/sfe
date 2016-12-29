package io.bold.sfe.common;

import java.io.File;

public final class Files {
  private Files() {}

  public static File join(String ...paths) {
    // TODO(d): Make this more efficient.
    File f = new File(paths[0]);
    for (int i = 1; i < paths.length; ++i ){
      f = new File(f, paths[i]);
    }
    return f;
  }
}
