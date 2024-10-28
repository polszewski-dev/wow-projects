import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainComponent } from './components/main/main.component';
import { ProfileModule } from './modules/profile/profile.module';

@NgModule({
	declarations: [
		AppComponent,
		MainComponent,
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		ProfileModule
	],
	providers: [provideHttpClient(withInterceptorsFromDi())],
	bootstrap: [AppComponent]
})
export class AppModule { }
