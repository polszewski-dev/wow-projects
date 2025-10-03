package wow.character.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.build.TalentLink;
import wow.character.model.build.TalentLinkType;
import wow.character.model.build.Talents;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentNameRank;
import wow.commons.repository.spell.TalentRepository;
import wow.commons.util.parser.ParsedMultipleValues;
import wow.commons.util.parser.ParserUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static wow.character.util.TalentLinkParser.TalentFinder;

/**
 * User: POlszewski
 * Date: 2023-12-10
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TalentLinkParser {
	private final String link;
	private final TalentRepository talentRepository;
	private final Talents talents;

	private TalentLinkType type;
	private GameVersionId gameVersionId;
	private CharacterClassId characterClassId;
	private String talentListString;
	private List<TalentNameRank> talentList;

	public static TalentLink parse(String link, TalentRepository talentRepository) {
		var parser = new TalentLinkParser(link, talentRepository, null);

		return parser.parse();
	}

	public static TalentLink parse(String link, Talents talents) {
		var parser = new TalentLinkParser(link, null, talents);

		return parser.parse();
	}

	private TalentLink parse() {
		parsePrefix();
		parseTalentList();

		return new TalentLink(
				link,
				type,
				gameVersionId,
				characterClassId,
				talentList
		);
	}

	private void parsePrefix() {
		var parseResult = ParserUtil.parseMultipleValues("https://www\\.wowhead\\.com/([^/]+)/talent-calc/([^/]+)/?(.*)", link);

		if (!parseResult.isEmpty()) {
			setPrefix(TalentLinkType.WOWHEAD, parseResult);
			return;
		}

		parseResult = ParserUtil.parseMultipleValues("https://legacy-wow\\.com/(.+)-talents/(.+)-talents/\\?tal=(.+)", link);

		if (!parseResult.isEmpty()) {
			setPrefix(TalentLinkType.LEGACY_WOW, parseResult);
			return;
		}

		throw new IllegalArgumentException("Unsupported link format: " + link);
	}

	private void setPrefix(TalentLinkType type, ParsedMultipleValues parseResult) {
		this.type = type;
		this.gameVersionId = parseGameVersion(parseResult);
		this.characterClassId = parseCharacterClass(parseResult);
		this.talentListString = parseResult.get(2);
	}

	private void parseTalentList() {
		var talentListParser = getTalentListParser();

		talentListParser.parseTalentList(talentListString);

		this.talentList = talentListParser.getTalents();
	}

	private TalentListParser getTalentListParser() {
		var talentFinder = getTalentFinder();

		return switch (type) {
			case WOWHEAD -> new WowheadTalentListParser(talentFinder);
			case LEGACY_WOW -> new LegacyWowTalentListParser(talentFinder);
		};
	}

	private static GameVersionId parseGameVersion(ParsedMultipleValues parseResult) {
		String gameVersionStr = parseResult.get(0);

		if (gameVersionStr.equalsIgnoreCase("classic")) {
			return GameVersionId.VANILLA;
		}

		return GameVersionId.parse(gameVersionStr);
	}

	private static CharacterClassId parseCharacterClass(ParsedMultipleValues parseResult) {
		return CharacterClassId.parse(parseResult.get(1));
	}

	interface TalentFinder {
		Optional<Talent> getTalent(int talentCalculatorPosition, int rank);

		List<Talent> getAvailableTalents();
	}

	private TalentFinder getTalentFinder() {
		if (talentRepository != null) {
			return getTalentFinder(talentRepository);
		} else {
			return getTalentFinder(talents);
		}
	}

	private TalentFinder getTalentFinder(TalentRepository talentRepository) {
		return new TalentFinder() {
			@Override
			public Optional<Talent> getTalent(int talentCalculatorPosition, int rank) {
				return talentRepository.getTalent(
						characterClassId,
						talentCalculatorPosition,
						rank,
						gameVersionId.getEarliestPhase()
				);
			}

			@Override
			public List<Talent> getAvailableTalents() {
				return talentRepository.getAvailableTalents(characterClassId, gameVersionId.getEarliestPhase());
			}
		};
	}

	private TalentFinder getTalentFinder(Talents talents) {
		return new TalentFinder() {
			@Override
			public Optional<Talent> getTalent(int talentCalculatorPosition, int rank) {
				return talents.getTalent(
						talentCalculatorPosition,
						rank
				);
			}

			@Override
			public List<Talent> getAvailableTalents() {
				return talents.getAvailableTalents();
			}
		};
	}
}

@RequiredArgsConstructor
abstract class TalentListParser {
	@Getter
	private final List<TalentNameRank> talents = new ArrayList<>();
	protected final TalentFinder talentFinder;

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
		var talent = talentFinder.getTalent(position, talentRank).orElseThrow();

		talents.add(new TalentNameRank(talent.getName(), talentRank));
	}
}

/*
	https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025
*/

class WowheadTalentListParser extends TalentListParser {
	WowheadTalentListParser(TalentFinder talentFinder) {
		super(talentFinder);
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

		var talents = talentFinder.getAvailableTalents();

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

class LegacyWowTalentListParser extends TalentListParser {
	LegacyWowTalentListParser(TalentFinder talentFinder) {
		super(talentFinder);
	}

	@Override
	void parseTalentList(String talentListString) {
		addTalents(talentListString, 1);
	}
}
