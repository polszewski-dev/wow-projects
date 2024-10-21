import { CharacterClass } from '../../shared/model/character/CharacterClass';
import { Race } from '../../shared/model/character/Race';

export interface ProfileInfo {
	profileId?: string;
	profileName: string;
	characterClass: CharacterClass;
	race: Race;
	lastModified?: Date;
	lastUsedCharacterId?: string;
}
