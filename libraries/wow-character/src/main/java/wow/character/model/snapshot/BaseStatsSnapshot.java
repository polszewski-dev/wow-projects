package wow.character.model.snapshot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User: POlszewski
 * Date: 2023-10-21
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BaseStatsSnapshot {
	private int strength;
	private int agility;
	private int stamina;
	private int intellect;
	private int spirit;
	private int maxHealth;
	private int maxMana;

	public BaseStatsSnapshot difference(BaseStatsSnapshot other) {
		return new BaseStatsSnapshot(
				strength - other.strength,
				agility - other.agility,
				stamina - other.stamina,
				intellect - other.intellect,
				spirit - other.spirit,
				maxHealth - other.maxHealth,
				maxMana - other.maxMana
		);
	}
}
