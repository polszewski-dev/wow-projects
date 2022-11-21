package wow.commons.util;

import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.SpellDataRepository;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-09-28
 */
public final class TalentCalculatorUtil {
	public static Map<TalentId, TalentInfo> parseFromLink(String link, SpellDataRepository spellDataRepository) {
		Map<TalentId, TalentInfo> result = new LinkedHashMap<>();

		String talentStringStart = "?tal=";
		String talentString = link.substring(link.indexOf(talentStringStart) + talentStringStart.length());

		for (int position = 1; position <= talentString.length(); ++position) {
			int talentRank = talentString.charAt(position - 1) - '0';

			if (talentRank > 0) {
				TalentInfo talentInfo = spellDataRepository.getTalentInfo(position, talentRank).orElseThrow();
				result.put(talentInfo.getTalentId(), talentInfo);
			}
		}

		return result;
	}

	private TalentCalculatorUtil() {}
}
