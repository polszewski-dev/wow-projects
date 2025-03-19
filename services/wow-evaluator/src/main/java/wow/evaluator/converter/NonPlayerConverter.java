package wow.evaluator.converter;

import org.springframework.stereotype.Component;
import wow.character.service.CharacterService;
import wow.character.service.NonPlayerCharacterFactory;
import wow.commons.client.converter.AbstractNonPlayerConverter;
import wow.commons.client.converter.BuffConverter;
import wow.evaluator.model.NonPlayer;
import wow.evaluator.model.impl.NonPlayerImpl;

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
