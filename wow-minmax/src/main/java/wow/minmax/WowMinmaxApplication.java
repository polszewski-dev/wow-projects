package wow.minmax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import wow.character.config.ItemConfig;
import wow.character.service.ItemService;
import wow.character.service.impl.CachedItemService;
import wow.character.service.impl.ItemServiceImpl;
import wow.character.service.impl.classifiers.PveRoleStatClassifier;
import wow.commons.repository.ItemRepository;

import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character",
		"wow.minmax"
})
public class WowMinmaxApplication {
	public static void main(String[] args) {
		SpringApplication.run(WowMinmaxApplication.class, args);
	}

	@Bean
	public ItemService itemService(ItemRepository itemRepository, ItemConfig itemConfig, List<PveRoleStatClassifier> pveRoleStatClassifies) {
		return new CachedItemService(new ItemServiceImpl(itemRepository, itemConfig, pveRoleStatClassifies));
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
//		config.setAllowCredentials(true);
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
}
