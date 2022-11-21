package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Repository;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.effects.EffectId;
import wow.commons.model.effects.EffectInfo;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.SpellDataRepository;
import wow.commons.repository.impl.parsers.spells.SpellExcelParser;
import wow.commons.util.AttributesBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Repository
public class SpellDataRepositoryImpl implements SpellDataRepository {
	private final Map<SpellId, SpellInfo> spellInfoBySpellId = new LinkedHashMap<>();
	private final Map<TalentId, Map<Integer, TalentInfo>> talentInfoByTalentIdByRank = new LinkedHashMap<>();
	private final Map<Integer, Map<Integer, TalentInfo>> talentInfoByCalculatorPositionIdByRank = new LinkedHashMap<>();
	private final Map<EffectId, EffectInfo> effectInfoByEffectId = new LinkedHashMap<>();
	private final Map<Integer, Buff> buffsById = new LinkedHashMap<>();

	@Override
	public Optional<SpellInfo> getSpellInfo(SpellId spellId) {
		return Optional.ofNullable(spellInfoBySpellId.get(spellId));
	}

	@Override
	public Optional<Spell> getSpell(SpellId spellId, Integer rank) {
		Optional<SpellInfo> optionalSpellInfo = getSpellInfo(spellId);

		if (optionalSpellInfo.isEmpty()) {
			return Optional.empty();
		}

		SpellInfo spellInfo = optionalSpellInfo.orElseThrow();

		if (spellInfo.hasRanks() && rank != null) {
			return spellInfo.getRank(rank).map(spellRankInfo -> new Spell(spellInfo, spellRankInfo));
		} else if (!spellInfo.hasRanks() && rank == null) {
			return Optional.of(new Spell(spellInfo, null));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public Optional<Spell> getSpellHighestRank(SpellId spellId, int level) {
		Optional<SpellInfo> optionalSpellInfo = getSpellInfo(spellId);

		if (optionalSpellInfo.isEmpty()) {
			return Optional.empty();
		}

		SpellInfo spellInfo = optionalSpellInfo.orElseThrow();

		return spellInfo
				.getHighestRank(level)
				.map(spellRankInfo -> new Spell(spellInfo, spellRankInfo));
	}

	@Override
	public Optional<TalentInfo> getTalentInfo(TalentId talentId, Integer rank) {
		Map<Integer, TalentInfo> talentInfoByRank = talentInfoByTalentIdByRank.get(talentId);
		return getTalentInfo(rank, talentInfoByRank);
	}

	@Override
	public Optional<TalentInfo> getTalentInfo(int talentCalculatorPosition, Integer rank) {
		Map<Integer, TalentInfo> talentInfoByRank = talentInfoByCalculatorPositionIdByRank.get(talentCalculatorPosition);
		return getTalentInfo(rank, talentInfoByRank);
	}

	private static Optional<TalentInfo> getTalentInfo(Integer rank, Map<Integer, TalentInfo> talentInfoByRank) {
		if (talentInfoByRank == null) {
			return Optional.empty();
		}
		if (rank != null) {
			return Optional.ofNullable(talentInfoByRank.get(rank));
		}
		return talentInfoByRank.keySet().stream()
				.max(Integer::compareTo)
				.map(talentInfoByRank::get);
	}

	@Override
	public Optional<EffectInfo> getEffectInfo(EffectId effectId) {
		return Optional.ofNullable(effectInfoByEffectId.get(effectId));
	}

	@Override
	public Optional<Buff> getBuff(int buffId) {
		return Optional.ofNullable(buffsById.get(buffId));
	}

	@Override
	public Optional<Buff> getHighestRankBuff(String name, int level) {
		return buffsById.values().stream()
				.filter(buff -> buff.getName().equals(name) && buff.getLevel() <= level)
				.max(Comparator.comparingInt(Buff::getLevel));
	}

	@Override
	public List<Buff> getAvailableBuffs() {
		return new ArrayList<>(buffsById.values());
	}

	@Override
	public List<Buff> getBuffs(Collection<String> buffNames) {
		return buffsById.values()
						.stream()
						.filter(buff -> buffNames.contains(buff.getName()))
						.collect(Collectors.toList());
	}

	@Override
	public List<Buff> getBuffs(BuffExclusionGroup exclusionGroup) {
		return buffsById.values()
						.stream()
						.filter(buff -> buff.getExclusionGroup() == exclusionGroup)
						.collect(Collectors.toList());
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var spellExcelParser = new SpellExcelParser(this);
		spellExcelParser.readFromXls();
		validateAll();
	}

	private void validateAll() {
		for (SpellInfo spellInfo : spellInfoBySpellId.values()) {
			if (spellInfo.getTalentTree() == null) {
				throw new IllegalArgumentException("No talent tree: " + spellInfo.getSpellId());
			}
		}

		for (Map<Integer, TalentInfo> talentsByRank : talentInfoByTalentIdByRank.values()) {
			for (TalentInfo talentInfo : talentsByRank.values()) {
				if (!(1 <= talentInfo.getRank() && talentInfo.getRank() <= talentInfo.getMaxRank())) {
					throw new IllegalArgumentException("Wrong rank: " + talentInfo.getTalentId());
				}
			}
		}
	}

	public void addTalent(TalentInfo talentInfo, Attributes attributes) {
		Map<Integer, TalentInfo> talentByRank = talentInfoByTalentIdByRank.computeIfAbsent(talentInfo.getTalentId(), x -> new LinkedHashMap<>());

		talentInfoByCalculatorPositionIdByRank.computeIfAbsent(talentInfo.getTalentCalculatorPosition(), x -> talentByRank);

		if (talentByRank.containsKey(talentInfo.getRank())) {
			// simply add another benefit to already existing talent rank
			talentInfo = talentByRank.get(talentInfo.getRank());
		}

		talentInfo.setAttributes(
				new AttributesBuilder()
				.addAttributes(talentInfo.getAttributes())
				.addAttributes(attributes)
				.toAttributes());

		talentInfoByTalentIdByRank.get(talentInfo.getTalentId()).put(talentInfo.getRank(), talentInfo);
	}

	public void addSpellInfo(SpellInfo spellInfo) {
		spellInfoBySpellId.put(spellInfo.getSpellId(), spellInfo);
	}

	public void addEffectInfo(EffectInfo effectInfo) {
		effectInfoByEffectId.put(effectInfo.getEffectId(), effectInfo);
	}

	public void addBuff(Buff buff) {
		buffsById.put(buff.getId(), buff);
	}
}
