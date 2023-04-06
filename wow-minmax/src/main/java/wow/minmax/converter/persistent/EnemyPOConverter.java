package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Enemy;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.EnemyPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class EnemyPOConverter implements Converter<Enemy, EnemyPO>, ParametrizedBackConverter<Enemy, EnemyPO> {
	private final BuffPOConverter buffPOConverter;

	@Override
	public EnemyPO doConvert(Enemy source) {
		return new EnemyPO(
				source.getEnemyType(),
				source.getLevelDifference(),
				buffPOConverter.convertList(source.getDebuffs().getList())
		);
	}

	@Override
	public Enemy doConvertBack(EnemyPO source, Map<String, Object> params) {
		Enemy enemy = new Enemy(source.getEnemyType(), source.getLevelDifference());

		enemy.setDebuffs(buffPOConverter.convertBackList(source.getDebuffs(), params));
		return enemy;
	}
}
