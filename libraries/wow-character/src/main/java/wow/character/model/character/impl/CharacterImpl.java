package wow.character.model.character.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.pve.Phase;

import static wow.character.model.character.BuffListType.CHARACTER_BUFF;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
@RequiredArgsConstructor
@Getter
public abstract class CharacterImpl implements Character {
	private final String name;
	private final Phase phase;
	private final CharacterClass characterClass;
	private final int level;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;
	private final Spellbook spellbook;
	private final Buffs buffs;
	private Character target;
	private Percent healthPct = Percent._100;

	protected CharacterImpl(String name, Phase phase, CharacterClass characterClass, int level, BaseStatInfo baseStatInfo, CombatRatingInfo combatRatingInfo) {
		this.name = name;
		this.phase = phase;
		this.characterClass = characterClass;
		this.level = level;
		this.baseStatInfo = baseStatInfo;
		this.combatRatingInfo = combatRatingInfo;
		this.spellbook = new Spellbook();
		this.buffs = new Buffs(CHARACTER_BUFF);
	}

	@Override
	public void setTarget(Character target) {
		this.target = target;
	}

	@Override
	public void setHealthPct(Percent healthPct) {
		this.healthPct = healthPct;
	}

	@Override
	public String toString() {
		return name;
	}
}
