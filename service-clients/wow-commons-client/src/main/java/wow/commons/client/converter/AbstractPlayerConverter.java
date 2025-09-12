package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import wow.character.model.build.RotationTemplate;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.equipment.EquipmentConverter;
import wow.commons.client.dto.ConsumableDTO;
import wow.commons.client.dto.NonPlayerDTO;
import wow.commons.client.dto.PlayerDTO;

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
	private final TalentConverter talentConverter;
	private final ConsumableConverter consumableConverter;
	private final BuffConverter buffConverter;
	private final AbstractNonPlayerConverter<N> nonPlayerConverter;

	@Override
	public PlayerDTO doConvert(P source) {
		return new PlayerDTO(
				source.getName(),
				source.getCharacterClassId(),
				source.getRaceId(),
				source.getLevel(),
				source.getPhaseId(),
				characterProfessionConverter.convertList(source.getProfessions().getList()),
				source.getExclusiveFactions().getList(),
				equipmentConverter.convert(source.getEquipment()),
				talentConverter.convertList(source.getTalents().getList()),
				source.getRole(),
				source.getActivePetType(),
				source.getRotation().getTemplate().toString(),
				buffConverter.convertList(source.getBuffs().getList()),
				List.of(),
				consumableConverter.convertList(source.getConsumables().getList()),
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

		player.getConsumables().setConsumables(getConsumableNames(source));

		return player;
	}

	private void changeBuild(PlayerCharacter character, PlayerDTO source) {
		var build = character.getBuild();

		for (var sourceTalent : source.talents()) {
			build.getTalents().enableTalent(sourceTalent.talentId(), sourceTalent.rank());
		}

		build.setRole(source.role());
		build.setActivePet(source.activePet());
		build.setRotation(RotationTemplate.parse(source.rotation()).createRotation());
	}

	private void enableBuffs(PlayerDTO source, P player) {
		for (var buff : source.buffs()) {
			player.getBuffs().enable(buff.buffId(), buff.rank(), true);
		}
	}

	private void enableTargetBuffs(NonPlayerDTO source, N nonPlayer) {
		for (var buff : source.buffs()) {
			nonPlayer.getBuffs().enable(buff.buffId(), buff.rank(), true);
		}
	}

	private List<String> getConsumableNames(PlayerDTO source) {
		return source.consumables().stream()
				.map(ConsumableDTO::name)
				.toList();
	}

	protected abstract PlayerCharacterFactory<P> getFactory(String name);
}
