package wow.simulator.script.warlock;

import lombok.RequiredArgsConstructor;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.unit.Player;
import wow.simulator.script.AIScript;
import wow.simulator.script.ConditionalSpellCast;

import java.util.List;

import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.spell.AbilityId.LIFE_TAP;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
@RequiredArgsConstructor
public class RotationScript implements AIScript {
	private final Player player;
	private List<Ability> rotationCooldowns;
	private Ability filler;
	//todo onUse, racials, talents (?)

	@Override
	public void setupPlayer() {
		this.rotationCooldowns = player.getRotation().getCooldowns();
		this.filler = player.getRotation().getFiller();
	}

	@Override
	public void execute() {
		player.increaseMana(1000, false, null);

		var spell = getSpellToCast();

		if (player.canCast(spell)) {
			player.cast(spell);
		} else if (player.canCast(LIFE_TAP)) {
			player.cast(LIFE_TAP);
		} else {
			player.idleFor(Duration.seconds(1));
		}
	}

	private AbilityId getSpellToCast() {
		for (var ability : rotationCooldowns) {
			if (getConditionalCast(ability).check(player)) {
				return ability.getAbilityId();
			}
		}

		return filler.getAbilityId();
	}

	private ConditionalSpellCast getConditionalCast(Ability ability) {
		if (player.getCharacterClassId() == WARLOCK) {
			return WarlockActionConditions.forAbility(ability.getAbilityId()).orElseThrow();
		}
		throw new IllegalArgumentException();
	}
}
