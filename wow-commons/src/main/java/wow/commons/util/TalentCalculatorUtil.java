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

		for (int i = 1; i <= talentString.length(); ++i) {
			int talentCalculatorPosition = i;
			int talentRank = talentString.charAt(i - 1) - '0';

			if (talentRank > 0) {
				TalentId talentId = TalentId.fromTalentCalculatorPosition(talentCalculatorPosition);
				TalentInfo talentInfo = spellDataRepository.getTalentInfo(talentId, talentRank).orElseThrow();
				result.put(talentId, talentInfo);
			}
		}

		return result;
	}

	private TalentCalculatorUtil() {}
}
