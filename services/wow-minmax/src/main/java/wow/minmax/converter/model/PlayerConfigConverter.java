package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.talent.TalentId;
import wow.minmax.converter.model.equipment.EquipmentConfigConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.NonPlayer;
import wow.minmax.model.Player;
import wow.minmax.model.PlayerConfig;
import wow.minmax.model.impl.PlayerImpl;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class PlayerConfigConverter implements ParametrizedConverter<Player, PlayerConfig, CharacterId>, BackConverter<Player, PlayerConfig> {
	private final BuildConfigConverter buildConfigConverter;
	private final EquipmentConfigConverter equipmentConfigConverter;
	private final CharacterProfessionConfigConverter characterProfessionConfigConverter;
	private final NonPlayerConfigConverter nonPlayerConfigConverter;

	private final CharacterService characterService;

	@Override
	public PlayerConfig doConvert(Player source, CharacterId characterId) {
		var buffIds = source.getBuffs().getStream()
				.map(Buff::getId)
				.map(BuffId::value)
				.toList();

		var consumableIds = source.getConsumables().getStream()
				.map(Consumable::getId)
				.map(ConsumableId::value)
				.toList();

		return new PlayerConfig(
				characterId.toString(),
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

		player.getBuffs().setBuffIds(getBuffIds(source.getBuffIds()));
		player.getTarget().getBuffs().setBuffIds(getBuffIds(source.getTarget().getDebuffIds()));

		player.getConsumables().setConsumableIds(getConsumableIds(source));

		return player;
	}

	private void changeBuild(Player player, PlayerConfig source) {
		var build = player.getBuild();
		var sourceBuild = source.getBuild();

		for (var talentId : sourceBuild.getTalentIds()) {
			build.getTalents().enable(TalentId.of(talentId));
		}

		build.setRole(sourceBuild.getRole());
		build.setActivePet(sourceBuild.getActivePet());
		build.setScript(sourceBuild.getScript());
	}

	private List<BuffId> getBuffIds(List<Integer> buffs) {
		return buffs.stream()
				.map(BuffId::of)
				.toList();
	}

	private List<ConsumableId> getConsumableIds(PlayerConfig source) {
		return source.getConsumableIds().stream()
				.map(ConsumableId::of)
				.toList();
	}
}
