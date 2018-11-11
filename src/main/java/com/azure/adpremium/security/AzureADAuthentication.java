package com.azure.adpremium.security;

import java.net.MalformedURLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.aad.adal4j.UserAssertion;

public class AzureADAuthentication {

	private static final String CURRENT_USER_PRINCIPAL = "CURRENT_USER_PRINCIPAL";
	private static final String CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN = "CURRENT_USER_PRINCIPAL_GRAPHAPI_TOKEN";
	private static final String DEFAULE_ROLE_PREFIX = "ROLE_";
	private static final String REQUEST_ID_SUFFIX = "aadfeed5";

	private static final String TOKEN_HEADER = "Authorization";
	private static final String TOKEN_TYPE = "Bearer ";
	private static final String CLIENT_ID = "e752e97f-5840-4f7d-91d4-f30723e127e8";
	private static final String CLIENT_SECRET = "7QQMVAB/Q/e3S/G50TEMmSO5LcNV1y+R2rwSx6wj54s=";

	public static void aadAuthentication(HttpServletRequest request)
			throws MalformedURLException, ServiceUnavailableException, InterruptedException, ExecutionException {

		final String authHeader = request.getHeader(TOKEN_HEADER);

		if (authHeader != null && authHeader.startsWith(TOKEN_TYPE)) {

			final String idToken = authHeader.replace(TOKEN_TYPE, "");

			final String tenantId = "lab0042.modernworkplacelab.biz";

			String accessToken = acquireTokenForGraphApi(idToken, tenantId).getAccessToken();

			boolean authenticated = false;
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(accessToken)) {
				authenticated = true;
			}
			System.out.println("Authenticated: " + authenticated);
		}

	}

	private static AuthenticationResult acquireTokenForGraphApi(String idToken, String tenantId)
			throws MalformedURLException, ServiceUnavailableException, InterruptedException, ExecutionException {

		final ClientCredential credential = new ClientCredential(CLIENT_ID, CLIENT_SECRET);
		final UserAssertion assertion = new UserAssertion(idToken);

		AuthenticationResult result = null;
		ExecutorService service = null;
		try {
			service = Executors.newFixedThreadPool(1);
			final AuthenticationContext context = new AuthenticationContext(
					"https://login.microsoftonline.com/" + tenantId + "/", true, service);
			context.setCorrelationId(getCorrelationId());
			final Future<AuthenticationResult> future = context.acquireToken("https://graph.microsoft.com", assertion,
					credential, null);
			result = future.get();
		} finally {
			if (service != null) {
				service.shutdown();
			}
		}

		if (result == null) {
			throw new ServiceUnavailableException("unable to acquire on-behalf-of token for client " + CLIENT_ID);
		}
		return result;
	}

	private static String getCorrelationId() {
		final String uuid = UUID.randomUUID().toString();
		return uuid.substring(0, uuid.length() - REQUEST_ID_SUFFIX.length()) + REQUEST_ID_SUFFIX;
	}

}
