import { CharacterClass } from "../../shared/model/character/CharacterClass";
import { Race } from "../../shared/model/character/Race";
import { Profession } from "./Profession";
import { ScriptInfo } from './ScriptInfo';

export interface Character {
	characterId: string;
	characterClass: CharacterClass;
	race: Race;
	professions: (Profession | null)[];
	script: ScriptInfo;
}
