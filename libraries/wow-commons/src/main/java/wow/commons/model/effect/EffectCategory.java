package wow.commons.model.effect;

import lombok.AllArgsConstructor;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2023-10-14
 */
@AllArgsConstructor
public enum EffectCategory {
	FEARS("Fears"),
	SNARES("Snares"),
	STUNS("Stuns"),
	DISARMS("Disarms"),
	SILENCES("Silences"),
	INTERRUPTS("Interrupts"),
	DISORIENTS("Disorients"),
	DISEASES("Diseases");

	private final String name;

	public static EffectCategory parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.name);
	}

	public static EffectCategory tryParse(String value) {
		return EnumUtil.tryParse(value, values(), x -> x.name);
	}

	@Override
	public String toString() {
		return name;
	}
}
