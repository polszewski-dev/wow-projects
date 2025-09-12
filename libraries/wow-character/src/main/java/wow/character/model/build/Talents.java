package wow.character.model.build;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;

import java.util.*;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Talents implements EffectCollection, Copyable<Talents> {
	private final Map<TalentId, Map<Integer, Talent>> availableTalentsByIdByRank;
	private final Map<Integer, Talent> availableTalentsById;
	private final Map<TalentId, Talent> talentById = new EnumMap<>(TalentId.class);

	public Talents(List<Talent> availableTalents) {
		this.availableTalentsByIdByRank = new EnumMap<>(TalentId.class);
		this.availableTalentsById = new HashMap<>();

		for (var talent : availableTalents) {
			availableTalentsByIdByRank.computeIfAbsent(talent.getTalentId(), x -> new HashMap<>())
					.put(talent.getRank(), talent);
			availableTalentsById.put(talent.getId(), talent);
		}
	}

	@Override
	public Talents copy() {
		var copy = new Talents(availableTalentsByIdByRank, availableTalentsById);

		copy.talentById.putAll(talentById);
		return copy;
	}

	public void reset() {
		talentById.clear();
	}

	public void loadFromTalentLink(TalentLink link) {
		reset();

		for (var talent : link.talents()) {
			enableTalent(talent.talentId(), talent.rank());
		}
	}

	public void enableTalent(TalentId talentId, int talentRank) {
		var talent = getTalent(talentId, talentRank).orElseThrow();
		enableTalent(talent);
	}

	public void enableTalent(int talentId) {
		var talent = getTalent(talentId).orElseThrow();
		enableTalent(talent);
	}

	private void enableTalent(Talent talent) {
		talentById.put(talent.getTalentId(), talent);
	}

	private Optional<Talent> getTalent(TalentId talentId, int talentRank) {
		return Optional.ofNullable(availableTalentsByIdByRank.getOrDefault(talentId, Map.of()).get(talentRank));
	}

	private Optional<Talent> getTalent(int talentId) {
		return Optional.ofNullable(availableTalentsById.get(talentId));
	}

	public Collection<Talent> getList() {
		return talentById.values();
	}

	public boolean hasTalent(TalentId talentId) {
		return talentById.containsKey(talentId);
	}

	public boolean hasTalent(TalentId talentId, int rank) {
		var talent = talentById.get(talentId);
		if (rank != 0) {
			return talent != null && talent.getRank() == rank;
		} else {
			return talent == null;
		}
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (var talent : talentById.values()) {
			collector.addEffect(talent.getEffect());
		}
	}

	public void removeTalent(TalentId talentId) {
		talentById.remove(talentId);
	}
}
