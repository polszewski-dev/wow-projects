package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.RotationTemplate;
import wow.character.model.character.PlayerCharacter;
import wow.character.service.CharacterService;
import wow.commons.client.converter.CharacterProfessionConverter;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.EquipmentConverter;
import wow.simulator.client.dto.CharacterDTO;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
@AllArgsConstructor
public class PlayerCharacterConverter implements Converter<CharacterDTO, PlayerCharacter> {
	private final CharacterService characterService;

	private final CharacterProfessionConverter characterProfessionConverter;
	private final EquipmentConverter equipmentConverter;

	@Override
	public PlayerCharacter doConvert(CharacterDTO source) {
		var character = characterService.createPlayerCharacter(
				source.characterClassId(),
				source.raceId(),
				source.level(),
				source.phaseId()
		);

		changeBuild(character, source);

		character.setProfessions(characterProfessionConverter.convertBackList(source.professions(), source.phaseId()));
		character.getExclusiveFactions().set(source.exclusiveFactions());
		character.setEquipment(equipmentConverter.convertBack(source.equipment(), source.phaseId()));

		return character;
	}

	private void changeBuild(PlayerCharacter character, CharacterDTO source) {
		var build = character.getBuild();

		for (var sourceTalent : source.talents()) {
			build.getTalents().enableTalent(sourceTalent.talentId(), sourceTalent.rank());
		}

		build.setRole(source.role());
		build.setActivePet(source.activePet());
		build.setRotation(RotationTemplate.parse(source.rotation()).createRotation());
	}
}
