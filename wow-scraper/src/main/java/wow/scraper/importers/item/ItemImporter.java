package wow.scraper.importers.item;

import wow.scraper.importers.WowheadImporter;

import java.io.IOException;

import static wow.scraper.model.WowheadItemCategory.*;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
public class ItemImporter extends WowheadImporter {
	@Override
	public void importAll() throws IOException {
		fetch("items/armor/cloth/slot:5", CHEST);
		fetch("items/armor/cloth/slot:8", FEET);
		fetch("items/armor/cloth/slot:10", HANDS);
		fetch("items/armor/cloth/slot:1", HEAD);
		fetch("items/armor/cloth/slot:7", LEGS);
		fetch("items/armor/cloth/slot:3", SHOULDER);
		fetch("items/armor/cloth/slot:6", WAIST);
		fetch("items/armor/cloth/slot:9", WRIST);
		fetch("items/armor/amulets/slot:9", AMULETS);
		fetch("items/armor/rings", RINGS);
		fetch("items/armor/trinkets", TRINKETS);
		fetch("items/armor/cloaks", CLOAKS);
		fetch("items/armor/off-hand-frills", OFF_HANDS);
		fetch("items/weapons/daggers", DAGGERS);
		fetch("items/weapons/one-handed-swords", ONE_HANDED_SWORDS);
		fetch("items/weapons/one-handed-maces", ONE_HANDED_MACES);
		fetch("items/weapons/staves", STAVES);
		fetch("items/weapons/wands", WANDS);
	}
}
