package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.AbstractNonPlayerConverter;
import wow.commons.client.converter.AbstractPlayerConverter;
import wow.commons.client.converter.BuffConverter;
import wow.commons.client.converter.CharacterProfessionConverter;
import wow.commons.client.converter.equipment.EquipmentConverter;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Component
public class PlayerConverter extends AbstractPlayerConverter<PlayerCharacter, NonPlayerCharacter> {
	public PlayerConverter(CharacterService characterService, CharacterProfessionConverter characterProfessionConverter, EquipmentConverter equipmentConverter, BuffConverter buffConverter, AbstractNonPlayerConverter<NonPlayerCharacter> nonPlayerConverter) {
		super(characterService, characterProfessionConverter, equipmentConverter, buffConverter, nonPlayerConverter);
	}

	@Override
	protected PlayerCharacterFactory<PlayerCharacter> getFactory(String name) {
		return PlayerCharacterImpl::new;
	}
}
