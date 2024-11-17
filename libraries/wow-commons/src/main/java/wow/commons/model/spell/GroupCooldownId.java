package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2024-11-13
 */
public record GroupCooldownId(CooldownGroup group) implements CooldownId {
	public static final GroupCooldownId TRINKET = new GroupCooldownId(CooldownGroup.TRINKET);
}
