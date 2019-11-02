/*
 * Copyright (c) 2011-2019 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.netty.http;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.junit.BeforeClass;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.server.HttpServer;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author Violeta Georgieva
 */
public class HttpsMetricsHandlerTests extends HttpMetricsHandlerTests {
	private static SelfSignedCertificate ssc;

	@BeforeClass
	public static void createSelfSignedCertificate() throws CertificateException {
		ssc = new SelfSignedCertificate();
	}

	@Override
	protected HttpServer customizeServerOptions(HttpServer server) {
		try {
			SslContext ctx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
			                                  .sslProvider(SslProvider.JDK)
			                                  .build();
			return server.secure(ssl -> ssl.sslContext(ctx));
		}
		catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected HttpClient customizeClientOptions(HttpClient httpClient) {
		try {
			SslContext ctx = SslContextBuilder.forClient()
			                                  .trustManager(InsecureTrustManagerFactory.INSTANCE)
			                                  .sslProvider(SslProvider.JDK)
			                                  .build();
			return httpClient.secure(ssl -> ssl.sslContext(ctx));
		}
		catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void checkTlsTimer(String name, String[] tags, boolean exists, long expectedCount) {
		checkTimer(name, tags, exists, expectedCount);
	}
}
