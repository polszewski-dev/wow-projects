package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.RotationTemplate;
import wow.character.model.character.PlayerCharacter;
import wow.character.service.CharacterService;
import wow.commons.client.converter.CharacterProfessionConverter;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.EquipmentConverter;
import wow.commons.client.dto.ConsumableDTO;
import wow.commons.client.dto.PlayerDTO;
import wow.simulator.model.unit.Player;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
@AllArgsConstructor
public class PlayerConverter implements Converter<PlayerDTO, Player> {
	private final CharacterService characterService;

	private final CharacterProfessionConverter characterProfessionConverter;
	private final EquipmentConverter equipmentConverter;

	private final NonPlayerConverter nonPlayerConverter;

	@Override
	public Player doConvert(PlayerDTO source) {
		var player = characterService.createPlayerCharacter(
				source.characterClassId(),
				source.raceId(),
				source.level(),
				source.phaseId(),
				Player.getFactory(source.name())
		);

		changeBuild(player, source);

		player.setProfessions(characterProfessionConverter.convertBackList(source.professions(), source.phaseId()));
		player.getExclusiveFactions().set(source.exclusiveFactions());
		player.setEquipment(equipmentConverter.convertBack(source.equipment(), source.phaseId()));

		var target = nonPlayerConverter.convert(source.target(), source.phaseId());

		player.setTarget(target);

		characterService.updateAfterRestrictionChange(player);

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

	private List<String> getConsumableNames(PlayerDTO source) {
		return source.consumables().stream()
				.map(ConsumableDTO::name)
				.toList();
	}
}
