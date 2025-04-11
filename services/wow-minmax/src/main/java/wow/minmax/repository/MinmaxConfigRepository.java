package wow.minmax.repository;

import wow.character.model.character.Character;
import wow.character.model.equipment.ItemLevelFilter;
import wow.minmax.model.Player;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ViewConfig;

import java.util.Optional;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public interface MinmaxConfigRepository {
	Optional<ViewConfig> getViewConfig(Character character);

	Set<CharacterFeature> getFeatures(Character character);

	boolean hasFeature(Character character, CharacterFeature feature);

	Optional<FindUpgradesConfig> getFindUpgradesConfig(Character character);

	Optional<ItemLevelFilter> getItemLevelFilter(Player player);
}
