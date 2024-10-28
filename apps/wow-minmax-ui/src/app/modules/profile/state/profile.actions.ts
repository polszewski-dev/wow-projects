import { createAction, props } from "@ngrx/store";
import { ProfileInfo } from "../model/ProfileInfo";

export const loadProfiles = createAction(
	"[Profile] Load Profiles"
);

export const loadProfilesSuccess = createAction(
	"[Profile] Load Profiles Success",
	props<{ profiles: ProfileInfo[] }>()
);

export const loadProfilesFailure = createAction(
	"[Profile] Load Profiles Failure",
	props<{ error: string }>()
);

export const addProfile = createAction(
	"[Profile] Add Profile",
	props<{ profile: ProfileInfo }>()
);

export const selectProfile = createAction(
	"[Profile] Select Profile",
	props<{ selectedProfile: ProfileInfo }>()
)
