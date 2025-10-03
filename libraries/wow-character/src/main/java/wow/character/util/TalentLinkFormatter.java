package wow.character.util;

import lombok.RequiredArgsConstructor;
import wow.character.model.build.Talents;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentTree;

import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.*;

/**
 * User: POlszewski
 * Date: 2025-09-27
 */
@RequiredArgsConstructor
public class TalentLinkFormatter {
	private final Talents talents;
	private final GameVersionId gameVersionId;
	private final CharacterClassId characterClassId;

	public String format() {
		var prefix = "https://www.wowhead.com/%s/talent-calc/%s".formatted(
				formatGameVersion(), characterClassId.name().toLowerCase()
		);

		var talentStringsByTree = getTalentStringsByTree();

		var suffix = talentStringsByTree.keySet().stream()
				.sorted()
				.map(talentStringsByTree::get)
				.collect(joining("-"))
				.replaceAll("-+$", "");

		if (suffix.isEmpty()) {
			return prefix;
		}

		return prefix + "/" + suffix;
	}

	private String formatGameVersion() {
		if (gameVersionId == GameVersionId.VANILLA) {
			return "classic";
		}
		return gameVersionId.name().toLowerCase();
	}

	private Map<TalentTree, List<String>> getAvailableTalentNamesByTree() {
		var availableTalents = talents.getAvailableTalents();

		return availableTalents.stream().collect(groupingBy(
				Talent::getTalentTree,
				collectingAndThen(
						toList(),
						list -> list.stream()
								.map(x -> new NamePosition(x.getName(), x.getTalentCalculatorPosition()))
								.distinct()
								.sorted(comparingInt(NamePosition::position))
								.map(NamePosition::name)
								.toList()
				)
		));
	}

	private Map<TalentTree, String> getTalentStringsByTree() {
		return getAvailableTalentNamesByTree().entrySet().stream().collect(toMap(
				Map.Entry::getKey,
				x -> getRankString(x.getValue())
		));
	}

	private String getRankString(List<String> availableTalentNames) {
		return availableTalentNames.stream()
				.map(talents::getRank)
				.map(Object::toString)
				.collect(joining())
				.replaceAll("0+$", "");
	}

	private record NamePosition(String name, int position) {}
}
