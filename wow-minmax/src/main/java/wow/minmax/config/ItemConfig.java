package wow.minmax.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wow.commons.model.categorization.ItemRarity;

/**
 * User: POlszewski
 * Date: 2022-11-14
 */
@Configuration
@ConfigurationProperties("wow.minmax.item")
@Getter
@Setter
public class ItemConfig {
	private int minItemLevel;
	private ItemRarity minRarity;
	private boolean includePvpItems;
	private boolean includeHealingItems;
}
