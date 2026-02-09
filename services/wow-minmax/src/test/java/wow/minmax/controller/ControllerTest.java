package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.Item;
import wow.estimator.client.dto.stats.*;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.*;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.equipment.EquipmentSocketStatus;
import wow.minmax.service.*;
import wow.simulator.client.dto.StatsDTO;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
abstract class ControllerTest extends WowMinMaxSpringTest {
	@MockBean
	PlayerProfileService playerProfileService;

	@MockBean
	PlayerService playerService;

	@MockBean
	EquipmentService equipmentService;

	@MockBean
	BuffService buffService;

	@MockBean
	ConsumableService consumableService;

	@MockBean
	UpgradeService upgradeService;

	@MockBean
	StatsService statsService;

	@MockBean
	SimulatorService simulatorService;

	PlayerProfile profile;
	Player player;
	PlayerProfileInfo profileInfo;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		player = getPlayer();

		equipGearSet(player);

		profileInfo = profile.getProfileInfo();

		when(playerProfileService.getPlayerProfileInfos()).thenReturn(List.of(profileInfo));
		when(playerProfileService.getPlayerProfile(profile.getProfileIdAsUUID())).thenReturn(profile);
		when(playerProfileService.createPlayerProfile(any())).thenReturn(profile);
		when(playerProfileService.getNewProfileOptions()).thenReturn(new NewProfileOptions(List.of()));
		when(playerProfileService.getCharacterSelectionOptions(any())).thenReturn(new CharacterSelectionOptions(List.of(), List.of(), List.of()));

		when(playerService.getPlayer(CHARACTER_KEY)).thenReturn(player);
		when(playerService.getViewConfig(any())).thenReturn(new ViewConfig(CharacterRestriction.EMPTY, TimeRestriction.of(PHASE), 1, List.of()));
		when(playerService.getAvailableProfessions(any())).thenReturn(List.of());
		when(playerService.changeProfession(any(), anyInt(), any())).thenReturn(player);
		when(playerService.getAvailableExclusiveFactions(any())).thenReturn(List.of());
		when(playerService.changeExclusiveFaction(any(), any())).thenReturn(player);
		when(playerService.changeTalents(any(), any())).thenReturn(player);
		when(playerService.getAvailableScripts(any())).thenReturn(List.of());
		when(playerService.changeScript(any(), any())).thenReturn(player);

		when(equipmentService.getEquipment(any())).thenReturn(player.getEquipment());
		when(equipmentService.resetEquipment(any())).thenReturn(player);
		when(equipmentService.equipItem(any(), any(), any())).thenReturn(List.of());
		when(equipmentService.equipItem(any(), any(), any(), anyBoolean(), any())).thenReturn(List.of());
		when(equipmentService.equipItemGroup(any(), any(), any())).thenReturn(List.of());
		when(equipmentService.getEquipmentSocketStatus(any())).thenReturn(new EquipmentSocketStatus(Map.of()));

		when(buffService.changeBuffStatus(any(), any(), any(), anyBoolean())).thenReturn(player);

		when(consumableService.changeConsumableStatus(any(), any(), anyBoolean())).thenReturn(player);

		when(upgradeService.findUpgrades(any(), any(), any(), any())).thenReturn(List.of());
		when(upgradeService.getBestItemVariant(any(), any(), any(), any())).thenAnswer(input -> new EquippableItem(input.getArgument(0, Item.class)));

		when(statsService.getAbilityStats(any())).thenReturn(new GetAbilityStatsResponseDTO(List.of()));
		when(statsService.getCharacterStats(any())).thenReturn(new GetCharacterStatsResponseDTO(List.of()));
		when(statsService.getSpecialAbilityStats(any())).thenReturn(new GetSpecialAbilityStatsResponseDTO(List.of()));
		when(statsService.getRotationStats(any())).thenReturn(new GetRotationStatsResponseDTO(new RotationStatsDTO(List.of(), 0, 0)));
		when(statsService.getTalentStats(any())).thenReturn(new GetTalentStatsResponseDTO(List.of()));

		when(simulatorService.simulate(any())).thenReturn(new StatsDTO(List.of(), List.of(), List.of(), 0, 0, 0, 0));
	}
}
