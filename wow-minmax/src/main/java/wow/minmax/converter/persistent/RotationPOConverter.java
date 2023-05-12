package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.Rotation;
import wow.commons.model.spells.Spell;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.RotationPO;

import java.util.Map;
import java.util.Optional;

import static wow.minmax.converter.persistent.PoConverterParams.getCharacter;

/**
 * User: POlszewski
 * Date: 2023-05-10
 */
@Component
@AllArgsConstructor
public class RotationPOConverter implements Converter<Rotation, RotationPO>, ParametrizedBackConverter<Rotation, RotationPO> {
	@Override
	public RotationPO doConvert(Rotation source) {
		return new RotationPO(
				source.getCooldowns().stream()
						.map(Spell::getSpellId)
						.toList(),
				source.getFiller().getSpellId()
		);
	}

	@Override
	public Rotation doConvertBack(RotationPO source, Map<String, Object> params) {
		return new Rotation(
				source.getCooldowns().stream()
						.map(x -> getCharacter(params).getSpellbook().getSpell(x))
						.map(Optional::orElseThrow)
						.toList(),
				getCharacter(params).getSpellbook().getSpell(source.getFiller()).orElseThrow()
		);
	}
}
