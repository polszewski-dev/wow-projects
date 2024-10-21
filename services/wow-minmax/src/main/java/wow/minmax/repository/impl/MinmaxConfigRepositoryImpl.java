package wow.minmax.repository.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.character.model.character.Character;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.TimeRestricted;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.model.config.CharacterFeatureConfig;
import wow.minmax.model.config.FindUpgradesConfig;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.repository.impl.parser.config.MinMaxConfigExcelParser;

import javax.annotation.PostConstruct;
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
@Repository
public class MinmaxConfigRepositoryImpl implements MinmaxConfigRepository {
	private final List<ViewConfig> viewConfigs = new ArrayList<>();
	private final List<CharacterFeatureConfig> featureConfigs = new ArrayList<>();
	private final List<FindUpgradesConfig> findUpgradesConfigs = new ArrayList<>();

	@Value("${config.xls.file.path}")
	private String xlsFilePath;

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

	private <T extends CharacterRestricted & TimeRestricted> Optional<T> getFirst(Character character, List<T> list) {
		return list.stream()
				.filter(x -> x.isAvailableTo(character))
				.filter(x -> x.isAvailableDuring(character.getPhaseId()))
				.findFirst();
	}

	@PostConstruct
	public void init() throws IOException {
		var excelParser = new MinMaxConfigExcelParser(xlsFilePath, this);
		excelParser.readFromXls();
	}

	public void add(ViewConfig config) {
		viewConfigs.add(config);
	}

	public void add(CharacterFeatureConfig featureConfig) {
		featureConfigs.add(featureConfig);
	}

	public void add(FindUpgradesConfig config) {
		findUpgradesConfigs.add(config);
	}
}
