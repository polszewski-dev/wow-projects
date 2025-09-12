package wow.estimator.converter;

import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.AbstractNonPlayerConverter;
import wow.commons.client.converter.AbstractPlayerConverter;
import wow.commons.client.converter.CharacterProfessionConverter;
import wow.commons.client.converter.equipment.EquipmentConverter;
import wow.estimator.model.NonPlayer;
import wow.estimator.model.Player;
import wow.estimator.model.impl.PlayerImpl;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
public class PlayerConverter extends AbstractPlayerConverter<Player, NonPlayer> {
	public PlayerConverter(CharacterService characterService, CharacterProfessionConverter characterProfessionConverter, EquipmentConverter equipmentConverter, AbstractNonPlayerConverter<NonPlayer> nonPlayerConverter) {
		super(characterService, characterProfessionConverter, equipmentConverter, nonPlayerConverter);
	}

	@Override
	protected PlayerCharacterFactory<Player> getFactory(String name) {
		return PlayerImpl::new;
	}
}
