package wow.commons.repository.impl;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Repository;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.effects.EffectId;
import wow.commons.model.effects.EffectInfo;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellIdAndRank;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.model.talents.TalentIdAndRank;
import wow.commons.repository.SpellDataRepository;
import wow.commons.repository.impl.parsers.spells.SpellExcelParser;

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
	private final Map<SpellIdAndRank, Spell> spellById = new LinkedHashMap<>();
	private final Map<SpellId, List<Spell>> spellBySpellId = new LinkedHashMap<>();
	private final Map<TalentIdAndRank, Talent> talentById = new LinkedHashMap<>();
	private final Map<Integer, TalentId> talentIdByCalculatorPosition = new LinkedHashMap<>();
	private final Map<EffectId, EffectInfo> effectInfoByEffectId = new LinkedHashMap<>();
	private final Map<Integer, Buff> buffsById = new LinkedHashMap<>();

	@Override
	public Optional<Spell> getSpell(SpellId spellId, Integer rank) {
		return Optional.ofNullable(spellById.get(new SpellIdAndRank(spellId, rank)));
	}

	@Override
	public Optional<Spell> getSpellHighestRank(SpellId spellId, int level) {
		return spellById.values().stream()
				.filter(spell -> spell.getSpellId() == spellId)
				.filter(spell -> spell.getRequiredLevel() <= level)
				.max(Comparator.nullsFirst(Comparator.comparing(Spell::getRank)));
	}

	@Override
	public List<Spell> getAllSpellRanks(SpellId spellId) {
		return spellBySpellId.getOrDefault(spellId, List.of());
	}

	@Override
	public Optional<Talent> getTalent(TalentId talentId, int rank) {
		return Optional.ofNullable(talentById.get(new TalentIdAndRank(talentId, rank)));
	}

	@Override
	public Optional<Talent> getTalent(int talentCalculatorPosition, int rank) {
		TalentId talentId = talentIdByCalculatorPosition.get(talentCalculatorPosition);
		if (talentId == null) {
			return Optional.empty();
		}
		return getTalent(talentId, rank);
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
				.filter(buff -> buff.getName().equals(name) && buff.getRequiredLevel() <= level)
				.max(Comparator.comparingInt(Buff::getRequiredLevel));
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
	}

	public void addTalent(Talent talent) {
		talentIdByCalculatorPosition.put(talent.getTalentCalculatorPosition(), talent.getTalentId());

		Talent existingTalent = talentById.get(talent.getId());

		if (existingTalent == null) {
			talentById.put(talent.getId(), talent);
			return;
		}

		Talent combinedTalent = existingTalent.combineWith(talent);
		talentById.put(combinedTalent.getId(), combinedTalent);
	}

	public void addSpell(Spell spell) {
		spellById.put(spell.getId(), spell);
		spellBySpellId.computeIfAbsent(spell.getSpellId(), x -> new ArrayList<>()).add(spell);
	}

	public void addEffectInfo(EffectInfo effectInfo) {
		effectInfoByEffectId.put(effectInfo.getEffectId(), effectInfo);
	}

	public void addBuff(Buff buff) {
		buffsById.put(buff.getId(), buff);
	}
}
