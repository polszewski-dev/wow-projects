package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.ProfIdSpecId;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.minmax.client.dto.ProfessionDTO;

/**
 * User: POlszewski
 * Date: 2025-09-26
 */
@Component
@AllArgsConstructor
public class ProfessionConverter implements Converter<CharacterProfession, ProfessionDTO>, BackConverter<ProfIdSpecId, ProfessionDTO> {
	@Override
	public ProfessionDTO doConvert(CharacterProfession source) {
		var profession = source.profession();
		var specialization = source.specialization();

		if (specialization != null) {
			return new ProfessionDTO(
					profession.getProfessionId().name(),
					specialization.getName(),
					specialization.getIcon(),
					specialization.getTooltip(),
					specialization.getSpecializationId().name()
			);
		} else {
			return new ProfessionDTO(
					profession.getProfessionId().name(),
					profession.getName(),
					profession.getIcon(),
					profession.getTooltip(),
					null
			);
		}
	}

	@Override
	public ProfIdSpecId doConvertBack(ProfessionDTO source) {
		var professionId = ProfessionId.valueOf(source.id());
		var specializationId = source.specializationId() != null
				? ProfessionSpecializationId.valueOf(source.specializationId())
				: null;

		return new ProfIdSpecId(professionId, specializationId);
	}
}
