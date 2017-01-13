package io.bold.sfe.server;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/guice")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuiceTestResource {

  private final Provider<TestObject> singletonProvider;
  private final Provider<TestObject> requestScopedProvider;
  private final Provider<TestObject> instanceProvider;

  @Inject GuiceTestResource(@TestObject.Singleton Provider<TestObject> singletonProvider,
                            @TestObject.RequestScoped Provider<TestObject> requestScopedProvider,
                            @TestObject.Instance Provider<TestObject> instanceProvider) {
    this.singletonProvider = singletonProvider;
    this.requestScopedProvider = requestScopedProvider;
    this.instanceProvider = instanceProvider;
  }

  @GET
  public Response get() {
    Response response = new Response();
    response.singleton = inc(singletonProvider.get());

    // This should equal 2
    inc(requestScopedProvider.get());
    response.requestScoped = inc(requestScopedProvider.get());

    response.instance = inc(instanceProvider.get());
    System.out.println(String.format("s=%d r=%d c=%d",
      response.singleton.injectCount, response.requestScoped.injectCount, response.instance.injectCount));
    return response;
  }

  private TestObject inc(TestObject object) {
    object.injectCount++;
    return object;
  }

  public static class Response {
    TestObject singleton;
    TestObject requestScoped;
    TestObject instance;
  }
}
