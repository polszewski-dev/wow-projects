package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.Build;
import wow.commons.client.converter.Converter;
import wow.minmax.model.BuildConfig;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class BuildConfigConverter implements Converter<Build, BuildConfig> {
	private final TalentConfigConverter talentConfigConverter;

	@Override
	public BuildConfig doConvert(Build source) {
		return new BuildConfig(
				talentConfigConverter.convertList(source.getTalents().getList()),
				source.getRole(),
				source.getRotation().getTemplate().toString(),
				source.getActivePetType()
		);
	}
}
