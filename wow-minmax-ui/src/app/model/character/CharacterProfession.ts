import { Profession } from '../../modules/shared/model/character/Profession';
import { ProfessionSpecialization } from '../../modules/shared/model/character/ProfessionSpecialization';

export interface CharacterProfession {
	profession?: Profession;
	specialization?: ProfessionSpecialization;
	level: number;
}
