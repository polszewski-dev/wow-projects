package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.*;
import wow.character.model.character.Character;
import wow.character.model.character.GameVersion;
import wow.character.model.character.Phase;
import wow.character.repository.CharacterRepository;
import wow.character.service.CharacterService;
import wow.character.service.SpellService;
import wow.commons.model.buffs.Buff;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.util.*;

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
	public BuildTemplate getBuildTemplate(BuildId buildId, Character character) {
		return characterRepository.getBuildTemplate(
				buildId, character.getCharacterClassId(), character.getLevel(), character.getPhaseId()
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
	public Character createCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId) {
		Phase phase = characterRepository.getPhase(phaseId).orElseThrow();
		GameVersion gameVersion = phase.getGameVersion();

		return new Character(
				gameVersion.getCharacterClass(characterClassId),
				gameVersion.getRace(raceId),
				level,
				phase
		);
	}

	@Override
	public void setDefaultBuild(Character character) {
		changeBuild(character, character.getCharacterClass().getDefaultBuildId());
	}

	@Override
	public void changeBuild(Character character, BuildId buildId) {
		BuildTemplate buildTemplate = getBuildTemplate(buildId, character);

		character.resetBuild();
		character.resetBuffs();
		character.getTargetEnemy().resetDebuffs();
		character.resetProfessions();

		Build build = character.getBuild();

		build.setBuildId(buildId);
		build.setTalentLink(buildTemplate.getTalentLink());
		build.setTalents(getTalentsFromTalentLink(buildTemplate.getTalentLink(), character));
		build.setRole(buildTemplate.getRole());
		build.setRotation(getRotation(character, buildTemplate));
		build.setRelevantSpells(spellService.getSpellHighestRanks(buildTemplate.getRelevantSpells(), character));
		build.setActivePet(buildTemplate.getActivePet());
		build.setBuffSets(getBuffSets(buildTemplate, character));

		character.setBuffs(BuffSetId.values());
		character.setProfessions(buildTemplate.getProfessions());
	}

	private Rotation getRotation(Character character, BuildTemplate buildTemplate) {
		List<Spell> cooldowns = new ArrayList<>();
		Spell filler = null;

		for (SpellId spellId : buildTemplate.getDefaultRotation()) {
			Spell spell = spellService.getSpellHighestRank(spellId, character);
			if (spell.hasDotComponent() || spell.getCooldown().isPositive()) {
				cooldowns.add(spell);
			} else if (filler == null) {
				filler = spell;
			} else {
				throw new IllegalArgumentException("Can't have two fillers: %s, %s".formatted(filler, spell));
			}
		}

		if (filler == null) {
			throw new IllegalArgumentException("No filler");
		}

		return new Rotation(cooldowns, filler);
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
}
