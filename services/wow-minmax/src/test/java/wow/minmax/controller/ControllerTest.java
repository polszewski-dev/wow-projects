package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.item.Item;
import wow.estimator.client.dto.stats.*;
import wow.minmax.WowMinMaxSpringTest;
import wow.minmax.model.CharacterSelectionOptions;
import wow.minmax.model.NewProfileOptions;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerProfileInfo;
import wow.minmax.model.config.ViewConfig;
import wow.minmax.model.equipment.EquipmentSocketStatus;
import wow.minmax.service.*;
import wow.simulator.client.dto.StatsDTO;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
abstract class ControllerTest extends WowMinMaxSpringTest {
	@MockBean
	PlayerProfileService playerProfileService;

	@MockBean
	PlayerCharacterService playerCharacterService;

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
	PlayerCharacter character;
	PlayerProfileInfo profileInfo;

	@BeforeEach
	void setup() {
		profile = getPlayerProfile();
		character = getCharacter();

		equipGearSet(character);

		profileInfo = profile.getProfileInfo();

		when(playerProfileService.getPlayerProfileInfos()).thenReturn(List.of(profileInfo));
		when(playerProfileService.getPlayerProfile(profile.getProfileIdAsUUID())).thenReturn(profile);
		when(playerProfileService.createPlayerProfile(any())).thenReturn(profile);
		when(playerProfileService.getNewProfileOptions()).thenReturn(new NewProfileOptions(List.of()));
		when(playerProfileService.getCharacterSelectionOptions(any())).thenReturn(new CharacterSelectionOptions(List.of(), List.of(), List.of()));

		when(playerCharacterService.getPlayer(CHARACTER_KEY)).thenReturn(character);
		when(playerCharacterService.getViewConfig(any())).thenReturn(new ViewConfig(CharacterRestriction.EMPTY, TimeRestriction.of(PHASE), 1, List.of()));

		when(equipmentService.getEquipment(any())).thenReturn(character.getEquipment());
		when(equipmentService.resetEquipment(any())).thenReturn(character);
		when(equipmentService.equipItem(any(), any(), any())).thenReturn(character);
		when(equipmentService.equipItem(any(), any(), any(), anyBoolean(), any())).thenReturn(character);
		when(equipmentService.equipItemGroup(any(), any(), any())).thenReturn(character);
		when(equipmentService.getEquipmentSocketStatus(any())).thenReturn(new EquipmentSocketStatus(Map.of()));

		when(buffService.changeBuffStatus(any(), any(), any(), anyBoolean())).thenReturn(character);

		when(consumableService.changeConsumableStatus(any(), any(), anyBoolean())).thenReturn(character);

		when(upgradeService.findUpgrades(any(), any(), any(), any())).thenReturn(List.of());
		when(upgradeService.getBestItemVariant(any(), any(), any(), any())).thenAnswer(input -> new EquippableItem(input.getArgument(0, Item.class)));

		when(statsService.getAbilityStats(any())).thenReturn(new GetAbilityStatsResponseDTO(List.of()));
		when(statsService.getCharacterStats(any())).thenReturn(new GetCharacterStatsResponseDTO(List.of()));
		when(statsService.getSpecialAbilityStats(any())).thenReturn(new GetSpecialAbilityStatsResponseDTO(List.of()));
		when(statsService.getRotationStats(any())).thenReturn(new GetRotationStatsResponseDTO(new RotationStatsDTO(List.of(), 0, 0)));
		when(statsService.getTalentStats(any())).thenReturn(new GetTalentStatsResponseDTO(List.of()));

		when(simulatorService.simulate(any())).thenReturn(new StatsDTO(List.of(), 0, 0, 0, 0));
	}
}
