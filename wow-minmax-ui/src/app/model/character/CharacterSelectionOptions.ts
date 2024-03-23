import { EnemyType } from "../../modules/shared/model/character/EnemyType";
import { LevelDifference } from "../../modules/shared/model/character/LevelDifference";
import { Phase } from "../../modules/shared/model/character/Phase";

export interface CharacterSelectionOptions {
	phases: Phase[];
	enemyTypes: EnemyType[];
	enemyLevelDiffs: LevelDifference[];
	lastModifiedCharacterId: string;
}
