import { Location } from '@angular/common';
import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { catchError, distinctUntilChanged, map, of, switchMap, tap } from "rxjs";
import { selectCharacter } from '../../character/state/character/character.actions';
import { ProfileInfo } from "../model/ProfileInfo";
import { ProfileService } from '../services/profile.service';
import { loadProfiles, loadProfilesFailure, loadProfilesSuccess, selectProfile } from './profile.actions';

@Injectable()
export class ProfileEffects {
	constructor(
		private actions$: Actions,
		private profileService: ProfileService,
		private location: Location
	) {}

	loadProfiles$ = createEffect(() => this.actions$.pipe(
		ofType(loadProfiles),
		switchMap(() => this.profileService.getProfileList().pipe(
			map(profiles => loadProfilesSuccess({ profiles })),
			catchError(error => of(loadProfilesFailure({ error })))
		))
	));

	selectProfile$ = createEffect(() => this.actions$.pipe(
		ofType(selectProfile),
		tap(x => this.changeUrl(x.selectedProfile)),
		map(x => selectCharacter({
			characterId: x.selectedProfile ? x.selectedProfile.lastUsedCharacterId! : null
		}))
	));

	private changeUrl(selectedProfile: ProfileInfo | null) {
		if (selectedProfile) {
			this.location.replaceState('/edit-profile/' + selectedProfile.profileId);
		} else {
			this.location.replaceState('/');
		}
	}
}
