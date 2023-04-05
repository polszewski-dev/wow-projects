package wow.character;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import wow.character.service.ItemService;
import wow.character.service.impl.CachedItemService;
import wow.character.service.impl.ItemServiceImpl;
import wow.character.service.impl.classifiers.PveRoleStatClassifier;
import wow.commons.repository.ItemRepository;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-12
 */
@ComponentScan(basePackages = {
		"wow.commons",
		"wow.character"
})
@PropertySource("classpath:test.properties")
public class WowCharacterSpringTestConfig {
	@Bean
	public ItemService itemService(ItemRepository itemRepository, List<PveRoleStatClassifier> pveRoleStatClassifies) {
		return new CachedItemService(new ItemServiceImpl(itemRepository, pveRoleStatClassifies));
	}
}
