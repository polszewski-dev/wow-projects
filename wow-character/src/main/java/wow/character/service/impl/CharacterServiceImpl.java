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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

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
	public Character createCharacter(CharacterClassId characterClassId, RaceId raceId, int level, PhaseId phaseId) {
		Phase phase = characterRepository.getPhase(phaseId).orElseThrow();
		GameVersion gameVersion = phase.getGameVersion();

		return new Character(
				gameVersion.getCharacterClass(characterClassId),
				gameVersion.getRace(raceId),
				level,
				phase,
				new Talents(spellService.getAvailableTalents(characterClassId, phaseId))
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
		build.getTalents().loadFromTalentLink(buildTemplate.getTalentLink());
		build.setRole(buildTemplate.getRole());

		character.getSpellbook().reset();
		character.getSpellbook().addSpells(spellService.getAvailableSpells(character));

		build.setRotation(getRotation(character, buildTemplate));
		build.setActivePet(buildTemplate.getActivePet());
		build.setBuffSets(getBuffSets(buildTemplate, character));

		build.setRelevantSpells(getSpellHighestRanks(character, buildTemplate));

		character.setBuffs(BuffSetId.values());
		character.setProfessions(buildTemplate.getProfessions());
	}

	private Rotation getRotation(Character character, BuildTemplate buildTemplate) {
		List<Spell> cooldowns = new ArrayList<>();
		Spell filler = null;

		for (SpellId spellId : buildTemplate.getDefaultRotation()) {
			Optional<Spell> optionalSpell = character.getSpellbook().getSpell(spellId);

			if (optionalSpell.isEmpty()) {
				continue;
			}

			Spell spell = optionalSpell.get();

			if ((spell.hasDotComponent() && !spell.isChanneled()) || spell.getCooldown().isPositive()) {
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

	private List<Spell> getSpellHighestRanks(Character character, BuildTemplate buildTemplate) {
		return buildTemplate.getRelevantSpells().stream()
				.map(x -> character.getSpellbook().getSpell(x))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toList();
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
