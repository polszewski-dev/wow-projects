package wow.simulator.converter;

import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.character.service.PlayerCharacterFactory;
import wow.commons.client.converter.*;
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.Player;
import wow.simulator.model.unit.impl.PlayerImpl;

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
