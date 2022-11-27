package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.spells.Snapshot;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.Build;
import wow.minmax.converter.dto.PlayerSpellStatsConverter;
import wow.minmax.model.BuildIds;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.model.dto.PlayerStatsDTO;
import wow.minmax.model.dto.SpecialAbilityStatsDTO;
import wow.minmax.model.dto.SpellStatsDTO;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static wow.commons.model.unit.Build.BuffSetId;
import static wow.commons.model.unit.Build.BuffSetId.*;

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
	private final PlayerSpellStatsConverter playerSpellStatsConverter;

	@GetMapping("spell/{profileId}")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);
		List<SpellStatsDTO> result = new ArrayList<>();

		for (Spell spell : playerProfile.getBuild().getRelevantSpells()) {
			PlayerSpellStats playerSpellStats = calculationService.getPlayerSpellStats(playerProfile, spell);
			result.add(playerSpellStatsConverter.convert(playerSpellStats));
		}

		return result;
	}

	@GetMapping("player/{profileId}")
	public List<PlayerStatsDTO> getPlayerStats(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);

		return List.of(
				getPlayerStatsDTO("Current buffs", playerProfile, CURRENT),
				getEquipmentStats("Items", playerProfile),
				getPlayerStatsDTO("No buffs", playerProfile),
				getPlayerStatsDTO("Self-buffs", playerProfile, SELF_BUFFS),
				getPlayerStatsDTO("Party buffs", playerProfile, SELF_BUFFS, PARTY_BUFFS),
				getPlayerStatsDTO("Party buffs & consumes", playerProfile, SELF_BUFFS, PARTY_BUFFS, CONSUMES),
				getPlayerStatsDTO("Raid buffs & consumes", playerProfile, SELF_BUFFS, PARTY_BUFFS, RAID_BUFFS, CONSUMES)
		);
	}

	@GetMapping("special/{profileId}")
	public List<SpecialAbilityStatsDTO> getSpecialAbilityStats(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);

		return playerProfile.getStats().getSpecialAbilities().stream()
				.filter(x -> x.getLine() != null)
				.map(x -> getSpecialAbilityStatsDTO(playerProfile, x))
				.collect(Collectors.toList());
	}

	private PlayerStatsDTO getEquipmentStats(String type, PlayerProfile playerProfile) {
		PlayerProfile copy = playerProfile.copy();

		Build emptyBuild = playerProfileService.getBuild(BuildIds.NONE, playerProfile.getLevel());
		emptyBuild.setDamagingSpell(playerProfile.getDamagingSpell());

		copy.setBuild(emptyBuild);
		copy.setBuffs(List.of());

		return getPlayerStatsDTO(type, copy, copy.getDamagingSpell());
	}

	private PlayerStatsDTO getPlayerStatsDTO(String type, PlayerProfile playerProfile, BuffSetId... buffSetIds) {
		PlayerProfile copy = playerProfile.copy();

		copy.setBuffs(buffSetIds);

		Spell spell = copy.getDamagingSpell();
		return getPlayerStatsDTO(type, copy, spell);
	}

	private PlayerStatsDTO getPlayerStatsDTO(String type, PlayerProfile playerProfile, Spell spell) {
		Attributes attributes = playerProfile.getStats();
		Snapshot snapshot = calculationService.getSnapshot(playerProfile, spell, null);

		return new PlayerStatsDTO(
				type,
				(int) attributes.getTotalSpellDamage(),
				(int) attributes.getTotalSpellDamage(SpellSchool.SHADOW),
				(int) attributes.getTotalSpellDamage(SpellSchool.FIRE),
				(int) attributes.getSpellHitRating(),
				snapshot.getTotalHit(),
				(int) attributes.getSpellCritRating(),
				snapshot.getTotalCrit(),
				(int) attributes.getSpellHasteRating(),
				snapshot.getTotalHaste(),
				(int) snapshot.getStamina(),
				(int) snapshot.getIntellect(),
				(int) snapshot.getSpirit()
		);
	}

	private SpecialAbilityStatsDTO getSpecialAbilityStatsDTO(PlayerProfile playerProfile, SpecialAbility specialAbility) {
		Attributes statEquivalent = calculationService.getAbilityEquivalent(specialAbility, playerProfile, null, null);

		return new SpecialAbilityStatsDTO(
				specialAbility.getLine(),
				specialAbility.toString(),
				statEquivalent.statString()
		);
	}
}
