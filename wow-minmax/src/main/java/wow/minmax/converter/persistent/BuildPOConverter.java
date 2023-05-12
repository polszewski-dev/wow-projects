package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.build.Build;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.BuildPO;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class BuildPOConverter implements Converter<Build, BuildPO> {
	@Override
	public BuildPO doConvert(Build source) {
		return new BuildPO(
				source.getCharacterTemplateId()
		);
	}
}
