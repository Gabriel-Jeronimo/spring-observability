package com.example.appb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws java.io.IOException {
                    super.prepareConnection(connection, httpMethod);
                    if (connection instanceof HttpsURLConnection) {
                        ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
                        ((HttpsURLConnection) connection).setHostnameVerifier((hostname, session) -> true);
                    }
                }
            };

            return new RestTemplate(requestFactory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create RestTemplate with SSL bypass", e);
        }
    }
}
