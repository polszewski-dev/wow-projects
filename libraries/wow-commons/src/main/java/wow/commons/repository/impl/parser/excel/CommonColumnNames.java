package wow.commons.repository.impl.parser.excel;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public final class CommonColumnNames {
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ICON = "icon";
	public static final String TOOLTIP = "tooltip";

	public static final String REQ_VERSION = "req_version";
	public static final String REQ_PHASE = "req_phase";
	public static final String REQ_LEVEL = "req_level";
	public static final String REQ_CLASS = "req_class";
	public static final String REQ_RACE = "req_race";
	public static final String REQ_SIDE = "req_side";
	public static final String REQ_PROFESSION = "req_prof";
	public static final String REQ_PROFESSION_LEVEL = "req_prof_lvl";
	public static final String REQ_PROFESSION_SPEC = "req_prof_spec";
	public static final String REQ_XFACTION = "req_xfaction";
	public static final String REQ_PET = "req_pet";
	public static final String REQ_SPELL = "req_spell";
	public static final String REQ_TALENT = "req_talent";
	public static final String REQ_ROLE = "req_role";
	public static final String REQ_MAX_LEVEL = "req_level_max";

	public static final String PVE_ROLES = "pve_roles";

	private static final String ATTR_ID = "id";
	private static final String ATTR_VALUE = "value";
	private static final String ATTR_CONDITION = "condition";
	private static final String ATTR_SCALING = "scaling";

	public static String getAttrId(int idx) {
		return ATTR_ID + idx;
	}

	public static String getAttrValue(int idx) {
		return ATTR_VALUE + idx;
	}

	public static String getAttrCondition(int idx) {
		return ATTR_CONDITION + idx;
	}

	public static String getAttrScaling(int idx) {
		return ATTR_SCALING + idx;
	}

	public static String colEffectStats(String prefix, int statIdx) {
		return prefix + "stat" + (statIdx != 1 ? statIdx : "");
	}

	public static String colEffectDescr(String prefix, int statIdx) {
		return prefix + "descr" + (statIdx != 1 ? statIdx : "");
	}

	private CommonColumnNames() {}
}
