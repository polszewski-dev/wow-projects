package wow.evaluator.converter;

import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.*;
import wow.evaluator.model.NonPlayer;
import wow.evaluator.model.Player;
import wow.evaluator.model.impl.PlayerImpl;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
public class PlayerConverter extends AbstractPlayerConverter<Player, NonPlayer> {
	public PlayerConverter(CharacterService characterService, CharacterProfessionConverter characterProfessionConverter, EquipmentConverter equipmentConverter, TalentConverter talentConverter, ConsumableConverter consumableConverter, BuffConverter buffConverter, AbstractNonPlayerConverter<NonPlayer> nonPlayerConverter) {
		super(characterService, characterProfessionConverter, equipmentConverter, talentConverter, consumableConverter, buffConverter, nonPlayerConverter);
	}

	@Override
	protected PlayerCharacterFactory<Player> getFactory(String name) {
		return PlayerImpl::new;
	}
}
