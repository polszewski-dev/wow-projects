package wow.estimator.model.impl;

import lombok.Getter;
import wow.character.model.build.Talents;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.Equipment;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.TalentTree;
import wow.estimator.model.EffectInstances;
import wow.estimator.model.Player;
import wow.estimator.model.Unit;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Getter
public class PlayerImpl extends CharacterImpl implements Player {
	private final Race race;
	private final BuildWithRotation build;
	private final Equipment equipment;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Buffs buffs;
	private final Consumables consumables;
	private final Assets assets;
	private final EffectInstances effectInstances;

	public PlayerImpl(
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
		this.build = new BuildWithRotation(phase.getGameVersion(), talents);
		this.equipment = new Equipment();
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.buffs = new Buffs();
		this.consumables = new Consumables();
		this.assets = new Assets();
		this.effectInstances = new EffectInstances();
	}

	private PlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Spellbook spellbook,
			Buffs buffs,
			Race race,
			BuildWithRotation build,
			Equipment equipment,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions,
			Consumables consumables,
			Assets assets,
			EffectInstances effectInstances
	) {
		super(name, phase, characterClass, level, baseStatInfo, combatRatingInfo, spellbook);
		this.race = race;
		this.build = build;
		this.equipment = equipment;
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.buffs = buffs;
		this.consumables = consumables;
		this.assets = assets;
		this.effectInstances = effectInstances;
	}

	@Override
	public Unit getTarget() {
		return (Unit) super.getTarget();
	}

	@Override
	public void setTarget(Character target) {
		if (target != null && !(target instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setTarget(target);
	}

	@Override
	public PlayerImpl copy() {
		var copy = new PlayerImpl(
				getName(),
				getPhase(),
				getCharacterClass(),
				getLevel(),
				getBaseStatInfo(),
				getCombatRatingInfo(),
				getSpellbook().copy(),
				getBuffs().copy(),
				getRace(),
				getBuild().copy(),
				getEquipment().copy(),
				getProfessions().copy(),
				getExclusiveFactions().copy(),
				getConsumables().copy(),
				getAssets().copy(),
				getEffectInstances().copy()
		);
		copy.setTarget(getTarget());
		return copy;
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		return 0;
	}

	@Override
	public boolean canCast(Ability ability) {
		return !isSchoolPrevented(ability);
	}

	private boolean isSchoolPrevented(Ability ability) {
		return getEffectInstances().isSchoolPrevented(ability.getSchool());
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
		getEffectInstances().collectEffects(collector);
	}
}
