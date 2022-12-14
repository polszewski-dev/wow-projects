package wow.minmax;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import wow.commons.repository.ItemRepository;
import wow.minmax.config.ItemConfig;
import wow.minmax.service.ItemService;
import wow.minmax.service.impl.CachedItemService;
import wow.minmax.service.impl.ItemServiceImpl;

@SpringBootApplication
@ComponentScan(basePackages = {
		"wow.commons.repository",
		"wow.minmax"
})
public class WowMinmaxApplication {
	public static void main(String[] args) {
		SpringApplication.run(WowMinmaxApplication.class, args);
	}

	@Bean
	public ItemService itemService(ItemRepository itemRepository, ItemConfig itemConfig) {
		return new CachedItemService(new ItemServiceImpl(itemRepository, itemConfig));
	}
}
