package wow.character.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import wow.character.model.build.Build;
import wow.character.model.build.Rotation;
import wow.character.model.build.Talents;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.repository.CharacterRepository;
import wow.character.service.CharacterService;
import wow.character.service.SpellService;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;

import java.util.ArrayList;
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
	public void applyCharacterTemplate(Character character, CharacterTemplateId characterTemplateId) {
		CharacterTemplate characterTemplate = getCharacterTemplate(characterTemplateId, character);

		changeBuild(character, characterTemplate);

		character.setProfessions(characterTemplate.getProfessions());
		character.getExclusiveFactions().set(characterTemplate.getExclusiveFactions());
		character.setBuffs(spellService.getBuffs(characterTemplate.getDefaultBuffs(), character));
		character.getTargetEnemy().setDebuffs(spellService.getBuffs(characterTemplate.getDefaultDebuffs(), character));
	}

	private CharacterTemplate getCharacterTemplate(CharacterTemplateId characterTemplateId, Character character) {
		return characterRepository.getCharacterTemplate(
				characterTemplateId, character.getCharacterClassId(), character.getLevel(), character.getPhaseId()
		).orElseThrow();
	}

	private void changeBuild(Character character, CharacterTemplate characterTemplate) {
		Build build = character.getBuild();

		build.reset();
		build.getTalents().loadFromTalentLink(characterTemplate.getTalentLink());
		build.setRole(characterTemplate.getRole());
		build.setActivePet(characterTemplate.getActivePet());

		onTalentsChange(character);

		build.setRotation(getRotation(character, characterTemplate));
	}

	@Override
	public void onTalentsChange(Character character) {
		character.getSpellbook().reset();
		character.getSpellbook().addSpells(spellService.getAvailableSpells(character));
	}

	private Rotation getRotation(Character character, CharacterTemplate characterTemplate) {
		List<Spell> cooldowns = new ArrayList<>();
		Spell filler = null;

		for (SpellId spellId : characterTemplate.getDefaultRotation()) {
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
}
