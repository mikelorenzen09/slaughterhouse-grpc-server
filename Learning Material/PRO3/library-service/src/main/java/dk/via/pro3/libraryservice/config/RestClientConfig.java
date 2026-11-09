package dk.via.pro3.libraryservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig
{
  @Value("${vinyl.service.base.url}")
  private String baseUrl;

  @Bean
  public RestClient libraryRestClient()
  {
    return RestClient.builder().
        baseUrl(baseUrl).
        defaultHeader(HttpHeaders.CONTENT_TYPE,
        MediaType.APPLICATION_JSON_VALUE).
        build();
  }
}
