package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.buffs.Buff;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.Build;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.Snapshot;
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

import static wow.commons.model.unit.Build.BuffSet.*;

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
		Build build = playerProfile.getBuild();

		return List.of(
				getPlayerStatsDTO("Current buffs", playerProfile, playerProfile.getBuffs()),
				getEquipmentStats("Items", playerProfile),
				getPlayerStatsDTO("No buffs", playerProfile, List.of()),
				getPlayerStatsDTO("Self-buffs", playerProfile, build.getBuffs(SELF_BUFF)),
				getPlayerStatsDTO("Party buffs", playerProfile, build.getBuffs(SELF_BUFF, PARTY_BUFF)),
				getPlayerStatsDTO("Party buffs & consumes", playerProfile, build.getBuffs(SELF_BUFF, PARTY_BUFF, CONSUMES)),
				getPlayerStatsDTO("Raid buffs & consumes", playerProfile, build.getBuffs(SELF_BUFF, PARTY_BUFF, RAID_BUFF, CONSUMES))
		);
	}

	@GetMapping("special/{profileId}")
	public List<SpecialAbilityStatsDTO> getSpecialAbilityStats(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId);

		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(playerProfile)
				.solveAllLeaveAbilities();

		return attributes.getSpecialAbilities()
				.stream()
				.filter(x -> x.getLine() != null)
				.map(x -> getSpecialAbilityStatsDTO(playerProfile, attributes, x))
				.collect(Collectors.toList())
				;
	}

	private PlayerStatsDTO getEquipmentStats(String type, PlayerProfile playerProfile) {
		PlayerProfile copy = playerProfile.copy();

		Build emptyBuild = playerProfileService.getBuild(BuildIds.NONE);
		emptyBuild.setDamagingSpell(playerProfile.getDamagingSpell());

		copy.setBuild(emptyBuild);
		copy.setBuffs(List.of());

		return getPlayerStatsDTO(type, copy, copy.getDamagingSpell());
	}

	private PlayerStatsDTO getPlayerStatsDTO(String type, PlayerProfile playerProfile, List<Buff> buffs) {
		PlayerProfile copy = playerProfile.copy();

		copy.setBuffs(buffs);

		Spell spell = copy.getDamagingSpell();
		return getPlayerStatsDTO(type, copy, spell);
	}

	private PlayerStatsDTO getPlayerStatsDTO(String type, PlayerProfile playerProfile, Spell spell) {
		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(playerProfile)
				.solveAllLeaveAbilities();

		Snapshot snapshot = calculationService.getSnapshot(playerProfile, spell, attributes);

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

	private SpecialAbilityStatsDTO getSpecialAbilityStatsDTO(PlayerProfile playerProfile, Attributes attributes, SpecialAbility specialAbility) {
		Attributes statEquivalent = calculationService.getStatEquivalent(specialAbility, playerProfile, attributes);

		return new SpecialAbilityStatsDTO(
				specialAbility.getLine(),
				specialAbility.getAttributeModifier().toString(),
				statEquivalent.statString()
		);
	}
}
