package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@AllArgsConstructor
@Getter
@Setter
public class EquippableItemConfig {
	private ItemConfig item;
	private EnchantConfig enchant;
	private List<GemConfig> gems;
}
