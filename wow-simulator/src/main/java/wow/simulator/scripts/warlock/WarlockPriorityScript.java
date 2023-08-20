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
import static wow.simulator.scripts.warlock.WarlockActionConditions.CURSE_OF_DOOM_COND;
import static wow.simulator.scripts.warlock.WarlockActionConditions.SHADOW_BOLT_COND;

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
		return getPriorityList()
				.filter(x -> x.check(player))
				.map(ConditionalSpellCast::spellId)
				.findFirst()
				.orElseThrow();
	}

	private Stream<ConditionalSpellCast> getPriorityList() {
		return Stream.of(
				CURSE_OF_DOOM_COND,
				SHADOW_BOLT_COND
		);
	}

	private void equipSampleItems(Player player) {
		Gem meta = getGem(player, "Chaotic Skyfire Diamond");
		Gem red = getGem(player, 32196);// "Runed Crimson Spinel" - there are 2 with the same name
		Gem orange = getGem(player, "Reckless Pyrestone");
		Gem purple = getGem(player, "Glowing Shadowsong Amethyst");

		equip(player, "Dark Conjuror's Collar", "Glyph of Power").gem(meta, purple);
		equip(player, "Amulet of Unfettered Magics", null);
		equip(player, "Mantle of the Malefic", "Greater Inscription of the Orb").gem(purple, orange);
		equip(player, "Tattered Cape of Antonidas", "Enchant Cloak - Subtlety").gem(red);
		equip(player, "Sunfire Robe", "Enchant Chest - Exceptional Stats").gem(red, red, red);
		equip(player, "Bracers of the Malefic", "Enchant Bracer - Spellpower").gem(orange);
		equip(player, "Handguards of Defiled Worlds", "Enchant Gloves - Major Spellpower").gem(orange, red);
		equip(player, "Belt of the Malefic", null).gem(orange);
		equip(player, "Leggings of Calamity", "Runic Spellthread").gem(red, red, orange);
		equip(player, "Boots of the Malefic", "Enchant Boots - Boar's Speed").gem(orange);
		equip(player, ItemSlot.FINGER_1, "Ring of Omnipotence", "Enchant Ring - Spellpower");
		equip(player, ItemSlot.FINGER_2, "Loop of Forged Power", "Enchant Ring - Spellpower");
		equip(player, ItemSlot.TRINKET_1, "The Skull of Gul'dan", null);
		equip(player, ItemSlot.TRINKET_2, "Shifting Naaru Sliver", null);
		equip(player, "Sunflare", "Enchant Weapon - Soulfrost");
		equip(player, "Heart of the Pit", null);
		equip(player, "Wand of the Demonsoul", null).gem(orange);
	}

	private EquippableItem equip(Player player, String itemName, String enchantName) {
		EquippableItem equippableItem = getEquippableItem(player, itemName, enchantName);
		player.equip(equippableItem);
		return equippableItem;
	}

	private EquippableItem equip(Player player, ItemSlot slot, String itemName, String enchantName) {
		EquippableItem equippableItem = getEquippableItem(player, itemName, enchantName);
		player.equip(equippableItem, slot);
		return equippableItem;
	}

	private EquippableItem getEquippableItem(Player player, String itemName, String enchantName) {
		PhaseId phaseId = player.getCharacter().getPhaseId();
		Item item = getItemRepository().getItem(itemName, phaseId).orElseThrow();

		EquippableItem equippableItem = new EquippableItem(item);

		if (enchantName != null) {
			Enchant enchant = getItemRepository().getEnchant(enchantName, phaseId).orElseThrow();
			equippableItem.enchant(enchant);
		}
		return equippableItem;
	}

	private Gem getGem(Player player, String name) {
		PhaseId phaseId = player.getCharacter().getPhaseId();
		return getItemRepository().getGem(name, phaseId).orElseThrow();
	}

	private Gem getGem(Player player, int gemId) {
		PhaseId phaseId = player.getCharacter().getPhaseId();
		return getItemRepository().getGem(gemId, phaseId).orElseThrow();
	}
}
