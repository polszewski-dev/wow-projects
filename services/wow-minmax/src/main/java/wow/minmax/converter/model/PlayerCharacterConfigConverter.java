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
import wow.minmax.model.PlayerCharacterConfig;
import wow.minmax.model.impl.PlayerImpl;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class PlayerCharacterConfigConverter implements ParametrizedConverter<Player, PlayerCharacterConfig, CharacterId>, BackConverter<Player, PlayerCharacterConfig> {
	private final BuildConfigConverter buildConfigConverter;
	private final EquipmentConfigConverter equipmentConfigConverter;
	private final CharacterProfessionConfigConverter characterProfessionConfigConverter;
	private final NonPlayerCharacterConfigConverter nonPlayerCharacterConfigConverter;

	private final CharacterService characterService;

	@Override
	public PlayerCharacterConfig doConvert(Player source, CharacterId characterId) {
		var buffIds = source.getBuffs().getStream()
				.map(Buff::getId)
				.map(BuffId::value)
				.toList();

		var consumableIds = source.getConsumables().getStream()
				.map(Consumable::getId)
				.map(ConsumableId::value)
				.toList();

		return new PlayerCharacterConfig(
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
				nonPlayerCharacterConfigConverter.convert((NonPlayer) source.getTarget())
		);
	}

	@Override
	public Player doConvertBack(PlayerCharacterConfig source) {
		var character = characterService.createPlayerCharacter(
				source.getName(),
				source.getCharacterClassId(),
				source.getRace(),
				source.getLevel(),
				source.getPhaseId(),
				PlayerImpl::new
		);

		changeBuild(character, source);

		character.setProfessions(characterProfessionConfigConverter.convertBackList(source.getProfessions()));
		character.getExclusiveFactions().set(source.getExclusiveFactions());
		character.setEquipment(equipmentConfigConverter.convertBack(source.getEquipment(), character.getPhaseId()));

		var target = nonPlayerCharacterConfigConverter.convertBack(source.getTarget());
		character.setTarget(target);

		characterService.updateAfterRestrictionChange(character);

		character.getBuffs().setBuffIds(getBuffIds(source.getBuffIds()));
		character.getTarget().getBuffs().setBuffIds(getBuffIds(source.getTarget().getDebuffIds()));

		character.getConsumables().setConsumableIds(getConsumableIds(source));

		return character;
	}

	private void changeBuild(Player player, PlayerCharacterConfig source) {
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

	private List<ConsumableId> getConsumableIds(PlayerCharacterConfig source) {
		return source.getConsumableIds().stream()
				.map(ConsumableId::of)
				.toList();
	}
}
