import { Component, Input } from '@angular/core';
import { Description } from '../../model/Description';

@Component({
	selector: 'app-description',
	templateUrl: './description.component.html',
	styleUrl: './description.component.css'
})
export class DescriptionComponent {
	@Input({ required: true })
	source!: Description;
}
