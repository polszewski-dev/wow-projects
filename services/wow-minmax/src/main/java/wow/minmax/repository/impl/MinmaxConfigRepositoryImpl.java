package wow.minmax.repository.impl;

import org.springframework.stereotype.Component;
import wow.character.model.character.Character;
import wow.character.model.equipment.ItemLevelFilter;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.TimeRestricted;
import wow.minmax.model.Player;
import wow.minmax.model.config.*;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.impl.parser.config.MinMaxConfigExcelParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@Component
public class MinmaxConfigRepositoryImpl implements MinmaxConfigRepository {
	private final List<ViewConfig> viewConfigs = new ArrayList<>();
	private final List<CharacterFeatureConfig> featureConfigs = new ArrayList<>();
	private final List<FindUpgradesConfig> findUpgradesConfigs = new ArrayList<>();
	private final List<ItemLevelConfig> itemLevelConfigs = new ArrayList<>();

	public MinmaxConfigRepositoryImpl(MinMaxConfigExcelParser parser) throws IOException {
		parser.readFromXls();
		viewConfigs.addAll(parser.getViewConfigs());
		featureConfigs.addAll(parser.getCharacterFeatureConfigs());
		findUpgradesConfigs.addAll(parser.getFindUpgradesConfigs());
		itemLevelConfigs.addAll(parser.getItemLevelConfigs());
	}

	@Override
	public Optional<ViewConfig> getViewConfig(Character character) {
		return getFirst(character, viewConfigs);
	}

	@Override
	public Set<CharacterFeature> getFeatures(Character character) {
		return featureConfigs.stream()
				.filter(x -> x.isAvailableTo(character))
				.filter(x -> x.isAvailableDuring(character.getPhaseId()))
				.map(CharacterFeatureConfig::feature)
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public boolean hasFeature(Character character, CharacterFeature feature) {
		return featureConfigs.stream()
				.anyMatch(x -> x.feature() == feature && x.isAvailableTo(character) && x.isAvailableDuring(character.getPhaseId()));
	}

	@Override
	public Optional<FindUpgradesConfig> getFindUpgradesConfig(Character character) {
		return getFirst(character, findUpgradesConfigs);
	}

	@Override
	public Optional<ItemLevelFilter> getItemLevelFilter(Player player) {
		return getFirst(player, itemLevelConfigs)
				.map(ItemLevelConfig::filter);
	}

	private <T extends CharacterRestricted & TimeRestricted> Optional<T> getFirst(Character character, List<T> list) {
		return list.stream()
				.filter(x -> x.isAvailableTo(character))
				.filter(x -> x.isAvailableDuring(character.getPhaseId()))
				.findFirst();
	}
}
