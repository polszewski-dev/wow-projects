package wow.commons.model.spells;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.effects.EffectId;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentTree;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
public class SpellInfo {
	private final SpellId spellId;
	private final TalentTree talentTree;
	private final SpellSchool spellSchool;
	private final Percent coeffDirect;
	private final Percent coeffDot;
	private final Duration cooldown;
	private final boolean ignoresGCD;
	private final TalentId requiredTalent;
	private final boolean bolt;
	private final Conversion conversion;
	private final EffectId requiredSpellEffect;
	private final EffectId spellEffectRemovedOnHit;
	private final EffectId bonusDamageIfUnderSpellEffect;
	private final List<Integer> dotScheme;
	private final Map<Integer, SpellRankInfo> ranks = new TreeMap<>();

	public SpellInfo(SpellId spellId, TalentTree talentTree, SpellSchool spellSchool, Percent coeffDirect, Percent coeffDot, Duration cooldown, boolean ignoresGCD, TalentId requiredTalent, boolean bolt, Conversion conversion, EffectId requiredSpellEffect, EffectId spellEffectRemovedOnHit, EffectId bonusDamageIfUnderSpellEffect, List<Integer> dotScheme) {
		this.spellId = spellId;
		this.talentTree = talentTree;
		this.spellSchool = spellSchool;
		this.coeffDirect = coeffDirect;
		this.coeffDot = coeffDot;
		this.cooldown = cooldown;
		this.ignoresGCD = ignoresGCD;
		this.requiredTalent = requiredTalent;
		this.bolt = bolt;
		this.conversion = conversion;
		this.requiredSpellEffect = requiredSpellEffect;
		this.spellEffectRemovedOnHit = spellEffectRemovedOnHit;
		this.bonusDamageIfUnderSpellEffect = bonusDamageIfUnderSpellEffect;
		this.dotScheme = dotScheme;
	}

	public SpellId getSpellId() {
		return spellId;
	}

	public TalentTree getTalentTree() {
		return talentTree;
	}

	public SpellSchool getSpellSchool() {
		return spellSchool;
	}

	public Percent getCoeffDirect() {
		return coeffDirect;
	}

	public Percent getCoeffDot() {
		return coeffDot;
	}

	public Duration getCooldown() {
		return cooldown;
	}

	public boolean isIgnoresGCD() {
		return ignoresGCD;
	}

	public TalentId getRequiredTalent() {
		return requiredTalent;
	}

	public boolean isBolt() {
		return bolt;
	}

	public Conversion getConversion() {
		return conversion;
	}

	public EffectId getRequiredSpellEffect() {
		return requiredSpellEffect;
	}

	public EffectId getSpellEffectRemovedOnHit() {
		return spellEffectRemovedOnHit;
	}

	public EffectId getBonusDamageIfUnderSpellEffect() {
		return bonusDamageIfUnderSpellEffect;
	}

	public List<Integer> getDotScheme() {
		return dotScheme;
	}

	public Map<Integer, SpellRankInfo> getRanks() {
		return ranks;
	}

	public boolean hasRanks() {
		return !ranks.isEmpty();
	}

	public int getHighestRank() {
		return ranks.keySet().stream().max(Integer::compareTo).orElseThrow(IllegalArgumentException::new);
	}

	public SpellRankInfo getRank(int rank) {
		return ranks.get(rank);
	}

	public SpellRankInfo getHighestRank(int level) {
		if (hasRanks()) {
			return ranks.values().stream()
					.filter(x -> x.getLevel() <= level)
					.max(Comparator.comparingInt(SpellRankInfo::getRank))
					.orElse(null);
		} else {
			throw new IllegalArgumentException("No ranks");
		}
	}

	public Percent getConversionPct(Conversion.From from, Conversion.To to) {
		return conversion != null && conversion.is(from, to) ? conversion.getPercent() : Percent.ZERO;
	}
}
