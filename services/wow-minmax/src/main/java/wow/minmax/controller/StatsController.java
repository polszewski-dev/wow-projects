package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.character.Character;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.snapshot.StatSummary;
import wow.commons.client.converter.TalentConverter;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.talent.Talent;
import wow.minmax.client.dto.*;
import wow.minmax.converter.dto.CharacterStatsConverter;
import wow.minmax.converter.dto.RotationStatsConverter;
import wow.minmax.converter.dto.SpecialAbilityStatsConverter;
import wow.minmax.converter.dto.SpellStatsConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.SpecialAbility;
import wow.minmax.model.config.CharacterFeature;
import wow.minmax.repository.MinmaxConfigRepository;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerCharacterService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.model.buff.BuffCategory.*;

/**
 * User: POlszewski
 * Date: 2021-12-31
 */
@RestController
@RequestMapping("api/v1/stats")
@AllArgsConstructor
public class StatsController {
	private final PlayerCharacterService playerCharacterService;
	private final CalculationService calculationService;
	private final MinmaxConfigRepository minmaxConfigRepository;
	private final SpellStatsConverter spellStatsConverter;
	private final CharacterStatsConverter characterStatsConverter;
	private final SpecialAbilityStatsConverter specialAbilityStatsConverter;
	private final RotationStatsConverter rotationStatsConverter;
	private final TalentConverter talentConverter;

	@GetMapping("{characterId}/spell")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerCharacterService.getCharacter(characterId);

		var viewConfig = playerCharacterService.getViewConfig(character);

		return viewConfig.relevantSpells().stream()
				.map(character::getAbility)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(ability -> calculationService.getSpellStats(character, ability))
				.map(spellStatsConverter::convert)
				.toList();
	}

	@GetMapping("{characterId}/character")
	public List<CharacterStatsDTO> getCharacterStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerCharacterService.getCharacter(characterId);

		var result = new ArrayList<CharacterStatsDTO>();

		var currentStats = calculationService.getCurrentStats(character);
		var itemStats = calculationService.getEquipmentStats(character);

		result.add(convert("Current buffs", currentStats));
		result.add(convert("Items", itemStats));

		for (BuffCombo buffCombo : getBuffCombos(character)) {
			var stats = getCharacterStats(character, buffCombo);
			result.add(convert(buffCombo.type, stats));
		}

		return result;
	}

	private StatSummary getCharacterStats(PlayerCharacter character, BuffCombo buffCombo) {
		var buffCategories = buffCombo.buffCategories.toArray(BuffCategory[]::new);
		return calculationService.getStats(character, buffCategories);
	}

	@GetMapping("{characterId}/special")
	public List<SpecialAbilityStatsDTO> getSpecialAbilityStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerCharacterService.getCharacter(characterId);

		return getSpecialAbilities(character)
				.sorted(compareSources())
				.map(specialAbility -> calculationService.getSpecialAbilityStats(specialAbility, character))
				.map(specialAbilityStatsConverter::convert)
				.toList();
	}

	private Stream<SpecialAbility> getSpecialAbilities(PlayerCharacter character) {
		var rotationStats = calculationService.getAccumulatedRotationStats(character, character.getRotation());

		return Stream.concat(
				rotationStats.getActivatedAbilities().stream().map(SpecialAbility::of),
				rotationStats.getNonModifierEffects().stream().map(SpecialAbility::of)
		);
	}

	@GetMapping("{characterId}/rotation")
	public RotationStatsDTO getRotationStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerCharacterService.getCharacter(characterId);
		var rotationStats = calculationService.getRotationStats(character, character.getRotation());

		return rotationStatsConverter.convert(rotationStats);
	}

	private CharacterStatsDTO convert(String type, StatSummary statSummary) {
		return characterStatsConverter
				.convert(statSummary)
				.withType(type);
	}

	@Getter
	private enum BuffCombo {
		C1("No buffs"),
		C2("Self-buffs", SELF_BUFF),
		C3("Party buffs", SELF_BUFF, PARTY_BUFF),
		C4("Party buffs & consumes", SELF_BUFF, PARTY_BUFF, CONSUME),
		C5("Raid buffs & consumes", SELF_BUFF, PARTY_BUFF, RAID_BUFF, CONSUME),
		C6("World buffs & consumes", SELF_BUFF, PARTY_BUFF, RAID_BUFF, WORLD_BUFF, CONSUME);

		private final String type;
		private final List<BuffCategory> buffCategories;

		BuffCombo(String type, BuffCategory... buffCategories) {
			this.type = type;
			this.buffCategories = List.of(buffCategories);
		}
	}

	private List<BuffCombo> getBuffCombos(Character character) {
		boolean worldBuffsAllowed = minmaxConfigRepository.hasFeature(character, CharacterFeature.WORLD_BUFFS);

		return Stream.of(BuffCombo.values())
				.filter(x -> worldBuffsAllowed || !x.buffCategories.contains(WORLD_BUFF))
				.toList();
	}

	private Comparator<SpecialAbility> compareSources() {
		return
				Comparator.comparingInt((SpecialAbility x) -> x.getSource().getPriority())
				.thenComparing((SpecialAbility x) -> (Comparable<Object>)x.getSource())
				.thenComparingInt(SpecialAbility::getPriority)
				.thenComparing(SpecialAbility::getTooltip)
		;
	}

	@GetMapping("{characterId}/talent")
	public List<TalentStatsDTO> getTalentStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		var character = playerCharacterService.getCharacter(characterId);

		return character.getTalents().getList().stream()
				.map(x -> getTalentStatDTO(x, character))
				.toList();
	}

	private TalentStatsDTO getTalentStatDTO(Talent talent, PlayerCharacter character) {
		var talentDTO = talentConverter.convert(talent);
		var spEquivalent = getSpEquivalent(talent, character);

		return new TalentStatsDTO(
				talentDTO,
				talent.getEffect().getTooltip(),
				spEquivalent
		);
	}

	private double getSpEquivalent(Talent talent, PlayerCharacter character) {
		return calculationService.getSpEquivalent(
				talent.getTalentId(),
				character
		);
	}
}
