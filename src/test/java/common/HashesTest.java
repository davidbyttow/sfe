package com.davidbyttow.sfe.common;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class HashesTest {
  @Test public void testSizes() {
    assertThat(Hashes.md5("projectmanaging-platformbeta-itzmfx")).isEqualTo("0e35f217c9bc1240cfe277eccc148862");
    assertThat(Hashes.md5("")).hasSize(32);
    for (int i = 0; i < 1000; ++i) {
      assertThat(Hashes.md5(MoreRandoms.randomAlphanumeric(MoreRandoms.nextIntInclusive(0, 32)))).hasSize(32);
    }
  }
}
