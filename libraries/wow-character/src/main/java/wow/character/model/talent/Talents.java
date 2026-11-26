package wow.character.model.talent;

import wow.character.model.character.Character;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.character.util.TalentLinkFormatter;
import wow.character.util.TalentLinkParser;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.model.talent.TalentNameRank;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-12-20
 */
public class Talents implements EffectCollection {
	private final Character owner;
	private final Map<TalentId, Talent> availableTalentsById;
	private final Map<TalentNameRank, Talent> availableTalentsByNameRank;
	private final Map<CalcPositionKey, Talent> availableTalentsByCalcPosition;
	private final Map<String, Talent> talentByName = new LinkedHashMap<>();

	private record CalcPositionKey(int talentCalculatorPosition, int rank) {}

	public Talents(Character owner, List<Talent> availableTalents) {
		this.owner = owner;
		this.availableTalentsById = new HashMap<>();
		this.availableTalentsByNameRank = new HashMap<>();
		this.availableTalentsByCalcPosition = new HashMap<>();

		for (var talent : availableTalents) {
			availableTalentsById.put(talent.getId(), talent);
			availableTalentsByNameRank.put(talent.getNameRank(), talent);
			availableTalentsByCalcPosition.put(new CalcPositionKey(talent.getTalentCalculatorPosition(), talent.getRank()), talent);
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

	public boolean hasMaxRank(String name) {
		var talent = talentByName.get(name);

		return talent != null && talent.getRank() == talent.getMaxRank();
	}

	public int getRank(String name) {
		var talent = talentByName.get(name);

		return talent != null ? talent.getRank() : 0;
	}

	public List<Talent> getAvailableTalents() {
		return List.copyOf(availableTalentsById.values());
	}

	public String getTalentLink() {
		var talentLinkFormatter = new TalentLinkFormatter(
				this,
				owner.getGameVersionId(),
				owner.getCharacterClassId()
		);

		return talentLinkFormatter.format();
	}

	public <R> List<R> getIds(Function<TalentId, R> mapper) {
		return getStream()
				.map(Talent::getId)
				.map(mapper)
				.toList();
	}

	public List<TalentId> getIds() {
		return getIds(Function.identity());
	}

	public void reset() {
		talentByName.clear();
	}

	public void loadFromTalentLink(String link) {
		var talentLink = TalentLinkParser.parse(link, this);

		loadFromTalentLink(talentLink);
	}

	public void loadFromTalentLink(TalentLink link) {
		if (!link.matches(owner.getCharacterClassId(), owner.getGameVersionId())) {
			throw new IllegalArgumentException("Incorrect talent link");
		}

		reset();

		for (var talent : link.talents()) {
			enable(talent.name(), talent.rank());
		}
	}

	public <M> void setIds(Collection<M> ids, Function<M, TalentId> mapper) {
		reset();

		for (var id : ids) {
			enable(mapper.apply(id));
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

	public void enableMaxRank(String name) {
		var talent = getTalentMaxRank(name).orElseThrow();

		enable(talent);
	}

	private void enable(Talent talent) {
		talentByName.put(talent.getName(), talent);
	}

	private Optional<Talent> getTalent(String name, int rank) {
		var nameRank = new TalentNameRank(name, rank);

		return Optional.ofNullable(availableTalentsByNameRank.get(nameRank));
	}

	private Optional<Talent> getTalentMaxRank(String name) {
		return getTalent(name, 1).flatMap(
				rank1Talent -> getTalent(name, rank1Talent.getMaxRank())
		);
	}

	public Optional<Talent> getTalent(int talentCalculatorPosition, int rank) {
		var key = new CalcPositionKey(talentCalculatorPosition, rank);

		return Optional.ofNullable(availableTalentsByCalcPosition.get(key));
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
}
