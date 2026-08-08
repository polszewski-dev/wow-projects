package wow.character.model.character.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.character.model.talent.Talents;
import wow.commons.model.Percent;
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
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Buffs buffs;
	private final Assets assets;
	@Setter
	private Percent healthPct = Percent._100;

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
		super(name, phase, characterClass, level, race.getCreatureType(), race.getSide(), baseStatInfo, combatRatingInfo, talents);
		this.race = race;
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.buffs = new Buffs();
		this.assets = new Assets();
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		getTalents().collectEffects(collector);
		getEquipment().collectEffects(collector);
		getBuffs().collectEffects(collector);
		getConsumables().collectEffects(collector);
		for (var racial : getRace().getRacials(this)) {
			collector.addEffect(racial);
		}
	}
}
