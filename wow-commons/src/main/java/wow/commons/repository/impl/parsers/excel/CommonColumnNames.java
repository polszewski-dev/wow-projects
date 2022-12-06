package wow.commons.repository.impl.parsers.excel;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
public final class CommonColumnNames {
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
	public static final String REQ_TALENT = "req_talent";

	public static final String STAT = "stat";
	public static final String AMOUNT = "amount";

	public static String colStat(String prefix, int statIdx) {
		return prefix + STAT + statIdx;
	}

	public static String colAmount(String prefix, int statIdx) {
		return prefix + AMOUNT + statIdx;
	}

	private CommonColumnNames() {}
}
