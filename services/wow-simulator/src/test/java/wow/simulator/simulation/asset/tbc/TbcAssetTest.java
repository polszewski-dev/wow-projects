package wow.simulator.simulation.asset.tbc;

import wow.simulator.simulation.asset.AssetTest;

import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
public abstract class TbcAssetTest extends AssetTest {
	@Override
	protected void setDefaults() {
		phaseId = TBC_P5;
	}

	protected void setUpFireMage(String... enabledAssets) {
		setAssetParams(MAGE, UNDEAD, "https://www.wowhead.com/tbc/talent-calc/mage/2-5052120123033310531251-053002001", enabledAssets);
	}

	protected void setUpShadowPriest(String... enabledAssets) {
		setAssetParams(PRIEST, UNDEAD, "https://www.wowhead.com/tbc/talent-calc/priest/500230013--503250510240103051451", enabledAssets);
	}

	protected void setUpDisciplinePriest(String... enabledAssets) {
		setAssetParams(PRIEST, UNDEAD, "https://www.wowhead.com/tbc/talent-calc/priest/5002303130505120001551-2330500303", enabledAssets);
	}

	protected void setUpDestroWarlock(String... enabledAssets) {
		setAssetParams(WARLOCK, ORC, "https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025", enabledAssets);
	}

	protected void setUpAfflictionWarlock(String... enabledAssets) {
		setAssetParams(WARLOCK, ORC, "https://www.wowhead.com/tbc/talent-calc/warlock/55022000102351055103--50500051220001", enabledAssets);
	}

	protected void setUpBalanceDruid(String... enabledAssets) {
		setAssetParams(DRUID, TAUREN, "https://www.wowhead.com/tbc/talent-calc/druid/510022312503135231351--500233", enabledAssets);
	}

	protected void setUpElementalShaman(String... enabledAssets) {
		setAssetParams(SHAMAN, ORC, "https://www.wowhead.com/tbc/talent-calc/shaman/55003105100213351051--05105301005", enabledAssets);
	}

	protected void setUpHolyPaladin(String... enabledAssets) {
		setAssetParams(PALADIN, BLOOD_ELF, "https://www.wowhead.com/tbc/talent-calc/paladin/05503121520132531051-500231-5", enabledAssets);
	}

	protected void setUpRetributionPaladin(String... enabledAssets) {
		setAssetParams(PALADIN, BLOOD_ELF, "https://www.wowhead.com/tbc/talent-calc/paladin/5-053201-0523005120033125331051", enabledAssets);
	}
}
