package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.character.Character;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.buffs.BuffCategory;
import wow.minmax.converter.dto.CharacterStatsConverter;
import wow.minmax.converter.dto.RotationStatsConverter;
import wow.minmax.converter.dto.SpecialAbilityStatsConverter;
import wow.minmax.converter.dto.SpellStatsConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.CharacterStats;
import wow.minmax.model.RotationStats;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.dto.CharacterStatsDTO;
import wow.minmax.model.dto.RotationStatsDTO;
import wow.minmax.model.dto.SpecialAbilityStatsDTO;
import wow.minmax.model.dto.SpellStatsDTO;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.model.buffs.BuffCategory.*;

/**
 * User: POlszewski
 * Date: 2021-12-31
 */
@RestController
@RequestMapping("api/v1/stats")
@AllArgsConstructor
public class StatsController {
	private final PlayerProfileService playerProfileService;
	private final CalculationService calculationService;
	private final SpellStatsConverter spellStatsConverter;
	private final CharacterStatsConverter characterStatsConverter;
	private final SpecialAbilityStatsConverter specialAbilityStatsConverter;
	private final RotationStatsConverter rotationStatsConverter;

	@GetMapping("{characterId}/spell")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);

		ViewConfig viewConfig = playerProfileService.getViewConfig(character);

		return viewConfig.getRelevantSpells().stream()
				.map(spellId -> character.getSpellbook().getSpell(spellId))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(spell -> calculationService.getSpellStats(character, spell))
				.map(spellStatsConverter::convert)
				.toList();
	}

	@GetMapping("{characterId}/character")
	public List<CharacterStatsDTO> getCharacterStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);

		List<CharacterStatsDTO> result = new ArrayList<>();

		var currentStats = calculationService.getCurrentStats(character);
		var itemStats = calculationService.getEquipmentStats(character);

		result.add(convert("Current buffs", currentStats));
		result.add(convert("Items", itemStats));

		for (BuffCombo buffCombo : BuffCombo.getBuffCombos(character)) {
			CharacterStats stats = getCharacterStats(character, buffCombo);
			result.add(convert(buffCombo.type, stats));
		}

		return result;
	}

	private CharacterStats getCharacterStats(Character character, BuffCombo buffCombo) {
		var buffCategories = buffCombo.buffCategories.toArray(BuffCategory[]::new);
		return calculationService.getStats(character, buffCategories);
	}

	@GetMapping("{characterId}/special")
	public List<SpecialAbilityStatsDTO> getSpecialAbilities(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);

		return character.getStats().getSpecialAbilities().stream()
				.sorted(compareSources())
				.map(x -> calculationService.getSpecialAbilityStats(character, x))
				.map(specialAbilityStatsConverter::convert)
				.toList();
	}

	@GetMapping("{characterId}/rotation/stats")
	public RotationStatsDTO getRotationStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);
		RotationStats rotationStats = calculationService.getRotationStats(character, character.getRotation());

		return rotationStatsConverter.convert(rotationStats);
	}

	private CharacterStatsDTO convert(String type, CharacterStats characterStats) {
		CharacterStatsDTO result = characterStatsConverter.convert(characterStats);
		result.setType(type);
		return result;
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

		static List<BuffCombo> getBuffCombos(Character character) {
			boolean worldBuffsAllowed = character.getGameVersion().isWorldBuffs();

			return Stream.of(values())
					.filter(x -> worldBuffsAllowed || !x.buffCategories.contains(WORLD_BUFF))
					.toList();
		}
	}

	private Comparator<SpecialAbility> compareSources() {
		return
				Comparator.comparingInt((SpecialAbility x) -> x.getSource().getPriority())
				.thenComparing((SpecialAbility x) -> (Comparable<Object>)x.getSource())
				.thenComparingInt(SpecialAbility::getPriority)
				.thenComparing(SpecialAbility::getLine)
		;
	}
}
