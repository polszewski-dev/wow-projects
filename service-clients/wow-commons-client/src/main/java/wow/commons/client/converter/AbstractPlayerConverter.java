package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.equipment.EquipmentConverter;
import wow.commons.client.dto.PlayerDTO;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.talent.TalentId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@AllArgsConstructor
public abstract class AbstractPlayerConverter<P extends PlayerCharacter, N extends NonPlayerCharacter> implements Converter<P, PlayerDTO>, BackConverter<P, PlayerDTO> {
	private final CharacterService characterService;

	private final CharacterProfessionConverter characterProfessionConverter;
	private final EquipmentConverter equipmentConverter;
	private final AbstractNonPlayerConverter<N> nonPlayerConverter;

	@Override
	public PlayerDTO doConvert(P source) {
		var talentIds = source.getTalents().getIds(TalentId::value);
		var buffIds = source.getBuffs().getIds(BuffId::value);
		var consumableIds = source.getConsumables().getIds(ConsumableId::value);

		return new PlayerDTO(
				source.getName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				source.getLevel(),
				source.getPhaseId(),
				characterProfessionConverter.convertList(source.getProfessions().getList()),
				source.getExclusiveFactions().getNameList(),
				equipmentConverter.convert(source.getEquipment()),
				talentIds,
				source.getRole(),
				source.getActivePetType(),
				source.getBuild().getScript(),
				buffIds,
				List.of(),
				consumableIds,
				nonPlayerConverter.convert((N) source.getTarget())
		);
	}

	@Override
	public P doConvertBack(PlayerDTO source) {
		var player = characterService.createPlayerCharacter(
				source.name(),
				source.characterClassId(),
				source.raceId(),
				source.level(),
				source.phaseId(),
				getFactory(source.name())
		);

		changeBuild(player, source);

		player.setProfessions(characterProfessionConverter.convertBackList(source.professions()));
		player.getExclusiveFactions().set(source.exclusiveFactions());
		player.setEquipment(equipmentConverter.convertBack(source.equipment(), source.phaseId()));

		var target = nonPlayerConverter.convertBack(source.target(), source.phaseId());

		player.setTarget(target);

		characterService.updateAfterRestrictionChange(player);

		player.getBuffs().setIds(source.buffIds(), BuffId::of);
		player.getTarget().getBuffs().setIds(source.target().buffIds(), BuffId::of);
		player.getConsumables().setIds(source.consumableIds(), ConsumableId::of);

		return player;
	}

	private void changeBuild(PlayerCharacter character, PlayerDTO source) {
		var build = character.getBuild();

		build.getTalents().setIds(source.talentIds(), TalentId::of);
		build.setRole(source.role());
		build.setActivePet(source.activePet());
		build.setScript(source.script());
	}

	protected abstract PlayerCharacterFactory<P> getFactory(String name);
}
