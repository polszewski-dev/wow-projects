package wow.simulator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wow.character.service.ItemService;
import wow.character.service.impl.CachedItemService;
import wow.character.service.impl.ItemServiceImpl;
import wow.commons.repository.ItemRepository;

/**
 * User: POlszewski
 * Date: 2023-08-08
 */
@Component
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character"
})
@PropertySource("classpath:simulator.properties")
@Getter
@Setter
public class SimulatorConfig {
	@Bean
	public ItemService itemService(ItemRepository itemRepository) {
		return new CachedItemService(new ItemServiceImpl(itemRepository));
	}
}
