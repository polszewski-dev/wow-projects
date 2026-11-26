package wow.estimator.model.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.effect.EffectCollector;
import wow.character.model.script.ScriptPathResolver;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentTree;
import wow.estimator.model.*;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Getter
public class PlayerImpl extends CharacterImpl implements Player {
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Assets assets;
	private final EffectInstances effectInstances;
	private Rotation rotation;
	@Setter
	private Percent healthPct = Percent._100;

	public PlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			Race race,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			List<Talent> availableTalents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, level, race.getCreatureType(), race, race.getSide(), baseStatInfo, combatRatingInfo, availableTalents);
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.assets = new Assets(this);
		this.effectInstances = new EffectInstances(this);
	}

	private PlayerImpl(
			String name,
			Phase phase,
			CharacterClass characterClass,
			int level,
			BaseStatInfo baseStatInfo,
			CombatRatingInfo combatRatingInfo,
			Spellbook spellbook,
			Race race,
			List<Talent> availableTalents,
			CharacterProfessions professions,
			ExclusiveFactions exclusiveFactions
	) {
		super(name, phase, characterClass, level, race.getCreatureType(), race, race.getSide(), baseStatInfo, combatRatingInfo, availableTalents, spellbook);
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.assets = new Assets(this);
		this.effectInstances = new EffectInstances(this);
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
	public void setFocus(Character focus) {
		if (focus != null && !(focus instanceof Unit)) {
			throw new IllegalArgumentException();
		}
		super.setFocus(focus);
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
				getRace(),
				getTalents().getAvailableTalents(),
				getProfessions().copy(),
				getExclusiveFactions().copy()
		);
		copy.setEquipment(getEquipment());
		copy.setTalents(getTalents());
		copy.getBuffs().copyFrom(getBuffs());
		copy.getConsumables().copyFrom(getConsumables());
		copy.getAssets().copyFrom(getAssets());
		copy.setRole(getRole());
		copy.setScript(getScript());
		copy.setTarget(getTarget());
		copy.setFocus(getFocus());
		copy.getEffectInstances().copyFrom(effectInstances);
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
		super.collectEffects(collector);
		getConsumables().collectEffects(collector);
		getEffectInstances().collectEffects(collector);
	}

	@Override
	public Rotation getRotation() {
		if (rotation == null) {
			var scriptPath = ScriptPathResolver.getScriptPath(getScript(), getGameVersion());

			this.rotation = RotationTemplate.parse(scriptPath)
					.createRotation()
					.compile(this);
		}
		return rotation;
	}

	@Override
	public void setScript(String script) {
		super.setScript(script);

		this.rotation = null;
	}

	@Override
	public void invalidateCaches() {
		super.invalidateCaches();

		this.rotation = null;
	}
}
