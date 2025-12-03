package wow.commons.model.spell;

/**
 * User: POlszewski
 * Date: 2025-11-28
 */
public final class SpellTargets {
	public static final SpellTarget SELF = new SpellTarget(SpellTargetType.SELF);
	public static final SpellTarget PET = new SpellTarget(SpellTargetType.PET);

	public static final SpellTarget FRIEND = new SpellTarget(SpellTargetType.FRIEND);
	public static final SpellTarget FRIEND_AOE = new SpellTarget(SpellTargetType.FRIEND_AOE);
	public static final SpellTarget FRIENDS_PARTY = new SpellTarget(SpellTargetType.FRIENDS_PARTY);

	public static final SpellTarget PARTY = new SpellTarget(SpellTargetType.PARTY);
	public static final SpellTarget PARTY_AOE = new SpellTarget(SpellTargetType.PARTY_AOE);

	public static final SpellTarget ENEMY = new SpellTarget(SpellTargetType.ENEMY);
	public static final SpellTarget ENEMY_AOE = new SpellTarget(SpellTargetType.ENEMY_AOE);

	public static final SpellTarget ANY = new SpellTarget(SpellTargetType.ANY);

	public static final SpellTarget TARGET = new SpellTarget(SpellTargetType.TARGET);
	public static final SpellTarget ATTACKER = new SpellTarget(SpellTargetType.ATTACKER);

	public static final SpellTarget GROUND = new SpellTarget(SpellTargetType.GROUND);

	private SpellTargets() {}
}
