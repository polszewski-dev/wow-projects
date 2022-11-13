package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
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
@AllArgsConstructor
@Getter
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
