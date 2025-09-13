package wow.character.model.build;

import lombok.AllArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.model.talent.TalentNameRank;

import java.util.*;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
@AllArgsConstructor
public class Talents implements EffectCollection, Copyable<Talents> {
	private final Map<TalentId, Talent> availableTalentsById;
	private final Map<TalentNameRank, Talent> availableTalentsByNameRank;
	private final Map<String, Talent> talentByName = new LinkedHashMap<>();

	public Talents(List<Talent> availableTalents) {
		this.availableTalentsById = new HashMap<>();
		this.availableTalentsByNameRank = new HashMap<>();

		for (var talent : availableTalents) {
			availableTalentsById.put(talent.getId(), talent);
			availableTalentsByNameRank.put(talent.getNameRank(), talent);
		}
	}

	public Collection<Talent> getList() {
		return List.copyOf(talentByName.values());
	}

	public Stream<Talent> getStream() {
		return talentByName.values().stream();
	}

	public boolean has(String name) {
		return talentByName.containsKey(name);
	}

	public boolean has(String name, int rank) {
		var talent = talentByName.get(name);
		if (rank != 0) {
			return talent != null && talent.getRank() == rank;
		} else {
			return talent == null;
		}
	}

	public void reset() {
		talentByName.clear();
	}

	public void loadFromTalentLink(TalentLink link) {
		reset();

		for (var talent : link.talents()) {
			enable(talent.name(), talent.rank());
		}
	}

	public void enable(TalentId talentId) {
		var talent = getTalent(talentId).orElseThrow();

		enable(talent);
	}

	public void enable(String name, int rank) {
		var talent = getTalent(name, rank).orElseThrow();

		enable(talent);
	}

	private void enable(Talent talent) {
		talentByName.put(talent.getName(), talent);
	}

	private Optional<Talent> getTalent(String name, int rank) {
		var nameRank = new TalentNameRank(name, rank);

		return Optional.ofNullable(availableTalentsByNameRank.get(nameRank));
	}

	private Optional<Talent> getTalent(TalentId talentId) {
		return Optional.ofNullable(availableTalentsById.get(talentId));
	}

	public void removeTalent(String name) {
		talentByName.remove(name);
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (var talent : talentByName.values()) {
			collector.addEffect(talent.getEffect());
		}
	}

	@Override
	public Talents copy() {
		var copy = new Talents(availableTalentsById, availableTalentsByNameRank);

		copy.talentByName.putAll(talentByName);
		return copy;
	}
}
