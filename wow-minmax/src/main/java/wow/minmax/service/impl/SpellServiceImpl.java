package wow.minmax.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterInfo;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.service.SpellService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-28
 */
@Service
@AllArgsConstructor
public class SpellServiceImpl implements SpellService {
	private final SpellDataRepository spellDataRepository;

	@Override
	public Spell getSpellHighestRank(SpellId spellId, CharacterInfo characterInfo) {
		return spellDataRepository.getSpellHighestRank(spellId, characterInfo.getPhase()).orElseThrow();
	}

	@Override
	public List<Spell> getSpellHighestRanks(List<SpellId> spellIds, CharacterInfo characterInfo) {
		return spellIds.stream()
				.map(spellId -> spellDataRepository.getSpellHighestRank(spellId, characterInfo.getPhase()))
				.map(optionalSpell -> optionalSpell.orElse(null))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Override
	public List<Buff> getBuffs(List<String> buffNames, CharacterInfo characterInfo) {
		return buffNames.stream()
				.map(buffName -> spellDataRepository.getBuff(buffName, characterInfo.getPhase()))
				.map(optionalBuff -> optionalBuff.orElse(null))
				.filter(Objects::nonNull)
				.filter(buff -> buff.isAvailableTo(characterInfo))
				.collect(Collectors.toList());
	}

	@Override
	public List<Buff> getBuffs(CharacterInfo characterInfo) {
		return spellDataRepository.getBuffs(characterInfo.getPhase()).stream()
				.filter(buff -> buff.isAvailableTo(characterInfo))
				.collect(Collectors.toList());
	}

	@Override
	public Map<TalentId, Talent> getTalentsFromTalentLink(String link, CharacterInfo characterInfo) {
		Map<TalentId, Talent> result = new LinkedHashMap<>();

		String talentStringStart = "?tal=";
		String talentString = link.substring(link.indexOf(talentStringStart) + talentStringStart.length());

		for (int position = 1; position <= talentString.length(); ++position) {
			int talentRank = talentString.charAt(position - 1) - '0';

			if (talentRank > 0) {
				Talent talent = spellDataRepository.getTalent(characterInfo.getCharacterClass(), position, talentRank, characterInfo.getPhase()).orElseThrow();
				result.put(talent.getTalentId(), talent);
			}
		}

		return result;
	}
}
