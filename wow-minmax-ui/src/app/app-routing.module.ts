import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileEditorComponent } from './components/profile-editor/profile-editor.component';

const routes: Routes = [
	{ path: '', component: ProfileEditorComponent }
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }
