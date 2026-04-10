import { Component } from '@angular/core';
import { ClrIcon, ClrNavigationModule } from '@clr/angular';

@Component({
  selector: 'app-nav',
  imports: [
    ClrNavigationModule,
    ClrIcon
  ],
  templateUrl: './nav.html',
  styleUrl: './nav.css',
  host: { display: 'contents' }
})
export class Nav {}
