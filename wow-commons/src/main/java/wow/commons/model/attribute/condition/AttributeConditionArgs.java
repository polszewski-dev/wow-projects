package wow.commons.model.attribute.condition;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.character.*;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.spell.AbilityCategory;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
@RequiredArgsConstructor
@Getter
@Setter
public class AttributeConditionArgs {
	private final ActionType actionType;
	private TalentTree talentTree;
	private SpellSchool spellSchool;
	private AbilityId abilityId;
	private AbilityCategory abilityCategory;

	private PowerType powerType;

	private Duration baseCastTime;
	private boolean hasManaCost;
	private boolean canCrit;
	private boolean hadCrit;

	private boolean direct;
	private boolean periodic;

	private boolean hasDamagingComponent;
	private boolean hasHealingComponent;

	private Percent ownerHealth;

	private boolean targetingOthers;

	private CreatureType targetType;
	private CharacterClassId targetClass;
	private Percent targetHealth;

	private PetType petType;
	private DruidFormType druidForm;

	private WeaponSubType weaponType;
	private ProfessionId professionId;

	private Set<AbilityId> ownerEffects = Set.of();
	private AbilityId channeledAbility;
	private EffectCategory effectCategory;
	private boolean hostileSpell;
	private boolean normalMeleeAttack;
	private boolean specialAttack;
	private MovementType movementType = MovementType.RUNNING;

	public boolean hasOwnerEffect(AbilityId abilityId) {
		return ownerEffects.contains(abilityId);
	}

	public boolean isOwnerChannelig(AbilityId abilityId) {
		return channeledAbility == abilityId;
	}
}
