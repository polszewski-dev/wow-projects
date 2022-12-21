package wow.character.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import wow.commons.model.categorization.ItemRarity;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Configuration
@Getter
@Setter
public class ItemConfig {
	@Value("${wow.item.service.minItemLevel}")
	private int minItemLevel;

	@Value("${wow.item.service.minRarity}")
	private ItemRarity minRarity;

	@Value("${wow.item.service.includePvpItems}")
	private boolean includePvpItems;

	@Value("${wow.item.service.includeHealingItems}")
	private boolean includeHealingItems;
}
