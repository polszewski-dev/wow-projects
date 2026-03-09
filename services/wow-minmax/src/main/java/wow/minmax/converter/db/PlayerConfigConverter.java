package wow.minmax.converter.db;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.talent.TalentId;
import wow.minmax.converter.db.equipment.EquipmentConfigConverter;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerId;
import wow.minmax.model.db.PlayerConfig;
import wow.minmax.model.impl.PlayerImpl;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class PlayerConfigConverter implements ParametrizedConverter<Player, PlayerConfig, PlayerId>, BackConverter<Player, PlayerConfig> {
	private final BuildConfigConverter buildConfigConverter;
	private final EquipmentConfigConverter equipmentConfigConverter;
	private final CharacterProfessionConfigConverter characterProfessionConfigConverter;
	private final NonPlayerConfigConverter nonPlayerConfigConverter;

	private final CharacterService characterService;

	@Override
	public PlayerConfig doConvert(Player source, PlayerId playerId) {
		var buffIds = source.getBuffs().getIds(BuffId::value);
		var consumableIds = source.getConsumables().getIds(ConsumableId::value);

		return new PlayerConfig(
				playerId.toString(),
				source.getName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				source.getLevel(),
				source.getPhaseId(),
				buildConfigConverter.convert(source.getBuild()),
				equipmentConfigConverter.convert(source.getEquipment()),
				characterProfessionConfigConverter.convertList(source.getProfessions().getList()),
				source.getExclusiveFactions().getNameList(),
				buffIds,
				consumableIds,
				nonPlayerConfigConverter.convert((NonPlayer) source.getTarget())
		);
	}

	@Override
	public Player doConvertBack(PlayerConfig source) {
		var player = characterService.createPlayerCharacter(
				source.getName(),
				source.getCharacterClassId(),
				source.getRace(),
				source.getLevel(),
				source.getPhaseId(),
				PlayerImpl::new
		);

		changeBuild(player, source);

		player.setProfessions(characterProfessionConfigConverter.convertBackList(source.getProfessions()));
		player.getExclusiveFactions().set(source.getExclusiveFactions());
		player.setEquipment(equipmentConfigConverter.convertBack(source.getEquipment(), player.getPhaseId()));

		var target = nonPlayerConfigConverter.convertBack(source.getTarget());
		player.setTarget(target);

		characterService.updateAfterRestrictionChange(player);

		player.getBuffs().setIds(source.getBuffIds(), BuffId::of);
		player.getTarget().getBuffs().setIds(source.getTarget().getDebuffIds(), BuffId::of);
		player.getConsumables().setIds(source.getConsumableIds(), ConsumableId::of);

		return player;
	}

	private void changeBuild(Player player, PlayerConfig source) {
		var build = player.getBuild();
		var sourceBuild = source.getBuild();

		build.getTalents().setIds(sourceBuild.getTalentIds(), TalentId::of);
		build.setRole(sourceBuild.getRole());
		build.setActivePet(sourceBuild.getActivePet());
		build.setScript(sourceBuild.getScript());
	}
}
