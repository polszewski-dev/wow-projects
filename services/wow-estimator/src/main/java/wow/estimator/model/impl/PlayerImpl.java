package wow.estimator.model.impl;

import lombok.Getter;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.character.model.script.ScriptPathResolver;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.commons.model.spell.Ability;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentTree;
import wow.estimator.model.Player;
import wow.estimator.model.Rotation;
import wow.estimator.model.RotationTemplate;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-20
 */
@Getter
public class PlayerImpl extends UnitImpl implements Player {
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Assets assets;
	private Rotation rotation;

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
		copy.getEffectInstances().copyFrom(getEffectInstances());
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
