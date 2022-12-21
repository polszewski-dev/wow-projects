package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.*;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.repository.CharacterRepository;
import wow.character.service.CharacterService;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-14
 */
@Service
@AllArgsConstructor
public class CharacterServiceImpl implements CharacterService {
	private final CharacterRepository characterRepository;

	private final SpellService spellService;

	@Override
	public BaseStatInfo getBaseStats(Character character) {
		return characterRepository.getBaseStats(
				character.getCharacterClass(), character.getRace(), character.getLevel(), character.getPhase()
		).orElseThrow();
	}

	@Override
	public CombatRatingInfo getCombatRatings(Character character) {
		return characterRepository.getCombatRatings(character.getLevel(), character.getPhase()).orElseThrow();
	}

	@Override
	public BuildTemplate getBuildTemplate(BuildId buildId, Character character) {
		return characterRepository.getBuildTemplate(
				buildId, character.getCharacterClass(), character.getLevel(), character.getPhase()
		).orElseThrow();
	}

	@Override
	public Talents getTalentsFromTalentLink(String link, Character character) {
		Map<TalentId, Talent> result = new LinkedHashMap<>();

		String talentStringStart = "?tal=";
		String talentString = link.substring(link.indexOf(talentStringStart) + talentStringStart.length());

		for (int position = 1; position <= talentString.length(); ++position) {
			int talentRank = talentString.charAt(position - 1) - '0';

			if (talentRank > 0) {
				Talent talent = spellService.getTalent(position, talentRank, character);
				result.put(talent.getTalentId(), talent);
			}
		}

		return new Talents(result);
	}

	@Override
	public Character createCharacter(CharacterClass characterClass, Race race, int level, BuildId buildId, List<CharacterProfession> professions, Phase phase) {
		Character character = new Character(
				characterClass,
				race,
				level,
				new CharacterProfessions(professions),
				phase,
				characterRepository.getBaseStats(characterClass, race, level, phase).orElseThrow(),
				characterRepository.getCombatRatings(level, phase).orElseThrow()
		);

		changeBuild(character, buildId);

		return character;
	}

	private void changeBuild(Character character, BuildId buildId) {
		BuildTemplate buildTemplate = getBuildTemplate(buildId, character);

		character.resetBuild();
		character.resetBuffs();

		Build build = character.getBuild();

		build.setBuildId(buildId);
		build.setTalentLink(buildTemplate.getTalentLink());
		build.setTalents(getTalentsFromTalentLink(buildTemplate.getTalentLink(), character));
		build.setRole(buildTemplate.getRole());
		build.setDamagingSpell(spellService.getSpellHighestRank(buildTemplate.getDamagingSpell(), character));
		build.setRelevantSpells(spellService.getSpellHighestRanks(buildTemplate.getRelevantSpells(), character));
		build.setActivePet(buildTemplate.getActivePet());
		build.setBuffSets(getBuffSets(buildTemplate, character));

		character.setBuffs(BuffSetId.values());
	}

	private BuffSets getBuffSets(BuildTemplate buildTemplate, Character character) {
		var result = new EnumMap<BuffSetId, List<Buff>>(BuffSetId.class);

		for (var entry : buildTemplate.getBuffSets().entrySet()) {
			BuffSetId buffSetId = entry.getKey();
			List<String> buffNames = entry.getValue();
			List<Buff> buffs = spellService.getBuffs(buffNames, character);

			result.put(buffSetId, buffs);
		}

		return new BuffSets(result);
	}

	@Override
	public Enemy createEnemy(CreatureType enemyType) {
		return new Enemy(enemyType);
	}
}
