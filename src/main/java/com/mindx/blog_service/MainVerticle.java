package com.mindx.blog_service;

import java.util.logging.Logger;
import com.mindx.blog_service.service.AuthenticationHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import static com.mindx.blog_service.Path.*;

public class MainVerticle extends AbstractVerticle {
  private static Logger logger = Logger.getLogger(MainVerticle.class.getName());
  public static final String JSON = "application/json";
  private static final String PREFIX = ConfigUtil.getUriPrefix();

  @Override
  public void start() throws Exception {
    // Create a Router
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    router.route().failureHandler(Util::failureResponse);

    JWTAuthOptions config = new JWTAuthOptions()
        .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm("RS256")
            .setBuffer(ConfigUtil.getPublicKey()))
        .addPubSecKey(new PubSecKeyOptions()
            .setAlgorithm("RS256")
            .setBuffer(ConfigUtil.getPrivateKey()));

    JWTAuth provider = JWTAuth.create(vertx, config);

    router.post(PREFIX + LOGIN).handler(ctx -> AuthenticationHandler.login(ctx, provider));

    router.post(PREFIX + SIGN_UP).handler(AuthenticationHandler::signUp);

    router.route().handler(rc -> {
      Cookie cookie = rc.request().getCookie("token");
      String auth = cookie == null ? null : cookie.getValue();
      if (auth != null && !auth.isEmpty()) {
        rc.request().headers().add(String.valueOf(HttpHeaders.AUTHORIZATION), "Bearer " + auth);
      }
      String path = rc.request().path();
      if (path.contains(LOGOUT.toString()) && rc.request().method() == HttpMethod.DELETE) { // logout
        cookie.setMaxAge(0);
        rc.response().setStatusCode(204).end();
      } else {
        JWTAuthHandler.create(provider).handle(rc);
      }
    });

    router.get(PREFIX + PROFILE).handler(AuthenticationHandler::profile);

    // Create the HTTP server
    vertx.createHttpServer()
        .requestHandler(router)
        .listen(ConfigUtil.getServerPort())
        .onSuccess(server -> logger.info("HTTP server started on port " + server.actualPort()));
  }
}
