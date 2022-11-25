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
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
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
	private final Map<SpellId, List<Spell>> spellById = new LinkedHashMap<>();
	private final Map<TalentId, List<Talent>> talentById = new LinkedHashMap<>();
	private final Map<Integer, TalentId> talentIdByCalculatorPosition = new LinkedHashMap<>();
	private final Map<EffectId, EffectInfo> effectInfoByEffectId = new LinkedHashMap<>();
	private final Map<Integer, Buff> buffsById = new LinkedHashMap<>();

	@Override
	public Optional<Spell> getSpell(SpellId spellId, Integer rank) {
		List<Spell> spells = spellById.getOrDefault(spellId, List.of());
		return spells.stream()
				.filter(spell -> Objects.equals(spell.getRank(), rank))
				.findFirst();
	}

	@Override
	public Optional<Spell> getSpellHighestRank(SpellId spellId, int level) {
		List<Spell> spells = spellById.get(spellId);
		if (spells == null) {
			return Optional.empty();
		}
		return spells.stream()
				.filter(spell -> spell.getRequiredLevel() <= level)
				.max(Comparator.nullsFirst(Comparator.comparing(Spell::getRank)));
	}

	@Override
	public Optional<Talent> getTalent(TalentId talentId, int rank) {
		List<Talent> talents = talentById.getOrDefault(talentId, List.of());
		return talents.stream()
				.filter(talent -> talent.getRank() == rank)
				.findFirst();
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

	public void addTalent(Talent talent, Attributes attributes) {
		talentIdByCalculatorPosition.put(talent.getTalentCalculatorPosition(), talent.getTalentId());

		Optional<Talent> existingTalent = getTalent(talent.getTalentId(), talent.getRank());

		if (existingTalent.isPresent()) {
			talent = existingTalent.orElseThrow();
		} else {
			talentById.computeIfAbsent(talent.getTalentId(), x -> new ArrayList<>()).add(talent);
		}

		Attributes combinedAttributes = AttributesBuilder.addAttributes(talent.getAttributes(), attributes);
		talent.setAttributes(combinedAttributes);
	}

	public void addSpell(Spell spell) {
		spellById.computeIfAbsent(spell.getSpellId(), x -> new ArrayList<>()).add(spell);
	}

	public void addEffectInfo(EffectInfo effectInfo) {
		effectInfoByEffectId.put(effectInfo.getEffectId(), effectInfo);
	}

	public void addBuff(Buff buff) {
		buffsById.put(buff.getId(), buff);
	}
}
