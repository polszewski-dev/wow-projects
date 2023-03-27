package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.character.model.build.BuffSetId;
import wow.character.model.character.Character;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.pve.GameVersion;
import wow.minmax.converter.dto.CharacterStatsConverter;
import wow.minmax.converter.dto.SpecialAbilityStatsConverter;
import wow.minmax.converter.dto.SpellStatsConverter;
import wow.minmax.model.CharacterStats;
import wow.minmax.model.dto.CharacterStatsDTO;
import wow.minmax.model.dto.SpecialAbilityStatsDTO;
import wow.minmax.model.dto.SpellStatsDTO;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;

import java.util.*;
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

	@GetMapping("{profileId}/spell")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("profileId") UUID profileId
	) {
		Character character = playerProfileService.getPlayerProfile(profileId).getCharacter();

		return character.getRelevantSpells().stream()
				.map(spell -> calculationService.getSpellStats(character, spell))
				.map(spellStatsConverter::convert)
				.toList();
	}

	@GetMapping("{profileId}/character")
	public List<CharacterStatsDTO> getCharacterStats(
			@PathVariable("profileId") UUID profileId
	) {
		Character character = playerProfileService.getPlayerProfile(profileId).getCharacter();

		List<CharacterStatsDTO> result = new ArrayList<>();

		var currentStats = calculationService.getCurrentStats(character);
		var itemStats = calculationService.getEquipmentStats(character);

		result.add(convert("Current buffs", currentStats));
		result.add(convert("Items", itemStats));

		Stream.of(BUFF_COMBOS)
				.filter(x -> x.allBuffSetsArePermitted(character.getGameVersion()))
				.forEach(x -> {
					var stats = calculationService.getStats(character, x.buffSetIds);
					result.add(convert(x.type, stats));
				});

		return result;
	}

	@GetMapping("{profileId}/special")
	public List<SpecialAbilityStatsDTO> getSpecialAbilities(
			@PathVariable("profileId") UUID profileId
	) {
		Character character = playerProfileService.getPlayerProfile(profileId).getCharacter();

		return character.getStats().getSpecialAbilities().stream()
				.sorted(compareSources())
				.map(x -> calculationService.getSpecialAbilityStats(character, x))
				.map(specialAbilityStatsConverter::convert)
				.toList();
	}

	@GetMapping("{profileId}/dps")
	public double getSpellDps(
			@PathVariable("profileId") UUID profileId
	) {
		Character character = playerProfileService.getPlayerProfile(profileId).getCharacter();

		return calculationService.getSpellDps(character, null);
	}

	private CharacterStatsDTO convert(String type, CharacterStats characterStats) {
		CharacterStatsDTO result = characterStatsConverter.convert(characterStats);
		result.setType(type);
		return result;
	}

	private record BuffCombo(String type, BuffSetId... buffSetIds) {
		boolean allBuffSetsArePermitted(GameVersion gameVersion) {
			return gameVersion == GameVersion.VANILLA || !Arrays.asList(buffSetIds).contains(WORLD_BUFFS);
		}
	}

	private static final BuffCombo[] BUFF_COMBOS = {
			new BuffCombo("No buffs"),
			new BuffCombo("Self-buffs", SELF_BUFFS),
			new BuffCombo("Party buffs", SELF_BUFFS, PARTY_BUFFS),
			new BuffCombo("Party buffs & consumes", SELF_BUFFS, PARTY_BUFFS, CONSUMES),
			new BuffCombo("Raid buffs & consumes", SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES),
			new BuffCombo("World buffs & consumes", SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, WORLD_BUFFS, CONSUMES),
	};

	private Comparator<SpecialAbility> compareSources() {
		return
				Comparator.comparingInt((SpecialAbility x) -> x.getSource().getPriority())
				.thenComparing((SpecialAbility x) -> (Comparable<Object>)x.getSource())
				.thenComparingInt(SpecialAbility::getPriority)
				.thenComparing(SpecialAbility::getLine)
		;
	}
}
