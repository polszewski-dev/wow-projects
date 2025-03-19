package wow.character.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.character.CombatRatingInfo;
import wow.character.repository.CombatRatingInfoRepository;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.repository.pve.GameVersionRepository;
import wow.commons.util.GameVersionMap;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@Component
@RequiredArgsConstructor
public class CombatRatingInfoRepositoryImpl implements CombatRatingInfoRepository {
	private final GameVersionMap<Integer, CombatRatingInfo> combatRatingInfoByLevel = new GameVersionMap<>();

	private final GameVersionRepository gameVersionRepository;

	@Override
	public Optional<CombatRatingInfo> getCombatRatingInfo(GameVersionId gameVersionId, int level) {
		return combatRatingInfoByLevel.getOptional(gameVersionId, level);
	}

	@PostConstruct
	public void init() {
		calcCombatRatingInfos();
	}

	private void calcCombatRatingInfos() {
		for (var gameVersionId : GameVersionId.values()) {
			var gameVersion = gameVersionRepository.getGameVersion(gameVersionId).orElseThrow();
			var maxLevel = gameVersion.getLastPhase().getMaxLevel();

			for (int level = 1; level <= maxLevel + 3; ++level) {
				var combatRatingInfo = newCombatRatingInfo(gameVersion, level);
				addCombatRatingInfo(combatRatingInfo);
			}
		}
	}

	private CombatRatingInfo newCombatRatingInfo(GameVersion gameVersion, int level) {
		return new CombatRatingInfo(
				level,
				getAmountForOnePct(level, SPELL_CRIT_RATING_BASE),
				getAmountForOnePct(level, SPELL_HIT_RATING_BASE),
				getAmountForOnePct(level, SPELL_HASTE_RATING_BASE),
				gameVersion
		);
	}

	private void addCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		combatRatingInfoByLevel.put(
				combatRatingInfo.getGameVersion().getGameVersionId(),
				combatRatingInfo.getLevel(),
				combatRatingInfo
		);
	}

	private static final int SPELL_HIT_RATING_BASE = 8;
	private static final int SPELL_CRIT_RATING_BASE = 14;
	private static final int SPELL_HASTE_RATING_BASE = 10;

	private double getAmountForOnePct(int level, int ratingBase) {
		return ratingBase * calcH(level);
	}

	/*

	-- Level 1 to 10:  H = 2/52
	-- Level 10 to 60: H = (level-8)/52
	-- Level 60 to 70: H = 82/(262-3*level)
	-- Level 70 to 80: H = (82/52)*(131/63)^((level-70)/10)

	 */

	private double calcH(int level) {
		if (1 <= level && level <= 10) {
			return 2 / 52.0;
		} else if (10 < level && level <= 60) {
			return (level - 8) / 52.0;
		} else if (60 < level && level <= 70) {
			return 82.0 / (262 - 3 * level);
		} else if (70 < level && level <= 80) {
			return (82.0 / 52) * Math.pow(131.0 / 63, (level - 70) / 10.0);
		} else {
			return switch (level) {
				case 81 -> 4.3056015014648;
  				case 82 -> 5.6539749145508;
				case 83 -> 7.4275451660156;
				case 84 -> 9.7527236938477;
				case 85 -> 12.8057159423828;
				default -> throw new IllegalArgumentException();
			};
		}
	}
}
