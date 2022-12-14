package wow.commons.repository.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
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
	private final Map<SpellId, List<Spell>> spellById = new LinkedHashMap<>();
	private final Map<String, List<Talent>> talentByClassByCalcPosByRank = new LinkedHashMap<>();
	private final Map<Integer, List<Buff>> buffsById = new LinkedHashMap<>();
	private final Map<String, List<Buff>> buffsByName = new LinkedHashMap<>();
	private final List<Buff> buffs = new ArrayList<>();

	@Value("${spell.xls.file.path}")
	private String xlsFilePath;

	@Override
	public Optional<Spell> getSpellHighestRank(SpellId spellId, int level, Phase phase) {
		List<Spell> spells = getList(spellById, spellId, phase);

		if (spells.isEmpty()) {
			return Optional.empty();
		}

		int maxRank = spells.stream()
				.filter(spell -> spell.getRequiredLevel() <= level)
				.mapToInt(Spell::getRank)
				.max()
				.orElseThrow();

		return spells.stream()
				.filter(spell -> spell.getRank() == maxRank)
				.collect(CollectionUtil.toOptionalSingleton());
	}

	@Override
	public Optional<Talent> getTalent(CharacterClass characterClass, int talentCalculatorPosition, int rank, Phase phase) {
		String key = getTalentKey(characterClass, talentCalculatorPosition, rank);

		return getUnique(talentByClassByCalcPosByRank, key, phase);
	}

	@Override
	public Optional<Buff> getBuff(int buffId, Phase phase) {
		return getUnique(buffsById, buffId, phase);
	}

	@Override
	public Optional<Buff> getBuff(String buffName, Phase phase) {
		return getUnique(buffsByName, buffName, phase);
	}

	@Override
	public List<Buff> getBuffs(Phase phase) {
		return getList(buffs, phase);
	}

	@PostConstruct
	public void init() throws IOException, InvalidFormatException {
		var spellExcelParser = new SpellExcelParser(xlsFilePath, this);
		spellExcelParser.readFromXls();
	}

	private String getTalentKey(CharacterClass characterClass, int talentCalculatorPosition, int rank) {
		return characterClass + "#" + talentCalculatorPosition + "#" + rank;
	}

	public void addSpell(Spell spell) {
		addEntry(spellById, spell.getSpellId(), spell);
	}

	public void addTalent(Talent talent) {
		String key = getTalentKey(talent.getCharacterClass(), talent.getTalentCalculatorPosition(), talent.getRank());
		List<Talent> list = talentByClassByCalcPosByRank.computeIfAbsent(key, x -> new ArrayList<>());

		Optional<Talent> optionalExistingTalent = list.stream()
				.filter(x -> x.getTimeRestriction().getVersions().equals(talent.getTimeRestriction().getVersions()))
				.collect(CollectionUtil.toOptionalSingleton());

		if (optionalExistingTalent.isEmpty()) {
			list.add(talent);
			return;
		}

		Talent existingTalent = optionalExistingTalent.orElseThrow();
		Talent combinedTalent = existingTalent.combineWith(talent);

		list.remove(existingTalent);
		list.add(combinedTalent);
	}

	public void addBuff(Buff buff) {
		addEntry(buffsById, buff.getId(), buff);
		addEntry(buffsByName, buff.getName(), buff);
		buffs.add(buff);
	}
}
