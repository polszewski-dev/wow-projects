package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public record GroupCooldownId(CooldownGroup group) implements CooldownId {
	public static GroupCooldownId of(CooldownGroup group) {
		if (group == null) {
			return null;
		}
		return switch (group) {
			case POTION -> POTION;
			case CONJURED_ITEM -> CONJURED_ITEM;
			case TRINKET -> TRINKET;
		};
	}

	public static final GroupCooldownId POTION = new GroupCooldownId(CooldownGroup.POTION);
	public static final GroupCooldownId CONJURED_ITEM = new GroupCooldownId(CooldownGroup.CONJURED_ITEM);
	public static final GroupCooldownId TRINKET = new GroupCooldownId(CooldownGroup.TRINKET);
}
