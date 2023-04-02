import { EnemyType } from "./EnemyType";
import { LevelDifference } from "./LevelDifference";
import { Phase } from "./Phase";

export interface CharacterSelectionOptions {
	phases: Phase[];
	enemyTypes: EnemyType[];
	enemyLevelDiffs: LevelDifference[];
	lastModifiedCharacterId: string;
}
