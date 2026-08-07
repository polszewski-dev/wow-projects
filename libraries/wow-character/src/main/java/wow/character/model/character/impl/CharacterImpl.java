package wow.character.model.character.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.ability.ShootAbility;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.equipment.Equipment;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;

import java.util.Optional;

import static java.util.function.Function.identity;
import static wow.commons.model.categorization.ItemSlot.RANGED;
import static wow.commons.model.spell.AbilityId.SHOOT;

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
	private final CreatureType creatureType;
	private final Side side;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;
	private final Spellbook spellbook;
	private final Equipment equipment;
	private final Consumables consumables;
	private Character target;

	protected CharacterImpl(String name, Phase phase, CharacterClass characterClass, int level, CreatureType creatureType, Side side, BaseStatInfo baseStatInfo, CombatRatingInfo combatRatingInfo) {
		this.name = name;
		this.phase = phase;
		this.characterClass = characterClass;
		this.level = level;
		this.creatureType = creatureType;
		this.side = side;
		this.baseStatInfo = baseStatInfo;
		this.combatRatingInfo = combatRatingInfo;
		this.spellbook = new Spellbook();
		this.equipment = new Equipment();
		this.consumables = new Consumables();
	}

	@Override
	public Optional<Ability> getAbility(AbilityId abilityId) {
		var ability = getSpellbook().getAbility(abilityId);

		if (ability.isPresent()) {
			return ability.map(this::replaceShoot);
		}

		ability = getEquipment().getAbility(abilityId).map(identity());

		if (ability.isPresent()) {
			return ability;
		}

		return getConsumables().getAbility(abilityId).map(identity());
	}

	private Ability replaceShoot(Ability ability) {
		if (!ability.getAbilityId().equals(SHOOT)) {
			return ability;
		}

		var rangedWeapon = getEquippedItem(RANGED);

		if (rangedWeapon == null || rangedWeapon.getWeaponStats() == null) {
			return null;
		}

		return new ShootAbility(ability, rangedWeapon);
	}

	@Override
	public void setTarget(Character target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return name;
	}
}
