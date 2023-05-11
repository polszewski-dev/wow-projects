package wow.character.model.build;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Talents implements AttributeCollection, Copyable<Talents> {
	private final Map<TalentId, Map<Integer, Talent>> availableTalentsByIdByRank;
	private final Map<Integer, Map<Integer, Talent>> availableTalentsByPositionByRank;
	private final Map<TalentId, Talent> talentById = new EnumMap<>(TalentId.class);

	public Talents(List<Talent> availableTalents) {
		this.availableTalentsByIdByRank = new EnumMap<>(TalentId.class);
		this.availableTalentsByPositionByRank = new HashMap<>();

		for (Talent talent : availableTalents) {
			availableTalentsByIdByRank.computeIfAbsent(talent.getTalentId(), x -> new HashMap<>())
					.put(talent.getRank(), talent);

			availableTalentsByPositionByRank.computeIfAbsent(talent.getTalentCalculatorPosition(), x -> new HashMap<>())
					.put(talent.getRank(), talent);
		}
	}

	@Override
	public Talents copy() {
		Talents copy = new Talents(
				availableTalentsByIdByRank,
				availableTalentsByPositionByRank
		);

		copy.talentById.putAll(talentById);
		return copy;
	}

	public void reset() {
		talentById.clear();
	}

	public void loadFromTalentLink(String link) {
		reset();

		String talentStringStart = "?tal=";
		String talentString = link.substring(link.indexOf(talentStringStart) + talentStringStart.length());

		for (int position = 1; position <= talentString.length(); ++position) {
			int talentRank = talentString.charAt(position - 1) - '0';

			if (talentRank > 0) {
				enableTalent(position, talentRank);
			}
		}
	}

	private Optional<Talent> getTalent(int position, int talentRank) {
		return Optional.ofNullable(availableTalentsByPositionByRank.getOrDefault(position, Map.of()).get(talentRank));
	}

	private void enableTalent(int position, int talentRank) {
		Talent talent = getTalent(position, talentRank).orElseThrow();
		enableTalent(talent);
	}

	private void enableTalent(Talent talent) {
		talentById.put(talent.getTalentId(), talent);
	}

	public Collection<Talent> getList() {
		return talentById.values();
	}

	public boolean hasTalent(TalentId talentId) {
		return talentById.containsKey(talentId);
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		collector.addAttributes(talentById.values());
	}
}
