package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.RotationTemplate;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.CharacterService;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.Consumable;
import wow.commons.model.item.ConsumableId;
import wow.minmax.converter.model.equipment.EquipmentConfigConverter;
import wow.minmax.model.CharacterId;
import wow.minmax.model.PlayerCharacterConfig;
import wow.minmax.model.TalentConfig;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class PlayerCharacterConfigConverter implements ParametrizedConverter<PlayerCharacter, PlayerCharacterConfig, CharacterId>, BackConverter<PlayerCharacter, PlayerCharacterConfig> {
	private final BuildConfigConverter buildConfigConverter;
	private final EquipmentConfigConverter equipmentConfigConverter;
	private final CharacterProfessionConfigConverter characterProfessionConfigConverter;
	private final NonPlayerCharacterConfigConverter nonPlayerCharacterConfigConverter;

	private final CharacterService characterService;

	@Override
	public PlayerCharacterConfig doConvert(PlayerCharacter source, CharacterId characterId) {
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
				source.getExclusiveFactions().getList(),
				buffIds,
				consumableIds,
				nonPlayerCharacterConfigConverter.convert((NonPlayerCharacter) source.getTarget())
		);
	}

	@Override
	public PlayerCharacter doConvertBack(PlayerCharacterConfig source) {
		var character = characterService.createPlayerCharacter(
				source.getName(),
				source.getCharacterClassId(),
				source.getRace(),
				source.getLevel(),
				source.getPhaseId(),
				PlayerCharacterImpl::new
		);

		changeBuild(character, source);

		character.setProfessions(characterProfessionConfigConverter.convertBackList(source.getProfessions(), character.getPhaseId()));
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

	private void changeBuild(PlayerCharacter player, PlayerCharacterConfig source) {
		var build = player.getBuild();
		var sourceBuild = source.getBuild();

		for (TalentConfig sourceTalent : sourceBuild.getTalents()) {
			build.getTalents().enableTalent(sourceTalent.getTalentId(), sourceTalent.getRank());
		}

		build.setRole(sourceBuild.getRole());
		build.setActivePet(sourceBuild.getActivePet());
		build.setRotation(RotationTemplate.parse(sourceBuild.getRotation()).createRotation());
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
