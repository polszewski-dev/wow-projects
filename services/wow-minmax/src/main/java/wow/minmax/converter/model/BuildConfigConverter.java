package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.Build;
import wow.commons.client.converter.Converter;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.minmax.model.BuildConfig;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class BuildConfigConverter implements Converter<Build, BuildConfig> {
	@Override
	public BuildConfig doConvert(Build source) {
		var talentIds = source.getTalents().getStream()
				.map(Talent::getId)
				.map(TalentId::value)
				.toList();

		return new BuildConfig(
				talentIds,
				source.getRole(),
				source.getRotation().getTemplate().toString(),
				source.getActivePetType()
		);
	}
}
