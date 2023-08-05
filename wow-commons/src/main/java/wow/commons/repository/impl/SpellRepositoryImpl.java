package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffId;
import wow.commons.model.buffs.BuffIdAndRank;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.spells.SpellIdAndRank;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.repository.SpellRepository;
import wow.commons.repository.impl.parsers.spells.SpellExcelParser;
import wow.commons.util.CollectionUtil;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Repository
@RequiredArgsConstructor
public class SpellRepositoryImpl extends ExcelRepository implements SpellRepository {
	private final Map<CharacterClassId, List<Spell>> spellsByClass = new LinkedHashMap<>();
	private final Map<SpellIdAndRank, List<Spell>> spellById = new LinkedHashMap<>();
	private final Map<CharacterClassId, List<Talent>> talentsByClass = new LinkedHashMap<>();
	private final Map<String, List<Talent>> talentByClassByIdByRank = new LinkedHashMap<>();
	private final Map<String, List<Talent>> talentByClassByCalcPosByRank = new LinkedHashMap<>();
	private final Map<BuffIdAndRank, List<Buff>> buffsById = new LinkedHashMap<>();
	private final List<Buff> buffs = new ArrayList<>();

	@Value("${spell.xls.file.path}")
	private String xlsFilePath;

	@Override
	public List<Spell> getAvailableSpells(CharacterClassId characterClassId, int level, PhaseId phaseId) {
		return spellsByClass.getOrDefault(characterClassId, List.of()).stream()
				.filter(spell -> spell.getRequiredLevel() <= level)
				.filter(spell -> spell.isAvailableDuring(phaseId))
				.toList();
	}

	@Override
	public Optional<Spell> getSpell(SpellId spellId, int rank, PhaseId phaseId) {
		return getUnique(spellById, new SpellIdAndRank(spellId, rank), phaseId);
	}

	@Override
	public List<Talent> getAvailableTalents(CharacterClassId characterClassId, PhaseId phaseId) {
		return talentsByClass.getOrDefault(characterClassId, List.of()).stream()
				.filter(spell -> spell.isAvailableDuring(phaseId))
				.toList();
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, TalentId talentId, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentId, rank);

		return getUnique(talentByClassByIdByRank, key, phaseId);
	}

	@Override
	public Optional<Talent> getTalent(CharacterClassId characterClassId, int talentCalculatorPosition, int rank, PhaseId phaseId) {
		String key = getTalentKey(characterClassId, talentCalculatorPosition, rank);

		return getUnique(talentByClassByCalcPosByRank, key, phaseId);
	}

	@Override
	public List<Buff> getAvailableBuffs(PhaseId phaseId) {
		return getList(buffs, phaseId);
	}

	@Override
	public Optional<Buff> getBuff(BuffId buffId, int rank, PhaseId phaseId) {
		return getUnique(buffsById, new BuffIdAndRank(buffId, rank), phaseId);
	}

	@PostConstruct
	public void init() throws IOException {
		var spellExcelParser = new SpellExcelParser(xlsFilePath, this);
		spellExcelParser.readFromXls();
	}

	private String getTalentKey(CharacterClassId characterClassId, TalentId talentId, int rank) {
		return characterClassId + "#" + talentId + "#" + rank;
	}

	private String getTalentKey(CharacterClassId characterClassId, int talentCalculatorPosition, int rank) {
		return characterClassId + "#" + talentCalculatorPosition + "#" + rank;
	}

	public void addSpell(Spell spell) {
		for (CharacterClassId characterClassId : spell.getCharacterRestriction().characterClassIds()) {
			spellsByClass.computeIfAbsent(characterClassId, x -> new ArrayList<>()).add(spell);
		}
		addEntry(spellById, spell.getId(), spell);
	}

	public void addTalent(Talent talent) {
		String key = getTalentKey(talent.getCharacterClass(), talent.getTalentCalculatorPosition(), talent.getRank());
		List<Talent> list = talentByClassByCalcPosByRank.computeIfAbsent(key, x -> new ArrayList<>());

		Optional<Talent> optionalExistingTalent = list.stream()
				.filter(x -> x.getTimeRestriction().versions().equals(talent.getTimeRestriction().versions()))
				.collect(CollectionUtil.toOptionalSingleton());

		if (optionalExistingTalent.isEmpty()) {
			list.add(talent);
			addEntry(talentByClassByIdByRank, getTalentKey(talent.getCharacterClass(), talent.getTalentId(), talent.getRank()), talent);
			talentsByClass.computeIfAbsent(talent.getCharacterClass(), x -> new ArrayList<>()).add(talent);
			return;
		}

		Talent existingTalent = optionalExistingTalent.orElseThrow();
		Talent combinedTalent = existingTalent.combineWith(talent);

		list.remove(existingTalent);
		list.add(combinedTalent);
	}

	public void addBuff(Buff buff) {
		addEntry(buffsById, buff.getId(), buff);
		buffs.add(buff);
	}
}
