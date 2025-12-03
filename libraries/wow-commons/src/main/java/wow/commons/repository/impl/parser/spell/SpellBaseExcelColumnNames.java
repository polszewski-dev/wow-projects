package wow.commons.repository.impl.parser.spell;

/**
 * User: POlszewski
 * Date: 2023-09-15
 */
public final class SpellBaseExcelColumnNames {
	public static final String SPELL_TYPE = "type";

	public static final String ABILITY_RANK = "rank";
	public static final String ABILITY_TREE = "tree";
	public static final String ABILITY_CATEGORY = "category";

	public static final String TARGET = "target";

	public static final String CAST_TIME = "cast: time";
	public static final String CAST_CHANNELED = "cast: channeled";
	public static final String CAST_IGNORES_GCD = "cast: ignores gcd";
	public static final String COST_AMOUNT = "amount";
	public static final String COST_TYPE = "type";
	public static final String COST_BASE_STAT_PCT = "base stat%";
	public static final String COST_REAGENT = "reagent";
	public static final String COOLDOWN = "cooldown";
	public static final String COOLDOWN_GROUP = "cooldown: group";
	public static final String RANGE = "range";
	public static final String BOLT = "bolt";
	public static final String EFFECT_REMOVED_ON_HIT = "effect removed on hit";
	public static final String DIRECT_TYPE = "type";
	public static final String DIRECT_MIN = "min";
	public static final String DIRECT_MAX = "max";
	public static final String DIRECT_BONUS_MIN = "min bonus";
	public static final String DIRECT_BONUS_MAX = "max bonus";
	public static final String DIRECT_BONUS_REQUIRED_EFFECT = "bonus required effect";
	public static final String PERIODIC_TYPE = "type";
	public static final String PERIODIC_AMOUNT = "amount";
	public static final String PERIODIC_NUM_TICKS = "num ticks";
	public static final String PERIODIC_TICK_INTERVAL = "tick interval";
	public static final String PERIODIC_TICK_WEIGHTS = "tick weights";
	public static final String AUGMENTED_ABILITY = "augmented ability";
	public static final String STACKS_MAX = "stacks: max";
	public static final String SCOPE = "scope";
	public static final String EXCLUSION_GROUP = "exclusion group";
	public static final String PREVENTED_SCHOOLS = "prevented schools";
	public static final String ABSORB_CONDITION = "condition";
	public static final String ABSORB_MIN = "min";
	public static final String ABSORB_MAX = "max";
	public static final String STAT_CONVERSION_FROM = "from";
	public static final String STAT_CONVERSION_TO = "to";
	public static final String STAT_CONVERSION_TO_CONDITION = "to condition";
	public static final String STAT_CONVERSION_RATIO = "%";
	public static final String EVENT_ON = "on";
	public static final String EVENT_CONDITION = "condition";
	public static final String EVENT_CHANCE_PCT = "chance%";
	public static final String EVENT_ACTION = "action";
	public static final String EVENT_COOLDOWN = "cd";
	public static final String EVENT_TRIGGERED_SPELL = "triggered spell";
	public static final String EVENT_ACTION_PARAMS = "action params";

	public static final String APPLIED_EFFECT_ID = "id";
	public static final String APPLIED_EFFECT_DURATION = "duration";
	public static final String APPLIED_EFFECT_STACKS = "stacks";
	public static final String APPLIED_EFFECT_CHARGES = "charges";
	public static final String APPLIED_EFFECT_REPLACEMENT_MODE = "replacement mode";

	public static final String COST_PREFIX = "cost: ";
	public static final String PERIODIC_PREFIX = "periodic: ";
	public static final String MOD_PREFIX = "mod: ";
	public static final String ABSORB_PREFIX = "absorb: ";

	public static final String COEFF_VALUE = "coeff";
	public static final String COEFF_SCHOOL = "school";

	public static final String TALENT_RANK = "rank";
	public static final String TALENT_MAX_RANK = "max rank";
	public static final String TALENT_CALCULATOR_POSITION = "talent calculator position";
	public static final String TALENT_TREE = "tree";

	public static String getDirectCommandPrefix(int idx) {
		return "direct" + idx + ": ";
	}

	public static String getApplyEffectCommandPrefix(int idx) {
		return "apply" + idx + ": ";
	}

	public static String getPeriodicCommandPrefix(int idx) {
		return "periodic" + idx + ": ";
	}

	public static String getStatConversionPrefix(int idx) {
		return "stat conversion" + idx + ": ";
	}

	public static String getEventPrefix(int idx) {
		return "event" + idx + ": ";
	}

	public static final String EFFECT_ID = "id";

	private SpellBaseExcelColumnNames() {}
}
