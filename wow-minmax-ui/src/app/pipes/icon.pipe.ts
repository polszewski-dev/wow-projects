import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Pipe({
	name: 'icon'
})
export class IconPipe implements PipeTransform {
	constructor(private sanitizer: DomSanitizer) {}

	transform(value: string, ...args: unknown[]): string {
		return `https://wow.zamimg.com/images/wow/icons/small/${value}.jpg`;
	}
}
