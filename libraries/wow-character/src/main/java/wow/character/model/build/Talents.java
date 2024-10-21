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
	private final Map<TalentId, Talent> talentById = new EnumMap<>(TalentId.class);

	public Talents(List<Talent> availableTalents) {
		this.availableTalentsByIdByRank = new EnumMap<>(TalentId.class);

		for (Talent talent : availableTalents) {
			availableTalentsByIdByRank.computeIfAbsent(talent.getTalentId(), x -> new HashMap<>())
					.put(talent.getRank(), talent);
		}
	}

	@Override
	public Talents copy() {
		Talents copy = new Talents(availableTalentsByIdByRank);

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

	private Optional<Talent> getTalent(TalentId talentId, int talentRank) {
		return Optional.ofNullable(availableTalentsByIdByRank.getOrDefault(talentId, Map.of()).get(talentRank));
	}

	public void enableTalent(TalentId talentId, int talentRank) {
		Talent talent = getTalent(talentId, talentRank).orElseThrow();
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

	public Optional<Talent> getTalent(TalentId talentId) {
		return Optional.ofNullable(talentById.get(talentId));
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (Talent talent : talentById.values()) {
			collector.addEffect(talent.getEffect());
		}
	}

	public void removeTalent(TalentId talentId) {
		talentById.remove(talentId);
	}
}
