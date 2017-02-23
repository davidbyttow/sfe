package io.bold.sfe.server;

/**
 * TODO(d): This currently fails

public class TestServerTest {

  static final DropwizardTestSupport<TestConfig> SUPPORT =
    new DropwizardTestSupport<>(TestServer.class,
      ResourceHelpers.resourceFilePath("test-development.yaml"),
      ConfigOverride.config("server.applicationConnectors[0].port", "0"));

  @BeforeClass
  public static void beforeClass() {
    System.setProperty(CommonConfigBundle.ENV_SYSTEM_PROPERTY, "development");
    System.setProperty(CommonConfigBundle.TESTING_SYSTEM_PROPERTY, "true");
    SUPPORT.before();
  }

  @AfterClass
  public static void afterClass() {
    SUPPORT.after();
  }

  @Test
  public void guiceWorks() {
    Client client = new JerseyClientBuilder(SUPPORT.getEnvironment()).build("test-client");
    for (int i = 0; i < 1000; ++i) {
      Response response = client.target(
        String.format("http://localhost.com:%d/guice", SUPPORT.getLocalPort()))
        .request()
        .get();
      GuiceTestResource.Response resp = response.readEntity(GuiceTestResource.Response.class);
      assertThat(resp.instance.injectCount).isEqualTo(1);
      assertThat(resp.singleton.injectCount).isEqualTo(i + 1);
      assertThat(resp.requestScoped.injectCount).isEqualTo(2);
    }
  }
}
*/
