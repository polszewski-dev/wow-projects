package wow.character.util;

import wow.character.model.build.TalentLink;
import wow.character.model.build.TalentLinkType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentNameRank;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.parser.ParsedMultipleValues;
import wow.commons.util.parser.ParserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2023-12-10
 */
public abstract class TalentLinkParser {
	private final String link;
	private final TalentLinkType type;
	protected final GameVersionId gameVersionId;
	protected final CharacterClassId characterClassId;
	private final List<TalentNameRank> talents = new ArrayList<>();
	protected final TalentRepository talentRepository;

	protected final ParsedMultipleValues parseResult;

	TalentLinkParser(String link, TalentLinkType type, ParsedMultipleValues parseResult, TalentRepository talentRepository) {
		this.link = link;
		this.type = type;
		this.gameVersionId = parseGameVersion(parseResult);
		this.characterClassId = CharacterClassId.parse(parseResult.get(1));
		this.parseResult = parseResult;
		this.talentRepository = talentRepository;
	}

	private static GameVersionId parseGameVersion(ParsedMultipleValues parseResult) {
		String gameVersionStr = parseResult.get(0);

		if (gameVersionStr.equalsIgnoreCase("classic")) {
			return GameVersionId.VANILLA;
		}

		return GameVersionId.parse(gameVersionStr);
	}

	public static TalentLink parse(String link, TalentRepository talentRepository) {
		var parser = getParser(link, talentRepository);
		return parser.parse();
	}

	private static TalentLinkParser getParser(String link, TalentRepository talentRepository) {
		var parseResult = ParserUtil.parseMultipleValues("https://www\\.wowhead\\.com/([^/]+)/talent-calc/([^/]+)/?(.*)", link);

		if (!parseResult.isEmpty()) {
			return new WowheadLinkParser(link, parseResult, talentRepository);
		}

		parseResult = ParserUtil.parseMultipleValues("https://legacy-wow\\.com/(.+)-talents/(.+)-talents/\\?tal=(.+)", link);

		if (!parseResult.isEmpty()) {
			return new LegacyWowLinkParser(link, parseResult, talentRepository);
		}

		throw new IllegalArgumentException("Unsupported link format: " + link);
	}

	private TalentLink parse() {
		parseTalentList(parseResult.get(2));

		return new TalentLink(
				link, type, gameVersionId, characterClassId, talents
		);
	}

	abstract void parseTalentList(String talentListString);

	void addTalents(String talentRankString, int startPosition) {
		for (int idx = 0; idx < talentRankString.length(); ++idx) {
			int talentRank = talentRankString.charAt(idx) - '0';

			if (talentRank > 0) {
				addTalent(startPosition + idx, talentRank);
			}
		}
	}

	private void addTalent(int position, int talentRank) {
		var phaseId = gameVersionId.getEarliestPhase();
		var talent = talentRepository.getTalent(characterClassId, position, talentRank, phaseId).orElseThrow();

		talents.add(new TalentNameRank(talent.getName(), talentRank));
	}
}

/*
	https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025
*/

class WowheadLinkParser extends TalentLinkParser {
	WowheadLinkParser(String link, ParsedMultipleValues parseResult, TalentRepository talentRepository) {
		super(link, TalentLinkType.WOWHEAD, parseResult, talentRepository);
	}

	@Override
	void parseTalentList(String talentListString) {
		var treeSeparator = '-';
		var secondTreeIdx = talentListString.indexOf(treeSeparator);
		var thirdTreeIdx = talentListString.indexOf(treeSeparator, secondTreeIdx + 1);

		var firstTree = "";
		var secondTree = "";
		var thirdTree = "";

		if (secondTreeIdx >= 0) {
			firstTree = talentListString.substring(0, secondTreeIdx);

			if (thirdTreeIdx >= 0) {
				secondTree = talentListString.substring(secondTreeIdx + 1, thirdTreeIdx);
				thirdTree = talentListString.substring(thirdTreeIdx + 1);
			} else {
				secondTree = talentListString.substring(secondTreeIdx + 1);
			}
		} else {
			firstTree = talentListString;
		}

		var talents = talentRepository.getAvailableTalents(characterClassId, gameVersionId.getEarliestPhase());

		var talentTreeToPosition = talents.stream().collect(Collectors.toMap(
				Talent::getTalentTree,
				Talent::getTalentCalculatorPosition,
				Math::min
		));

		var trees = talentTreeToPosition.keySet().stream().sorted().toList();

		var firstPosition = talentTreeToPosition.get(trees.get(0));
		var secondPosition = talentTreeToPosition.get(trees.get(1));
		var thirdPosition = talentTreeToPosition.get(trees.get(2));

		addTalents(firstTree, firstPosition);
		addTalents(secondTree, secondPosition);
		addTalents(thirdTree, thirdPosition);
	}
}

/*
	https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000002050130133200100000000555000512210013030250
*/

class LegacyWowLinkParser extends TalentLinkParser {
	LegacyWowLinkParser(String link, ParsedMultipleValues parseResult, TalentRepository talentRepository) {
		super(link, TalentLinkType.LEGACY_WOW, parseResult, talentRepository);
	}

	@Override
	void parseTalentList(String talentListString) {
		addTalents(talentListString, 1);
	}
}
