package wow.minmax.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.buffs.Buff;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.unit.CombatRatingInfo;
import wow.commons.repository.PVERepository;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellStatistics;
import wow.minmax.converter.dto.PlayerSpellStatsConverter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.model.Spell;
import wow.minmax.model.dto.PlayerStatsDTO;
import wow.minmax.model.dto.SpecialAbilityStatsDTO;
import wow.minmax.model.dto.SpellStatsDTO;
import wow.minmax.service.CalculationService;
import wow.minmax.service.PlayerProfileService;
import wow.minmax.service.SpellService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static wow.commons.model.attributes.primitive.DoubleAttributeId.*;
import static wow.minmax.model.Build.BuffSet.*;

/**
 * User: POlszewski
 * Date: 2021-12-31
 */
@RestController
@RequestMapping("api/v1/stats")
@AllArgsConstructor
public class StatsController {
	private final PlayerProfileService playerProfileService;
	private final SpellService spellService;
	private final CalculationService calculationService;
	private final PVERepository pveRepository;
	private final PlayerSpellStatsConverter playerSpellStatsConverter;

	@GetMapping("spell/{profileId}")
	public List<SpellStatsDTO> getSpellStats(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId).readOnlyCopy();

		Spell[] spells = {
				spellService.getSpell(SpellId.SHADOW_BOLT),
				spellService.getSpell(SpellId.CURSE_OF_DOOM),
				spellService.getSpell(SpellId.CURSE_OF_AGONY),
				spellService.getSpell(SpellId.CORRUPTION),
				spellService.getSpell(SpellId.IMMOLATE),
				spellService.getSpell(SpellId.SHADOWBURN),
				spellService.getSpell(SpellId.SEED_OF_CORRUPTION_DIRECT)
		};

		List<SpellStatsDTO> result = new ArrayList<>();

		for (Spell spell : spells) {
			SpellStatistics spellStatistics = calculationService.getSpellStatistics(playerProfile, spell);
			double hitSpEqv = calculationService.getSpEquivalent(SPELL_HIT_RATING, 10, playerProfile, spell);
			double critSpEqv = calculationService.getSpEquivalent(SPELL_CRIT_RATING, 10, playerProfile, spell);
			double hasteSpEqv = calculationService.getSpEquivalent(SPELL_HASTE_RATING, 10, playerProfile, spell);
			PlayerSpellStats playerSpellStats = new PlayerSpellStats(playerProfile, spellStatistics, hitSpEqv, critSpEqv, hasteSpEqv);

			result.add(playerSpellStatsConverter.convert(playerSpellStats));
		}

		return result;
	}

	@GetMapping("player/{profileId}")
	public List<PlayerStatsDTO> getPlayerStats(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId).readOnlyCopy();

		return List.of(
				getEquipmentStats(playerProfile),
				getBuffedPlayerStats("No buffs", playerProfile, List.of()),
				getBuffedPlayerStats("Self-buffs", playerProfile, playerProfile.getBuild().getBuffs(SELF_BUFF)),
				getBuffedPlayerStats("Party buffs", playerProfile, playerProfile.getBuild().getBuffs(SELF_BUFF, PARTY_BUFF)),
				getBuffedPlayerStats("Party buffs & consumes", playerProfile, playerProfile.getBuild().getBuffs(SELF_BUFF, PARTY_BUFF, CONSUMES))
		);
	}

	@GetMapping("special/{profileId}")
	public List<SpecialAbilityStatsDTO> getSpecialAbilityStats(
			@PathVariable("profileId") UUID profileId
	) {
		PlayerProfile playerProfile = playerProfileService.getPlayerProfile(profileId)
														  .readOnlyCopy();

		Attributes attributes = AttributeEvaluator.of()
				.addAttributes(playerProfile)
				.solveAllLeaveAbilities()
				.getAttributes();

		return attributes.getSpecialAbilities()
				.stream()
				.filter(x -> x.getLine() != null)
				.map(x -> new SpecialAbilityStatsDTO(x.getLine(), x.getAttributeModifier().toString(), getStatEquivalent(x, playerProfile)))
				.collect(Collectors.toList())
				;
	}

	private String getStatEquivalent(SpecialAbility specialAbility, PlayerProfile playerProfile) {
		AttributeEvaluator attributeEvaluator = AttributeEvaluator.of(playerProfile.getDamagingSpell().getSpellInfo())
				.addAttributes(playerProfile);

		return specialAbility.getStatEquivalent(
				calculationService.getPlayerStatsProvider(playerProfile, playerProfile.getDamagingSpell(), attributeEvaluator))
				.statString();
	}

	private PlayerStatsDTO getEquipmentStats(PlayerProfile playerProfile) {
		Equipment equipment = playerProfile.getEquipment();

		Attributes attributes = getUnsolvedAttributes(equipment);

		CombatRatingInfo cr = pveRepository.getCombatRatings(playerProfile.getLevel()).orElseThrow();

		return new PlayerStatsDTO(
				"Items",
				(int) attributes.getTotalSpellDamage(),
				(int) attributes.getTotalSpellDamage(SpellSchool.SHADOW),
				(int) attributes.getTotalSpellDamage(SpellSchool.FIRE),
				(int) attributes.getSpellHitRating(),
				attributes.getSpellHitRating() / cr.getSpellHit(),
				(int) attributes.getSpellCritRating(),
				attributes.getSpellCritRating() / cr.getSpellCrit(),
				(int) attributes.getSpellHasteRating(),
				attributes.getSpellHasteRating() / cr.getSpellHaste(),
				(int) attributes.getStamina(),
				(int) attributes.getIntellect(),
				(int) attributes.getSpirit()
		);
	}

	private PlayerStatsDTO getBuffedPlayerStats(String type, PlayerProfile playerProfile, List<Buff> buffs) {
		PlayerProfile buffedPlayerProfile = playerProfile.copy();

		buffedPlayerProfile.setBuffs(buffs);

		Spell spell = buffedPlayerProfile.getDamagingSpell();
		Attributes attributes = getUnsolvedAttributes(buffedPlayerProfile);
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

	private Attributes getUnsolvedAttributes(AttributeCollection collection) {
		return AttributeEvaluator.of(/*None*/null, null, /*None*/null, /*None*/null, /*None*/null)//TODO
				.addAttributes(collection)
				.solveAllLeaveAbilities()
				.getAttributes();
	}
}
