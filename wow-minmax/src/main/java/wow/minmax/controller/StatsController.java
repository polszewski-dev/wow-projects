package wow.minmax.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.build.BuffSetId;
import wow.character.model.character.Character;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.minmax.converter.dto.CharacterStatsConverter;
import wow.minmax.converter.dto.SpecialAbilityStatsConverter;
import wow.minmax.converter.dto.SpellStatsConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.CharacterStats;
import wow.minmax.model.dto.CharacterStatsDTO;
import wow.minmax.model.dto.SpecialAbilityStatsDTO;
import wow.minmax.model.dto.SpellStatsDTO;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static wow.character.model.build.BuffSetId.*;

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

	@GetMapping("{characterId}/spell")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);

		return character.getRelevantSpells().stream()
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
			var buffSetIds = buffCombo.buffSetIds.toArray(BuffSetId[]::new);
			var stats = calculationService.getStats(character, buffSetIds);
			result.add(convert(buffCombo.type, stats));
		}

		return result;
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

	@GetMapping("{characterId}/dps")
	public double getSpellDps(
			@PathVariable("characterId") CharacterId characterId
	) {
		Character character = playerProfileService.getCharacter(characterId);

		return calculationService.getSpellDps(character, character.getDamagingSpell());
	}

	private CharacterStatsDTO convert(String type, CharacterStats characterStats) {
		CharacterStatsDTO result = characterStatsConverter.convert(characterStats);
		result.setType(type);
		return result;
	}

	@Getter
	private enum BuffCombo {
		C1("No buffs"),
		C2("Self-buffs", SELF_BUFFS),
		C3("Party buffs", SELF_BUFFS, PARTY_BUFFS),
		C4("Party buffs & consumes", SELF_BUFFS, PARTY_BUFFS, CONSUMES),
		C5("Raid buffs & consumes", SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES),
		C6("World buffs & consumes", SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, WORLD_BUFFS, CONSUMES);

		private final String type;
		private final List<BuffSetId> buffSetIds;

		BuffCombo(String type, BuffSetId... buffSetIds) {
			this.type = type;
			this.buffSetIds = List.of(buffSetIds);
		}

		static List<BuffCombo> getBuffCombos(Character character) {
			boolean worldBuffsAllowed = character.getGameVersion().isWorldBuffs();

			return Stream.of(values())
					.filter(x -> worldBuffsAllowed || !x.buffSetIds.contains(WORLD_BUFFS))
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
