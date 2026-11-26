package wow.character.model.character;

import wow.character.model.asset.Asset;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.buff.Buff;
import wow.commons.model.effect.Effect;
import wow.commons.model.item.*;
import wow.commons.model.talent.Talent;

/**
 * User: POlszewski
 * Date: 2026-11-25
 */
public interface TestEvent {
	record ItemStatsAdded(Item item) implements TestEvent {
		public ItemStatsAdded(EquippableItem equippableItem) {
			this(equippableItem.getItem());
		}
	}

	record ItemStatsRemoved(Item item) implements TestEvent {
		public ItemStatsRemoved(EquippableItem equippableItem) {
			this(equippableItem.getItem());
		}
	}

	record ItemSetBonusAdded(ItemSetBonus setBonus) implements TestEvent {}

	record ItemSetBonusRemoved(ItemSetBonus setBonus) implements TestEvent {}

	record EnchantStatsAdded(Enchant enchant) implements TestEvent {}

	record EnchantStatsRemoved(Enchant enchant) implements TestEvent {}

	record GemStatsAdded(Gem gem) implements TestEvent {}

	record GemStatsRemoved(Gem gem) implements TestEvent {}

	record SocketBonusAdded(Effect socketBonus) implements TestEvent {}

	record SocketBonusRemoved(Effect socketBonus) implements TestEvent {}

	record TalentEnabled(Talent talent) implements TestEvent {}

	record TalentDisabled(Talent talent) implements TestEvent {}

	record BuffEnabled(Buff buff) implements TestEvent {}

	record BuffDisabled(Buff buff) implements TestEvent {}

	record ConsumableEnabled(Consumable consumable) implements TestEvent {}

	record ConsumableDisabled(Consumable consumable) implements TestEvent {}

	record AssetEnabled(Asset asset) implements TestEvent {}

	record AssetDisabled(Asset asset) implements TestEvent {}
}
