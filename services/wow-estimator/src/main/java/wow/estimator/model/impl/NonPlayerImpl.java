package wow.estimator.model.impl;

import lombok.Getter;
import wow.character.model.character.BaseStatInfo;
import wow.character.model.character.CombatRatingInfo;
import wow.character.model.character.Spellbook;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talent.Talent;
import wow.estimator.model.NonPlayer;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Getter
public class NonPlayerImpl extends UnitImpl implements NonPlayer {
	public NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents
	) {
		super(name, phase, characterClass, level, creatureType, race, side, baseStatInfo, combatRatingInfo, availableTalents);
	}

	private NonPlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			CreatureType creatureType,
			Race race,
			Side side,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents,
			Spellbook spellbook
	) {
		super(name, phase, characterClass, level, creatureType, race, side, baseStatInfo, combatRatingInfo, availableTalents, spellbook);
	}

	@Override
	public NonPlayer copy() {
		var copy = new NonPlayerImpl(
				getName(),
				getPhase(),
				getCharacterClass(),
				getLevel(),
				getCreatureType(),
				getRace(),
				getSide(),
				getBaseStatInfo(),
				getCombatRatingInfo(),
				getTalents().getAvailableTalents(),
				getSpellbook().copy()
		);
		copy.setEquipment(getEquipment());
		copy.setTalents(getTalents());
		copy.getBuffs().copyFrom(getBuffs());
		copy.getConsumables().copyFrom(getConsumables());
		copy.setRole(getRole());
		copy.setScript(getScript());
		copy.setTarget(getTarget());
		copy.setFocus(getFocus());
		copy.getEffectInstances().copyFrom(getEffectInstances());
		return copy;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		getEffectInstances().collectEffects(collector);
	}
}
