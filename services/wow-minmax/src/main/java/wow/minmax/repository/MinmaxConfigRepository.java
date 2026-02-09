package wow.minmax.repository;

import wow.character.model.equipment.ItemLevelFilter;
import wow.minmax.model.Player;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ScriptInfo;
import wow.minmax.model.config.ViewConfig;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
public interface MinmaxConfigRepository {
	Optional<ViewConfig> getViewConfig(Player player);

	Set<CharacterFeature> getFeatures(Player player);

	boolean hasFeature(Player player, CharacterFeature feature);

	Optional<FindUpgradesConfig> getFindUpgradesConfig(Player player);

	Optional<ItemLevelFilter> getItemLevelFilter(Player player);

	List<ScriptInfo> getAvailableScripts(Player player);

	Optional<ScriptInfo> getScript(String scriptPath, Player player);
}
