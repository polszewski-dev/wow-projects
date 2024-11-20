package wow.character.model.character.impl;

import lombok.Getter;
import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.Equipment;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
@Getter
public class PlayerCharacterImpl extends CharacterImpl implements PlayerCharacter {
	private final Race race;
	private final Build build;
	private final Equipment equipment;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;

	public PlayerCharacterImpl(Phase phase, CharacterClass characterClass, Race race, int level, BaseStatInfo baseStatInfo, CombatRatingInfo combatRatingInfo, Talents talents) {
		super(phase, characterClass, level, baseStatInfo, combatRatingInfo);
		this.race = race;
		this.build = new Build(phase.getGameVersion(), talents);
		this.equipment = new Equipment();
		this.professions = new CharacterProfessions();
		this.exclusiveFactions = new ExclusiveFactions();
	}

	protected PlayerCharacterImpl(
			Phase phase,
			CharacterClass characterClass,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Spellbook spellbook,
			Buffs buffs,
			Race race,
			Build build,
			Equipment equipment,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(phase, characterClass, level, baseStatInfo, combatRatingInfo, spellbook, buffs);
		this.race = race;
		this.build = build;
		this.equipment = equipment;
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		build.collectEffects(collector);
		equipment.collectEffects(collector);
		getBuffs().collectEffects(collector);
		for (var racial : race.getRacials(this)) {
			collector.addEffect(racial);
		}
	}
}
