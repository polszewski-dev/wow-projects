package wow.estimator.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import wow.character.model.character.Raid;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.estimator.WowEstimatorSpringTest;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Player;
import wow.test.commons.AssetNames;
import wow.test.commons.TalentNames;

import static wow.commons.model.character.CharacterClassId.*;
import static wow.commons.model.character.RaceId.*;

/**
 * User: POlszewski
 * Date: 2022-11-20
 */
abstract class ServiceTest extends WowEstimatorSpringTest {
	Player player;

	@Autowired
	PlayerService playerService;

	@BeforeEach
	void setup() {
		player = getPlayer();

		var level = player.getLevel();
		var phaseId = player.getPhaseId();

		var mage = getNakedPlayer("Mage", MAGE, UNDEAD, level, phaseId);
		var warlock = getNakedPlayer("Warlock", WARLOCK, UNDEAD, level, phaseId);
		var priest = getNakedPlayer("Priest", PRIEST, UNDEAD, level, phaseId);
		var druid = getNakedPlayer("Druid", DRUID, TAUREN, level, phaseId);
		var shaman = getNakedPlayer("Shaman", SHAMAN, TAUREN, level, phaseId);
		var paladin = getNakedPlayer("Paladin", PALADIN, BLOOD_ELF, level, phaseId);

		var raid = new Raid<Player>();

		raid.add(player, priest, druid, shaman, paladin, mage, warlock);
		raid.forEach(characterService::applyDefaultCharacterTemplate);

		priest.getTalents().reset();
		priest.getTalents().enable(TalentNames.DIVINE_SPIRIT, 1);

		druid.getTalents().reset();

		raid.forEach(characterService::updateAfterRestrictionChange);

		priest.getAssets().enable(AssetNames.PRAYER_OF_SPIRIT);
		warlock.getAssets().enable(AssetNames.CURSE_OF_THE_ELEMENTS);

		player = playerService.getPlayer(raid, (NonPlayer) player.getTarget());
	}

	Ability getAbility(AbilityId abilityId) {
		return player.getAbility(abilityId).orElseThrow();
	}
}
