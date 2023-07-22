package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Rotation;
import wow.character.model.character.Buffs;
import wow.character.model.character.Character;
import wow.character.model.snapshot.*;
import wow.character.service.CharacterCalculationService;
import wow.character.service.CharacterService;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.special.OnUseAbility;
import wow.commons.model.attributes.complex.special.ProcAbility;
import wow.commons.model.attributes.complex.special.SpecialAbility;
import wow.commons.model.attributes.complex.special.TalentProcAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffCategory;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellSchool;
import wow.commons.model.talents.TalentId;
import wow.commons.util.AttributesBuilder;
import wow.minmax.model.*;
import wow.minmax.repository.ProcInfoRepository;
import wow.minmax.service.CalculationService;
import wow.minmax.service.impl.enumerators.RotationAbilityEquivalentCalculator;
import wow.minmax.service.impl.enumerators.RotationDpsCalculator;
import wow.minmax.service.impl.enumerators.RotationStatsCalculator;
import wow.minmax.service.impl.enumerators.StatEquivalentFinder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.*;
import static wow.minmax.service.CalculationService.EquivalentMode.ADDITIONAL;
import static wow.minmax.service.CalculationService.EquivalentMode.REPLACEMENT;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
@Service
@AllArgsConstructor
public class CalculationServiceImpl implements CalculationService {
	private final CharacterCalculationService characterCalculationService;
	private final CharacterService characterService;
	private final ProcInfoRepository procInfoRepository;

	@Override
	public Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, Character character) {
		return getDpsStatEquivalent(attributesToFindEquivalent, targetStat, mode, character, character.getRotation(), character.getStats());
	}

	@Override
	public Attributes getDpsStatEquivalent(
			Attributes attributesToFindEquivalent,
			PrimitiveAttributeId targetStat,
			EquivalentMode mode,
			Character character,
			Rotation rotation,
			Attributes totalStats
	) {
		var finder = new StatEquivalentFinder(
				getBaseStats(mode, totalStats, attributesToFindEquivalent),
				getTargetDps(mode, totalStats, attributesToFindEquivalent, character, rotation),
				targetStat,
				character,
				rotation,
				this
		);

		return finder.getDpsStatEquivalent();
	}

	private Attributes getBaseStats(EquivalentMode mode, Attributes totalStats, Attributes attributesToFindEquivalent) {
		return switch (mode) {
			case REPLACEMENT -> AttributesBuilder.removeAttributes(totalStats, attributesToFindEquivalent);
			case ADDITIONAL -> totalStats;
		};
	}

	private Attributes getTargetStats(EquivalentMode mode, Attributes totalStats, Attributes attributesToFindEquivalent) {
		return switch (mode) {
			case REPLACEMENT -> totalStats;
			case ADDITIONAL -> AttributesBuilder.addAttributes(totalStats, attributesToFindEquivalent);
		};
	}

	private double getTargetDps(EquivalentMode mode, Attributes totalStats, Attributes attributesToFindEquivalent, Character character, Rotation rotation) {
		Attributes targetStats = getTargetStats(mode, totalStats, attributesToFindEquivalent);
		return getRotationDps(character, rotation, targetStats);
	}

	@Override
	public Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character) {
		return getAbilityEquivalent(specialAbility, character, character.getRotation(), character.getStats());
	}

	@Override
	public Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character, Rotation rotation, Attributes totalStats) {
		var calculator = new RotationAbilityEquivalentCalculator(
				character,
				rotation,
				AttributesBuilder.removeAttributes(totalStats, Attributes.of(specialAbility)),
				this
		);
		calculator.calculate();
		return calculator.getAbilityEquivalent(specialAbility);
	}

	@Override
	public Attributes getTalentEquivalent(
			TalentId talentId,
			PrimitiveAttributeId targetStat,
			Character character
	) {
		Character copy = character.copy();
		copy.getTalents().removeTalent(talentId);

		characterService.updateAfterRestrictionChange(copy);

		Attributes baseStats = copy.getStats();
		double targetDps = getRotationDps(character, character.getRotation(), character.getStats());

		var finder = new StatEquivalentFinder(
				baseStats, targetDps, targetStat, copy, copy.getRotation(), this
		);

		return finder.getDpsStatEquivalent();
	}

	@Override
	public double getRotationDps(Character character, Rotation rotation) {
		return getRotationDps(character, rotation, character.getStats());
	}

	@Override
	public double getRotationDps(Character character, Rotation rotation, Attributes totalStats) {
		var calculator = new RotationDpsCalculator(character, rotation, totalStats, this);
		calculator.calculate();
		return calculator.getDps();
	}

	@Override
	public RotationStats getRotationStats(Character character, Rotation rotation) {
		var calculator = new RotationStatsCalculator(character, rotation, character.getStats(), this);
		calculator.calculate();
		return calculator.getStats();
	}

	@Override
	public Snapshot getSnapshot(Character character, Attributes totalStats) {
		return getSnapshot(character, null, totalStats);
	}

	@Override
	public Snapshot getSnapshot(Character character, Spell spell, Attributes totalStats) {
		Snapshot snapshot = characterCalculationService.createSnapshot(character, spell, totalStats);

		characterCalculationService.advanceSnapshot(snapshot, SnapshotState.SECONDARY_STATS);

		if (spell == null) {
			return snapshot;
		}

		characterCalculationService.advanceSnapshot(snapshot, SnapshotState.CAST_TIME);

		boolean recalculate = solveAbilities(snapshot);

		if (recalculate) {
			snapshot.setState(SnapshotState.INITIAL);
		}

		characterCalculationService.advanceSnapshot(snapshot, SnapshotState.COMPLETE);

		return snapshot;
	}

	private boolean solveAbilities(Snapshot snapshot) {
		AccumulatedSpellStats stats = snapshot.getStats();
		List<SpecialAbility> specialAbilities = stats.getAttributes().getSpecialAbilities();

		if (specialAbilities.isEmpty()) {
			return false;
		}

		for (SpecialAbility specialAbility : specialAbilities) {
			Attributes statEquivalent = getStatEquivalent(specialAbility, snapshot);
			stats.accumulatePrimitiveAttributes(statEquivalent.getPrimitiveAttributes());
		}

		return true;
	}

	@Override
	public Attributes getStatEquivalent(SpecialAbility specialAbility, Snapshot snapshot) {
		if (specialAbility instanceof OnUseAbility ability) {
			return ability.equivalent();
		} else if (specialAbility instanceof ProcAbility ability) {
			return getProcStatEquivalent(ability, snapshot);
		} else if (specialAbility instanceof TalentProcAbility ability) {
			return getTalentProcStatEquivalent(ability, snapshot);
		} else {
			return specialAbility.attributes();
		}
	}

	private Attributes getProcStatEquivalent(ProcAbility ability, Snapshot snapshot) {
		double procChance = getProcChance(ability, snapshot);

		if (procChance == 0) {
			return Attributes.EMPTY;
		}

		double uptime = getProcUptime(ability, snapshot, procChance);

		return ability.attributes().scale(uptime);
	}

	private double getProcChance(ProcAbility ability, Snapshot snapshot) {
		double hitChance = snapshot.getHitChance();
		double critChance = snapshot.getCritChance();

		return ability.event().getProcChance(hitChance, critChance);
	}

	private double getProcUptime(ProcAbility ability, Snapshot snapshot, double procChance) {
		double duration = ability.duration().getSeconds();
		double cooldown = ability.cooldown().getSeconds();
		double castTime = snapshot.getEffectiveCastTime();

		return getProcUptime(procChance, duration, cooldown, castTime);
	}

	private double getProcUptime(double procChance, double duration, double internalCooldown, double castTime) {
		double procChancePct = 100 * procChance;

		double p = procChancePct * ProcInfo.CHANCE_RESOLUTION;
		double t = castTime * ProcInfo.CAST_TIME_RESOLUTION;

		double modP = p % 1;
		double modT = t % 1;

		return getProcUptime((int) p, (int) t, modP, modT, (int) duration, (int) internalCooldown);
	}

	private double getProcUptime(int p, int t, double modP, double modT, int duration, int internalCooldown) {
		double procUptime0 = procInfoRepository.getAverageUptime(p, t, duration, internalCooldown);

		if (modP != 0 && modT != 0) {
			double procUptime1 = procInfoRepository.getAverageUptime(p + 1, t, duration, internalCooldown);
			double procUptime2 = procInfoRepository.getAverageUptime(p, t + 1, duration, internalCooldown);
			double procUptime3 = procInfoRepository.getAverageUptime(p + 1, t + 1, duration, internalCooldown);

			double v1 = interpolate(procUptime0, procUptime1, modP);
			double v2 = interpolate(procUptime2, procUptime3, modP);

			return interpolate(v1, v2, modT);
		}

		if (modP != 0) {
			double procUptime1 = procInfoRepository.getAverageUptime(p + 1, t, duration, internalCooldown);

			return interpolate(procUptime0, procUptime1, modP);
		}

		if (modT != 0) {
			double procUptime1 = procInfoRepository.getAverageUptime(p, t + 1, duration, internalCooldown);

			return interpolate(procUptime0, procUptime1, modT);
		}

		return procUptime0;
	}

	private static double interpolate(double v0, double v1, double t) {
		return v0 + (v1 - v0) * t;
	}

	private Attributes getTalentProcStatEquivalent(TalentProcAbility ability, Snapshot snapshot) {
		double critChance = snapshot.getCritChance();
		double extraCritCoeff = getExtraCritCoeff(ability, critChance);
		if (extraCritCoeff == 0) {
			return Attributes.EMPTY;
		}
		return Attributes.of(CRIT_COEFF_PCT, extraCritCoeff, ability.condition());
	}

	private double getExtraCritCoeff(TalentProcAbility ability, double critChance) {
		int rank = ability.effectId().getRank();
		double c = critChance;
		double n = 1 - c;
		return rank * 0.04 * (2 * c + n) * (1 + n + n * n + n * n * n);
	}

	@Override
	public SpellStats getSpellStats(Character character, Spell spell) {
		Snapshot snapshot = getSnapshot(character, spell, character.getStats());
		SpellStatistics spellStatistics = snapshot.getSpellStatistics(CritMode.AVERAGE, true);
		SpellStatEquivalents statEquivalents = getStatEquivalents(character, spell);
		return new SpellStats(character, spellStatistics, statEquivalents);
	}

	private SpellStatEquivalents getStatEquivalents(Character character, Spell spell) {
		PrimitiveAttributeId hit;
		PrimitiveAttributeId crit;
		PrimitiveAttributeId haste;

		if (character.getGameVersion().isCombatRatings()) {
			hit = SPELL_HIT_RATING;
			crit = SPELL_CRIT_RATING;
			haste = SPELL_HASTE_RATING;
		} else {
			hit = SPELL_HIT_PCT;
			crit = SPELL_CRIT_PCT;
			haste = SPELL_HASTE_PCT;
		}

		double amount = character.getGameVersion().getEvivalentAmount();
		double hitSpEqv = getSpEquivalent(hit, amount, character, spell);
		double critSpEqv = getSpEquivalent(crit, amount, character, spell);
		double hasteSpEqv = getSpEquivalent(haste, amount, character, spell);
		return new SpellStatEquivalents(hitSpEqv, critSpEqv, hasteSpEqv);
	}

	private double getSpEquivalent(PrimitiveAttributeId attributeId, double amount, Character character, Spell spell) {
		return getDpsStatEquivalent(
				Attributes.of(attributeId, amount),
				SPELL_POWER,
				ADDITIONAL,
				character,
				Rotation.onlyFiller(spell),
				character.getStats()
		).getSpellPower();
	}

	@Override
	public CharacterStats getCurrentStats(Character character) {
		return getStats(character, character.getStats());
	}

	@Override
	public CharacterStats getStats(Character character, BuffCategory... buffCategories) {
		Character copy = character.copy();
		copy.setBuffs(getFilteredBuffs(character.getBuffs(), buffCategories));
		copy.getTargetEnemy().setDebuffs(getFilteredBuffs(character.getTargetEnemy().getDebuffs(), buffCategories));
		return getStats(copy, copy.getStats());
	}

	private List<Buff> getFilteredBuffs(Buffs buffs, BuffCategory[] buffCategories) {
		return buffs.getList().stream()
				.filter(x -> Stream.of(buffCategories).anyMatch(y -> x.getCategories().contains(y)))
				.toList();
	}

	@Override
	public CharacterStats getEquipmentStats(Character character) {
		return getStats(character, character.getEquipment().getStats());
	}

	private CharacterStats getStats(Character character, Attributes totalStats) {
		Snapshot snapshot = getSnapshot(character, totalStats);

		return new CharacterStats(
				totalStats.getTotalSpellDamage(),
				getSpellDamageBySchool(totalStats),
				totalStats.getSpellHitRating(),
				snapshot.getTotalHit(),
				totalStats.getSpellCritRating(),
				snapshot.getTotalCrit(),
				totalStats.getSpellHasteRating(),
				snapshot.getTotalHaste(),
				snapshot.getStamina(),
				snapshot.getIntellect(),
				snapshot.getSpirit(),
				snapshot.getMaxHealth(),
				snapshot.getMaxMana()
		);
	}

	private Map<SpellSchool, Double> getSpellDamageBySchool(Attributes totalStats) {
		return Stream.of(SpellSchool.values())
				.collect(Collectors.toMap(x -> x, totalStats::getTotalSpellDamage));
	}

	@Override
	public SpecialAbilityStats getSpecialAbilityStats(Character character, SpecialAbility specialAbility) {
		Attributes statEquivalent = getAbilityEquivalent(specialAbility, character);
		Attributes spEquivalent = getDpsStatEquivalent(Attributes.of(specialAbility), SPELL_POWER, REPLACEMENT, character);

		return new SpecialAbilityStats(
				specialAbility,
				statEquivalent,
				spEquivalent.getSpellPower()
		);
	}
}
