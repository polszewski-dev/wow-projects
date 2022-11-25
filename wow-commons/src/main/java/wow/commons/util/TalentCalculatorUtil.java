package wow.commons.util;

import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.repository.SpellDataRepository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-09-28
 */
public final class TalentCalculatorUtil {
	public static Map<TalentId, Talent> parseFromLink(String link, SpellDataRepository spellDataRepository) {
		Map<TalentId, Talent> result = new LinkedHashMap<>();

		String talentStringStart = "?tal=";
		String talentString = link.substring(link.indexOf(talentStringStart) + talentStringStart.length());

		for (int position = 1; position <= talentString.length(); ++position) {
			int talentRank = talentString.charAt(position - 1) - '0';

			if (talentRank > 0) {
				Talent talent = spellDataRepository.getTalent(position, talentRank).orElseThrow();
				result.put(talent.getTalentId(), talent);
			}
		}

		return result;
	}

	private TalentCalculatorUtil() {}
}
