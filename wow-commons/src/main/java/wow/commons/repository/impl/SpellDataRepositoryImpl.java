package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Repository;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.effects.EffectId;
import wow.commons.model.effects.EffectInfo;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.SpellDataRepository;
import wow.commons.repository.impl.parsers.SpellExcelParser;
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
	private final Map<EffectId, EffectInfo> effectInfoByEffectId = new LinkedHashMap<>();
	private final Map<Integer, Buff> buffsById = new LinkedHashMap<>();

	@Override
	public SpellInfo getSpellInfo(SpellId spellId) {
		return spellInfoBySpellId.get(spellId);
	}

	@Override
	public TalentInfo getTalentInfo(TalentId talentId, Integer rank) {
		Map<Integer, TalentInfo> talentInfoByRank = talentInfoByTalentIdByRank.get(talentId);
		if (talentInfoByRank == null) {
			return null;
		}
		if (rank != null) {
			return talentInfoByRank.get(rank);
		}
		Integer maxRank = talentInfoByRank.keySet().stream().max(Integer::compareTo).orElse(null);
		return maxRank != null ? talentInfoByRank.get(maxRank) : null;
	}

	@Override
	public EffectInfo getEffectInfo(EffectId effectId) {
		return effectInfoByEffectId.get(effectId);
	}

	@Override
	public Buff getBuff(int buffId) {
		return buffsById.get(buffId);
	}

	@Override
	public Buff getHighestRankBuff(String name, int level) {
		Buff result = null;
		for (Buff buff : buffsById.values()) {
			if (buff.getName().equals(name) && buff.getLevel() <= level) {
				if (result == null || buff.getLevel() > result.getLevel()) {
					result = buff;
				}
			}
		}
		return result;
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
		SpellExcelParser.readFromXls(this);
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
