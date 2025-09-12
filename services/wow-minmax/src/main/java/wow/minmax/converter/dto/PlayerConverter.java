package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.character.model.character.NonPlayerCharacter;
import wow.character.model.character.PlayerCharacter;
import wow.character.model.character.impl.PlayerCharacterImpl;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.*;
import wow.commons.client.converter.equipment.EquipmentConverter;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Component
public class PlayerConverter extends AbstractPlayerConverter<PlayerCharacter, NonPlayerCharacter> {
	public PlayerConverter(CharacterService characterService, CharacterProfessionConverter characterProfessionConverter, EquipmentConverter equipmentConverter, TalentConverter talentConverter, ConsumableConverter consumableConverter, BuffConverter buffConverter, AbstractNonPlayerConverter<NonPlayerCharacter> nonPlayerConverter) {
		super(characterService, characterProfessionConverter, equipmentConverter, talentConverter, consumableConverter, buffConverter, nonPlayerConverter);
	}

	@Override
	protected PlayerCharacterFactory<PlayerCharacter> getFactory(String name) {
		return PlayerCharacterImpl::new;
	}
}
