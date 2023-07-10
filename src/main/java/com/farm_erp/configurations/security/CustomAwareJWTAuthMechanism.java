package com.farm_erp.configurations.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.springframework.util.StringUtils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.IdentityProviderManager;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.AuthenticationRequest;
import io.quarkus.security.identity.request.TokenAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.quarkus.vertx.http.runtime.security.ChallengeData;
import io.quarkus.vertx.http.runtime.security.HttpAuthenticationMechanism;
import io.quarkus.vertx.http.runtime.security.HttpCredentialTransport;
import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * An AuthenticationMechanism that validates a caller based on a MicroProfile
 * JWT bearer token
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class CustomAwareJWTAuthMechanism implements HttpAuthenticationMechanism {

	protected static final String AUTHORIZATION_HEADER = "Authorization";
	protected static final String BEARER = "Bearer";
	protected static final String ACCESS_KEY = "Access Key";
	protected static final String accessKey = "sdsRH1119X47fs8NmIyFUkR8NVxBt0CP";

	@Inject
	JWTAuthContextInfo authContextInfo;

	@Inject
	JwtUtils jwtUtils;

	@Override
	public Uni<SecurityIdentity> authenticate(RoutingContext context, IdentityProviderManager identityProviderManager) {

		HttpServerRequest request = context.request();
		String jwt = parseJwt(request);
		// System.out.println("Accessing {} : " + request.path());

		String openHeaderAuth = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.hasText(openHeaderAuth)
				&& openHeaderAuth.startsWith(ACCESS_KEY)) {

			String key = openHeaderAuth.substring(11, openHeaderAuth.length());

			if (validateKey(key)) {
				QuarkusSecurityIdentity identity = QuarkusSecurityIdentity.builder()
						.setPrincipal(new QuarkusPrincipal(key)).build();

				return Uni.createFrom().item(identity);
			}

		}

		if (request.path().startsWith("/server-api/auth")
				|| request.path().contains("/auth")
				|| request.path().contains("/file")
				|| request.path().contains("/swagger")) {

			return Uni.createFrom().optional(Optional.empty());

		} else {
			if (jwt != null) {
				if (jwtUtils.validateJwtToken(jwt, request)) {

					String username = jwtUtils.getUserNameFromJwtToken(jwt);

					Set<String> userroles = new HashSet<>();
					// write method to fetch user roles

					QuarkusSecurityIdentity identity = QuarkusSecurityIdentity.builder()
							.setPrincipal(new QuarkusPrincipal(username)).addRoles(userroles).build();

					return Uni.createFrom().item(identity);

				}

				return Uni.createFrom().failure(new AuthenticationFailedException());

			}

			return Uni.createFrom().failure(new AuthenticationFailedException());

		}
	}

	@Override
	public Uni<ChallengeData> getChallenge(RoutingContext context) {
		// logger.info("Context {token} ");
		ChallengeData result = new ChallengeData(HttpResponseStatus.UNAUTHORIZED.code(),
				HttpHeaderNames.WWW_AUTHENTICATE, "Bearer {token}");
		return Uni.createFrom().item(result);
	}

	@Override
	public Set<Class<? extends AuthenticationRequest>> getCredentialTypes() {
		return Collections.singleton(TokenAuthenticationRequest.class);
	}

	@Override
	public HttpCredentialTransport getCredentialTransport() {

		final String tokenHeaderName = authContextInfo.getTokenHeader();

		if (AUTHORIZATION_HEADER.equals(tokenHeaderName)) {
			return new HttpCredentialTransport(HttpCredentialTransport.Type.AUTHORIZATION, BEARER);

		}

		return null;
	}

	private String parseJwt(HttpServerRequest request) {
		String headerAuth = request.getHeader(AUTHORIZATION_HEADER);

		if (StringUtils.hasText(headerAuth)
				&& headerAuth.startsWith(BEARER)) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}

	public Boolean validateKey(String key) {

		if (key.equals(accessKey)) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;

	}

}
