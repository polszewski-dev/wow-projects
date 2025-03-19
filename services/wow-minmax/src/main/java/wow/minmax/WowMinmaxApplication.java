package wow.minmax;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character",
		"wow.evaluator",
		"wow.simulator",
		"wow.minmax"
})
@PropertySource({
		"classpath:wow-commons.properties",
		"classpath:wow-character.properties",
		"classpath:wow-minmax.properties"
})
public class WowMinmaxApplication {
	public static void main(String[] args) {
		SpringApplication.run(WowMinmaxApplication.class, args);
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("PATCH");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Bean
	public WebClient upgradesWebClient(
			WebClient.Builder webClientBuilder,
			@Value("${upgrades.api.url}") String apiUrl
	) {
		return webClientBuilder.baseUrl(apiUrl).build();
	}

	@Bean
	public WebClient statsWebClient(
			WebClient.Builder webClientBuilder,
			@Value("${stats.api.url}") String apiUrl
	) {
		return webClientBuilder.baseUrl(apiUrl).build();
	}

	@Bean
	public WebClient simulatorWebClient(
			WebClient.Builder webClientBuilder,
			@Value("${simulator.api.url}") String apiUrl
	) {
		return webClientBuilder.baseUrl(apiUrl).build();
	}
}
