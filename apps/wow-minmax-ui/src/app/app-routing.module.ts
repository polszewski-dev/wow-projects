import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from './components/main/main.component';
import { NewProfileComponent } from './modules/profile/components/new-profile/new-profile.component';

const routes: Routes = [
	{ path: '', component: MainComponent },
	{ path: 'new-profile', component: NewProfileComponent },
	{ path: 'edit-profile/:id', component: MainComponent },
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }
