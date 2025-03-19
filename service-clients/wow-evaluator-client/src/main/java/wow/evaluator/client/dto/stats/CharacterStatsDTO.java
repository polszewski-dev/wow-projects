package wow.evaluator.client.dto.stats;

import wow.commons.model.spell.SpellSchool;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public record CharacterStatsDTO(
		String type,
		double sp,
		Map<SpellSchool, Integer> spellDamageBySchool,
		double hitRating,
		double hitPct,
		double critRating,
		double critPct,
		double hasteRating,
		double hastePct,
		double stamina,
		double intellect,
		double spirit,
		double maxHealth,
		double maxMana
) {
}
