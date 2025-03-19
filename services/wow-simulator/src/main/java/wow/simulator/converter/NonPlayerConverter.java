package wow.simulator.converter;

import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.commons.client.converter.AbstractNonPlayerConverter;
import wow.commons.client.converter.BuffConverter;
import wow.simulator.model.unit.NonPlayer;
import wow.simulator.model.unit.impl.NonPlayerImpl;

/**
 * User: POlszewski
 * Date: 2024-11-10
 */
@Component
public class NonPlayerConverter extends AbstractNonPlayerConverter<NonPlayer> {
	public NonPlayerConverter(CharacterService characterService, BuffConverter buffConverter) {
		super(characterService, buffConverter);
	}

	@Override
	protected NonPlayerCharacterFactory<NonPlayer> getFactory(String name) {
		return NonPlayerImpl::new;
	}
}
