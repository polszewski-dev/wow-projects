package wow.simulator.model.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.Duration;
import wow.commons.model.spell.AbilityCooldownId;
import wow.commons.model.spell.CooldownId;
import wow.commons.model.spell.SpellCooldownId;
import wow.commons.model.spell.SpellId;
import wow.simulator.model.cooldown.CooldownInstance;
import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@RequiredArgsConstructor
@Getter
public class CooldownStats {
	private final CooldownId cooldownId;
	private Duration uptime = Duration.ZERO;

	public void addUptime(Duration duration) {
		this.uptime = uptime.add(duration);
	}

	public static boolean isSupported(CooldownInstance cooldown) {
		var cooldownId = cooldown.getCooldownId();
		return isSupported(cooldownId);
	}

	public static boolean isSupported(CooldownId cooldownId) {
		return cooldownId instanceof AbilityCooldownId || cooldownId instanceof SpellCooldownId;
	}

	public SpellId getSpellId(Player player) {
		return switch (cooldownId) {
			case AbilityCooldownId(var abilityId) -> {
				var ability = player.getAbility(abilityId).orElseThrow();
				yield ability.getId();
			}
			case SpellCooldownId(var spellId) -> spellId;
			default -> throw new IllegalArgumentException(cooldownId + "");
		};
	}
}
