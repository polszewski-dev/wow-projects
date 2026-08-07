package wow.minmax.model.impl;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.character.model.character.*;
import wow.character.model.character.impl.CharacterImpl;
import wow.character.model.effect.EffectCollector;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.model.Percent;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.Race;
import wow.commons.model.pve.Phase;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.model.Unit;

/**
 * User: POlszewski
 * Date: 2026-02-09
 */
@Getter
public class PlayerImpl extends CharacterImpl implements Player {
	private final PlayerId playerId;
	private final Race race;
	private final Build build;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Buffs buffs;
	private final Assets assets;
	@Setter
	private Percent healthPct = Percent._100;

	public PlayerImpl(
			PlayerId playerId,
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
		super(name, phase, characterClass, level, race.getCreatureType(), race.getSide(), baseStatInfo, combatRatingInfo);
		this.playerId = playerId;
		this.race = race;
		this.build = new Build(phase.getGameVersion(), talents);
		this.professions = professions;
		this.exclusiveFactions = exclusiveFactions;
		this.buffs = new Buffs();
		this.assets = new Assets();
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

	@Override
	public Unit getTarget() {
		return (Unit) super.getTarget();
	}

	public static PlayerCharacterFactory<Player> getFactory(PlayerId playerId) {
		return (name, phase, characterClass, race, level, baseStatInfo, combatRatingInfo, talents, professions, exclusiveFactions) -> new PlayerImpl(playerId, name, phase, characterClass, race, level, baseStatInfo, combatRatingInfo, talents, professions, exclusiveFactions);
	}
}
