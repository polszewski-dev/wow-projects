package wow.estimator.asset;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.character.Raid;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.pve.PhaseId;
import wow.estimator.WowEstimatorSpringTest;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Player;
import wow.estimator.model.Unit;
import wow.estimator.service.PlayerService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2025-12-13
 */
public abstract class AssetTest extends WowEstimatorSpringTest {
	@Autowired
	PlayerService playerService;

	protected CharacterClassId assetPlayerClassId;
	protected RaceId assetPlayerRaceId;
	protected String assetPlayerTalentLink;
	protected List<String> enabledAssets;

	protected PhaseId phaseId;
	protected int level;
	protected RaceId raceId = RACE;

	protected Player player;
	protected NonPlayer target;
	private Player partyAsset;

	protected boolean assetOnly;

	@BeforeEach
	void setUp() {
		setDefaults();
		beforeSetUp();

		partyAsset = createAsset();
		player = createPlayer();
		target = getTarget(level, phaseId);

		var raid = new Raid<Player>();

		if (assetOnly) {
			raid.add(partyAsset);
		} else {
			raid.add(player, partyAsset);
		}

		playerService.getPlayer(raid, target);
	}

	private Player createAsset() {
		var partyAsset = getNakedPlayer("Asset", assetPlayerClassId, assetPlayerRaceId, level, phaseId);

		if (assetPlayerTalentLink != null) {
			partyAsset.getTalents().loadFromTalentLink(assetPlayerTalentLink);
			characterService.updateAfterRestrictionChange(partyAsset);
		}

		partyAsset.getAssets().setNames(enabledAssets);

		return partyAsset;
	}

	private Player createPlayer() {
		if (assetOnly) {
			return partyAsset;
		} else {
			return getNakedPlayer("Player", CharacterClassId.WARLOCK, raceId, level, phaseId);
		}
	}

	protected void setDefaults() {}

	protected void beforeSetUp() {}

	protected void assertHasEffect(Unit unit, String effectName) {
		assertHasEffect(unit, effectName, 1);
	}

	protected void assertHasEffect(Unit unit, String effectName, int numStacks) {
		var optionalEffectInstance = unit.getEffectInstances().get(effectName);

		assertThat(optionalEffectInstance).withFailMessage("No effect '%s' on %s".formatted(effectName, unit)).isPresent();

		var effectInstance = optionalEffectInstance.orElseThrow();

		assertThat(effectInstance.numStacks()).isEqualTo(numStacks);
	}

	protected void setAssetParams(CharacterClassId characterClassId, RaceId raceId, String talentLink, String... enabledAssets) {
		this.assetPlayerClassId = characterClassId;
		this.assetPlayerRaceId = raceId;
		this.assetPlayerTalentLink = talentLink;
		this.enabledAssets = List.of(enabledAssets);
	}

	protected void assetOnly() {
		assetOnly = true;
	}
}
