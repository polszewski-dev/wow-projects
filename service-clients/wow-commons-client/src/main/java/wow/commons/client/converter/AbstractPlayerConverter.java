package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import wow.character.model.build.RotationTemplate;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.equipment.EquipmentConverter;
import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.client.dto.PlayerDTO;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;
import wow.commons.model.item.AbstractItem;
import wow.commons.model.item.ConsumableId;
import wow.commons.model.talent.Talent;
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
		var talentIds = source.getTalents().getStream()
				.map(Talent::getId)
				.map(TalentId::value)
				.toList();

		var buffIds = source.getBuffs().getStream()
				.map(Buff::getId)
				.map(BuffId::value)
				.toList();

		var consumableIds = source.getConsumables().getStream()
				.map(AbstractItem::getId)
				.map(ConsumableId::value)
				.toList();

		return new PlayerDTO(
				source.getName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				source.getLevel(),
				source.getPhaseId(),
				characterProfessionConverter.convertList(source.getProfessions().getList()),
				source.getExclusiveFactions().getList(),
				equipmentConverter.convert(source.getEquipment()),
				talentIds,
				source.getRole(),
				source.getActivePetType(),
				source.getRotation().getTemplate().toString(),
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

		player.setProfessions(characterProfessionConverter.convertBackList(source.professions(), source.phaseId()));
		player.getExclusiveFactions().set(source.exclusiveFactions());
		player.setEquipment(equipmentConverter.convertBack(source.equipment(), source.phaseId()));

		var target = nonPlayerConverter.convertBack(source.target(), source.phaseId());

		player.setTarget(target);

		characterService.updateAfterRestrictionChange(player);

		enableBuffs(source, player);
		enableTargetBuffs(source.target(), (N) player.getTarget());

		enableConsumables(source, player);

		return player;
	}

	private void changeBuild(PlayerCharacter character, PlayerDTO source) {
		var build = character.getBuild();

		for (var talentId : source.talentIds()) {
			build.getTalents().enable(TalentId.of(talentId));
		}

		build.setRole(source.role());
		build.setActivePet(source.activePet());
		build.setRotation(RotationTemplate.parse(source.rotation()).createRotation());
	}

	private void enableBuffs(PlayerDTO source, P player) {
		for (var buffId : source.buffIds()) {
			player.getBuffs().enable(BuffId.of(buffId), true);
		}
	}

	private void enableTargetBuffs(NonPlayerDTO source, N nonPlayer) {
		for (var buffId : source.buffIds()) {
			nonPlayer.getBuffs().enable(BuffId.of(buffId), true);
		}
	}

	private void enableConsumables(PlayerDTO source, P player) {
		for (var consumableId : source.consumableIds()) {
			var wrappedConsumableId = ConsumableId.of(consumableId);

			player.getConsumables().enable(wrappedConsumableId);
		}
	}

	protected abstract PlayerCharacterFactory<P> getFactory(String name);
}
