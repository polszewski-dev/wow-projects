package wow.simulator.scripts.warlock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.character.CharacterTemplateId;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.Duration;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.item.Enchant;
import wow.commons.model.item.Gem;
import wow.commons.model.item.Item;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spells.SpellId;
import wow.simulator.config.SimulatorContext;
import wow.simulator.config.SimulatorContextSource;
import wow.simulator.model.unit.Player;
import wow.simulator.scripts.AIScript;
import wow.simulator.scripts.ConditionalSpellCast;

import java.util.stream.Stream;

import static wow.commons.model.spells.SpellId.LIFE_TAP;
import static wow.commons.model.spells.SpellId.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
@AllArgsConstructor
@Getter
public class WarlockPriorityScript implements AIScript, SimulatorContextSource {
	private final SimulatorContext simulatorContext;

	@Override
	public void setupPlayer(Player player) {
		getCharacterService().applyCharacterTemplate(player.getCharacter(), CharacterTemplateId.DESTRO_SHADOW);
		equipSampleItems(player);
	}

	@Override
	public void execute(Player player) {
		SpellId spellToCast = getSpellToCast(player);

		if (player.canCast(spellToCast)) {
			player.cast(spellToCast);
		} else if (player.canCast(LIFE_TAP)) {
			player.cast(LIFE_TAP);
		} else {
			player.idleFor(Duration.seconds(1));
		}
	}

	private SpellId getSpellToCast(Player player) {
		for (var conditionalSpellCast : getPriorityList()) {
			if (conditionalSpellCast.check(player)) {
				return conditionalSpellCast.spellToCast();
			}
		}

		return SHADOW_BOLT;
	}

	private ConditionalSpellCast[] getPriorityList() {
		return new ConditionalSpellCast[] {
				// no spells atm
		};
	}

	private void equipSampleItems(Player player) {
		// no items atm
	}

	private void equip(Player player, String itemName, String enchantName, String... gemNames) {
		EquippableItem item = getItem(player, itemName, enchantName, gemNames);

		player.equip(item);
	}

	private void equip(Player player, ItemSlot slot, String itemName, String enchantName, String... gemNames) {
		EquippableItem item = getItem(player, itemName, enchantName, gemNames);

		player.equip(item, slot);
	}

	private EquippableItem getItem(Player player, String itemName, String enchantName, String[] gemNames) {
		PhaseId phaseId = player.getCharacter().getPhaseId();
		return getItem(phaseId, itemName, enchantName, gemNames);
	}

	private EquippableItem getItem(PhaseId phaseId, String itemName, String enchantName, String... gemNames) {
		Item item = getItemRepository().getItem(itemName, phaseId).orElseThrow();
		Enchant enchant = null;
		if (enchantName != null) {
			enchant = getItemRepository().getEnchant(enchantName, phaseId).orElseThrow();
		}
		var gems = Stream.of(gemNames)
				.map(x -> getItemRepository().getGem(x, phaseId).orElseThrow())
				.toArray(Gem[]::new);
		return new EquippableItem(item).enchant(enchant).gem(gems);
	}
}
