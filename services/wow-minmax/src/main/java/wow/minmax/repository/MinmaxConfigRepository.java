package wow.minmax.repository;

import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.ItemLevelFilter;
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
	Optional<ViewConfig> getViewConfig(PlayerCharacter player);

	Set<CharacterFeature> getFeatures(PlayerCharacter player);

	boolean hasFeature(PlayerCharacter player, CharacterFeature feature);

	Optional<FindUpgradesConfig> getFindUpgradesConfig(PlayerCharacter player);

	Optional<ItemLevelFilter> getItemLevelFilter(PlayerCharacter player);
}
