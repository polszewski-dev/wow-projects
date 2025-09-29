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
	private final Consumables consumables;

	public PlayerCharacterImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Talents talents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, level, baseStatInfo, combatRatingInfo);
		this.race = race;
		this.build = new Build(phase.getGameVersion(), talents);
		this.equipment = new Equipment();
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.consumables = new Consumables();
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		getBuild().collectEffects(collector);
		getEquipment().collectEffects(collector);
		getBuffs().collectEffects(collector);
		getConsumables().collectEffects(collector);
		for (var racial : getRace().getRacials(this)) {
			collector.addEffect(racial);
		}
	}
}
