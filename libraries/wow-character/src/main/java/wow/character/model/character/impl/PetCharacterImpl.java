package wow.character.model.character.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.Character;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.PetCharacter;
import wow.character.model.talent.Talents;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.Spell;

/**
 * User: POlszewski
 * Date: 09.08.2026
 */
@Getter
@Setter
public class PetCharacterImpl extends CharacterImpl implements PetCharacter {
	private final PetType petType;
	private final Spell sourceSpell;
	private Character master;
	private Percent healthPct = Percent._100;

	public PetCharacterImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			PetType petType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents,
			Spell sourceSpell
	) {
		super(name, phase, characterClass, level, petType.getCreatureType(), race, side, baseStatInfo, combatRatingInfo, talents);
		this.petType = petType;
		this.sourceSpell = sourceSpell;
	}
}
