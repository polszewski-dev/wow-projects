package wow.commons.model.pve;

/**
 * User: POlszewski
 * Date: 2021-03-13
 */
public class Raid extends Instance {
	public Raid(int no, String name, int partySize, GameVersion version, Phase phase, String shortName) {
		super(no, name, partySize, version, phase, shortName);
	}
}
