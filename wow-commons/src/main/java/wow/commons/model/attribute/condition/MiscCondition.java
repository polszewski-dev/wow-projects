package wow.commons.model.attribute.condition;

import lombok.AllArgsConstructor;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-09-23
 */
@AllArgsConstructor
public enum MiscCondition implements AttributeCondition {
	SPELL("Spell") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getActionType() == ActionType.SPELL;
		}
	},
	PHYSICAL("Physical") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getActionType() == ActionType.PHYSICAL;
		}
	},

	SPELL_DAMAGE("SpellDamage") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getPowerType() == PowerType.SPELL_DAMAGE;
		}
	},
	HEALING("Healing") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getPowerType() == PowerType.HEALING;
		}
	},
	MELEE("Melee") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getPowerType() == PowerType.MELEE;
		}
	},
	RANGED("Ranged") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getPowerType() == PowerType.RANGED;
		}
	},
	WEAPON("Weapon") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getPowerType() == PowerType.WEAPON;
		}
	},

	DIRECT("Direct") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isDirect();
		}
	},
	PERIODIC("Periodic") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isPeriodic();
		}
	},

	HAS_DAMAGING_COMPONENT("HasDamagingComponent") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isHasDamagingComponent();
		}
	},
	HAS_HEALING_COMPONENT("HasHealingComponent") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isHasHealingComponent();
		}
	},

	HOSTILE_SPELL("HostileSpell") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isHostileSpell();
		}
	},
	NORMAL_MELEE_ATTACK("NormalMeleeAttack") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isNormalMeleeAttack();
		}
	},
	SPECIAL_ATTACK("SpecialAttack") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isSpecialAttack();
		}
	},

	HAS_MANA_COST("HasManaCost") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isHasManaCost();
		}
	},
	HAS_CAST_TIME("HasCastTime") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getBaseCastTime().isPositive();
		}
	},
	IS_INSTANT_CAST("IsInstantCast") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getBaseCastTime().isZero();
		}
	},
	HAS_CAST_TIME_UNDER_10_SEC("HasCastTimeUnder10Sec") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getBaseCastTime().getSeconds() < 10;
		}
	},

	CAN_CRIT("CanCrit") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isCanCrit();
		}
	},
	HAD_CRITICAL("HadCrit") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isHadCrit();
		}
	},
	HAD_NO_CRITICAL("HadNoCrit") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isCanCrit() && !args.isHadCrit();
		}
	},

	HAS_PET("HasPet") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getPetType() != null;
		}
	},

	TARGETING_OTHERS("TargetingOthers") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.isTargetingOthers();
		}
	},

	OWNER_HEALTH_BELOW_20("OwnerHealthBelow20%") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getOwnerHealth().value() < 20;
		}
	},
	OWNER_HEALTH_BELOW_35("OwnerHealthBelow35%") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getOwnerHealth().value() < 3;
		}
	},
	OWNER_HEALTH_BELOW_40("OwnerHealthBelow40%") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getOwnerHealth().value() < 40;
		}
	},
	OWNER_HEALTH_BELOW_70("OwnerHealthBelow70%") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getOwnerHealth().value() < 70;
		}
	},
	TARGET_HEALTH_BELOW_50("TargetHealthBelow50%") {
		@Override
		public boolean test(AttributeConditionArgs args) {
			return args.getTargetHealth().value() < 50;
		}
	},

	;

	private final String key;

	public static MiscCondition parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	public static MiscCondition tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
