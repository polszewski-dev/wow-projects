package wow.commons.model.item;

/**
 * User: POlszewski
 * Date: 2025-09-12
 */
public sealed interface AbstractItemId permits ItemId, GemId, TradedItemId, ConsumableId {
	int value();
}
