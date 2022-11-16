package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.effects.EffectId;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentTree;

import java.util.*;

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

	public Optional<SpellRankInfo> getRank(int rank) {
		return Optional.ofNullable(ranks.get(rank));
	}

	public Optional<Integer> getHighestRank() {
		return ranks.keySet().stream().max(Integer::compareTo);
	}

	public Optional<SpellRankInfo> getHighestRank(int level) {
		if (!hasRanks()) {
			return Optional.empty();
		}
		return ranks.values().stream()
				.filter(x -> x.getLevel() <= level)
				.max(Comparator.comparingInt(SpellRankInfo::getRank));
	}

	public Percent getConversionPct(Conversion.From from, Conversion.To to) {
		return conversion != null && conversion.is(from, to) ? conversion.getPercent() : Percent.ZERO;
	}
}
