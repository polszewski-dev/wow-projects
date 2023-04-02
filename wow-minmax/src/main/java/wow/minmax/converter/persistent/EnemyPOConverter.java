package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.Enemy;
import wow.minmax.converter.BackConverter;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.EnemyPO;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@Component
@AllArgsConstructor
public class EnemyPOConverter implements Converter<Enemy, EnemyPO>, BackConverter<Enemy, EnemyPO> {
	@Override
	public EnemyPO doConvert(Enemy source) {
		return new EnemyPO(source.getEnemyType(), source.getLevelDifference());
	}

	@Override
	public Enemy doConvertBack(EnemyPO source) {
		return new Enemy(source.getEnemyType(), source.getLevelDifference());
	}
}
