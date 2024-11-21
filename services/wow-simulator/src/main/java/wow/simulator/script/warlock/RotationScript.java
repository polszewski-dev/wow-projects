package wow.simulator.script.warlock;

import lombok.RequiredArgsConstructor;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.script.AIScript;
import wow.simulator.script.ConditionalSpellCast;

import java.util.List;
import java.util.stream.Stream;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.spell.AbilityId.*;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
@RequiredArgsConstructor
public class RotationScript implements AIScript {
	private final Player player;
	private List<AbilityId> cooldowns;
	private AbilityId filler;
	private List<AbilityId> activatedAbilities;
	private List<AbilityId> consumes;
	private List<AbilityId> racialAbilities;

	@Override
	public void setupPlayer() {
		this.cooldowns = getCooldowns();
		this.filler = getFiller();
		this.activatedAbilities = getActivatedAbilities();
		this.consumes = getConsumes();
		this.racialAbilities = getRacialAbilities();
	}

	@Override
	public void execute() {
		player.increaseHealth(1000, false, null);
		player.increaseMana(1000, false, null);

		var spell = getSpellToCast();

		if (player.canCast(spell)) {
			player.cast(spell);
		} else if (player.canCast(LIFE_TAP)) {
			player.cast(LIFE_TAP);
		} else {
			player.idleFor(Duration.seconds(1));
		}
	}

	private AbilityId getSpellToCast() {
		for (var abilityId : activatedAbilities) {
			if (player.canCast(abilityId)) {
				return abilityId;
			}
		}

		for (var abilityId : consumes) {
			if (player.canCast(abilityId)) {
				return abilityId;
			}
		}

		for (var abilityId : racialAbilities) {
			if (player.canCast(abilityId)) {
				return abilityId;
			}
		}

		for (var abilityId : cooldowns) {
			if (getConditionalCast(abilityId).check(player)) {
				return abilityId;
			}
		}

		return filler;
	}

	private ConditionalSpellCast getConditionalCast(AbilityId abilityId) {
		if (player.getCharacterClassId() == WARLOCK) {
			return WarlockActionConditions.forAbility(abilityId).orElseThrow();
		}
		throw new IllegalArgumentException();
	}

	private AbilityId getFiller() {
		return player.getRotation().getFiller().getAbilityId();
	}

	private List<AbilityId> getCooldowns() {
		return player.getRotation().getCooldowns().stream().map(Ability::getAbilityId).toList();
	}

	private List<AbilityId> getActivatedAbilities() {
		return player.getEquipment().toList()
				.stream()
				.filter(EquippableItem::hasActivatedAbility)
				.map(EquippableItem::getActivatedAbility)
				.map(Ability::getAbilityId)
				.toList();
	}

	private List<AbilityId> getConsumes() {
		return Stream.of(DESTRUCTION_POTION)
				.filter(player::hasAbility)
				.toList();
	}

	private List<AbilityId> getRacialAbilities() {
		return Stream.of(BLOOD_FURY, BERSERKING)
				.filter(player::hasAbility)
				.toList();
	}
}
