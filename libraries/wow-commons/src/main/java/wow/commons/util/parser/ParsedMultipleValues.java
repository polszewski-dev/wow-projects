package wow.commons.util.parser;

/**
 * User: POlszewski
 * Date: 2022-12-10
 */
public class ParsedMultipleValues {
	private final String[] values;

	public static final ParsedMultipleValues EMPTY = new ParsedMultipleValues(null);

	public ParsedMultipleValues(String[] values) {
		this.values = values;
	}

	public boolean isEmpty() {
		return values == null;
	}

	public String get(int idx) {
		return values[idx];
	}

	public int getInteger(int idx) {
		return Integer.parseInt(values[idx]);
	}

	public double getDouble(int idx) {
		return Double.parseDouble(values[idx]);
	}

	public int size() {
		return values != null ? values.length : 0;
	}
}
