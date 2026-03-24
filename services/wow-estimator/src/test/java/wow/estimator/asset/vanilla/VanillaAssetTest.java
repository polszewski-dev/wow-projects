package wow.estimator.asset.vanilla;

import wow.estimator.asset.AssetTest;

import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;
import static wow.commons.model.pve.PhaseId.VANILLA_P6;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
public abstract class VanillaAssetTest extends AssetTest {
	@Override
	protected void setDefaults() {
		phaseId = VANILLA_P6;
		level = 60;
	}

	protected void setUpFireMage(String... enabledAssets) {
		setAssetParams(MAGE, UNDEAD, "https://www.wowhead.com/classic/talent-calc/mage/230025030002-5052000123033151-003", enabledAssets);
	}

	protected void setUpShadowPriest(String... enabledAssets) {
		setAssetParams(PRIEST, UNDEAD, "https://www.wowhead.com/classic/talent-calc/priest/50520013--5032504103501051", enabledAssets);
	}

	protected void setUpDisciplinePriest(String... enabledAssets) {
		setAssetParams(PRIEST, UNDEAD, "https://www.wowhead.com/classic/talent-calc/priest/053210130505151--05025021004", enabledAssets);
	}

	protected void setUpDestroWarlock(String... enabledAssets) {
		setAssetParams(WARLOCK, ORC, "https://www.wowhead.com/classic/talent-calc/warlock/25002-2050300142301-52500051020001", enabledAssets);
	}

	protected void setUpAfflictionWarlock(String... enabledAssets) {
		setAssetParams(WARLOCK, ORC, "https://www.wowhead.com/classic/talent-calc/warlock/5500203412201005--50502051020001", enabledAssets);
	}

	protected void setUpBalanceDruid(String... enabledAssets) {
		setAssetParams(DRUID, TAUREN, "https://www.wowhead.com/classic/talent-calc/druid/5001500202551351--505103101", enabledAssets);
	}

	protected void setUpElementalShaman(String... enabledAssets) {
		setAssetParams(SHAMAN, ORC, "https://www.wowhead.com/classic/talent-calc/shaman/550331050002151--05204301005", enabledAssets);
	}

	protected void setUpHolyPaladin(String... enabledAssets) {
		setAssetParams(PALADIN, HUMAN, "https://www.wowhead.com/classic/talent-calc/paladin/05503122521351-503201-5", enabledAssets);
	}

	protected void setUpRetributionPaladin(String... enabledAssets) {
		setAssetParams(PALADIN, HUMAN, "https://www.wowhead.com/classic/talent-calc/paladin/505001-503-542300512003151", enabledAssets);
	}
}
