package wow.commons.model.pve;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public class Dungeon extends Instance {
	public Dungeon(int no, String name, GameVersion version, int phase, String shortName) {
		super(no, name, 5, version, phase, shortName);
	}
}
