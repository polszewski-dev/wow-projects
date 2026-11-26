package wow.character.model.character.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.character.model.ability.ShootAbility;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.effect.EffectCollector;
import wow.character.model.equipment.Equipment;
import wow.character.model.talent.Talents;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.Race;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.item.ItemSetBonus;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.talent.Talent;

import java.util.List;
import java.util.Objects;
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
	private final Race race;
	private final Side side;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;
	private final Talents talents;
	private final Spellbook spellbook;
	private final Equipment equipment;
	private final Consumables consumables;
	private final Buffs buffs;
	private PveRole role;
	private String script;
	private PetCharacter activePet;
	private Character target;
	private Character focus;

	private static final String IDLE_SCRIPT = "idle";

	public CharacterImpl(
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
		this.name = name;
		this.phase = phase;
		this.characterClass = characterClass;
		this.level = level;
		this.creatureType = creatureType;
		this.race = race;
		this.side = side;
		this.baseStatInfo = baseStatInfo;
		this.combatRatingInfo = combatRatingInfo;
		this.talents = new Talents(this, availableTalents);
		this.spellbook = spellbook;
		this.equipment = new Equipment(this);
		this.consumables = new Consumables(this);
		this.buffs = new Buffs(this);
	}

	protected CharacterImpl(
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
		this.name = name;
		this.phase = phase;
		this.characterClass = characterClass;
		this.level = level;
		this.creatureType = creatureType;
		this.race = race;
		this.side = side;
		this.baseStatInfo = baseStatInfo;
		this.combatRatingInfo = combatRatingInfo;
		this.talents = new Talents(this, availableTalents);
		this.spellbook = new Spellbook();
		this.equipment = new Equipment(this);
		this.consumables = new Consumables(this);
		this.buffs = new Buffs(this);
		this.script = IDLE_SCRIPT;
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
	public void setRole(PveRole role) {
		this.role = role;
	}

	@Override
	public void setScript(String script) {
		Objects.requireNonNull(script);
		this.script = script;
	}

	@Override
	public void resetBuild() {
		talents.reset();
		role = null;
		script = null;
		invalidateCaches();
	}

	@Override
	public void invalidateCaches() {
		// void
	}

	@Override
	public void setActivePet(PetCharacter activePet) {
		if (this instanceof PetCharacter) {
			throw new IllegalArgumentException("Pets cannnot have pets");
		}
		if (this.activePet != null) {
			this.activePet.setMaster(null);
		}
		this.activePet = activePet;
		if (this.activePet != null) {
			this.activePet.setMaster(this);
		}
	}

	@Override
	public void setTarget(Character target) {
		this.target = target;
	}

	@Override
	public void setFocus(Character focus) {
		this.focus = focus;
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		getTalents().collectEffects(collector);
		getEquipment().collectEffects(collector);
		getBuffs().collectEffects(collector);
		for (var racial : getRacials()) {
			collector.addEffect(racial);
		}
	}

	@Override
	public void itemStatsAdded(Item item) {
		//todo
	}

	@Override
	public void itemStatsRemoved(Item item) {
		//todo
	}

	@Override
	public void itemSetBonusAdded(ItemSetBonus setBonus) {
		//todo
	}

	@Override
	public void itemSetBonusRemoved(ItemSetBonus setBonus) {
		//todo
	}

	@Override
	public void enchantStatsAdded(Enchant enchant) {
		//todo
	}

	@Override
	public void enchantStatsRemoved(Enchant enchant) {
		//todo
	}

	@Override
	public void gemStatsAdded(Gem gem) {
		//todo
	}

	@Override
	public void gemStatsRemoved(Gem gem) {
		//todo
	}

	@Override
	public void socketBonusAdded(Effect socketBonus) {
		//todo
	}

	@Override
	public void socketBonusRemoved(Effect socketBonus) {
		//todo
	}

	@Override
	public String toString() {
		return name;
	}
}
