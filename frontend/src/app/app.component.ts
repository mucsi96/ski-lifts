import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <header class="header">
      <div class="container">
        <a routerLink="/" class="header-link">
          <h1>Swiss Ski Resorts</h1>
        </a>
        <p>Discover ski areas with lift information and elevation profiles</p>
      </div>
    </header>
    <main class="container">
      <router-outlet />
    </main>
  `,
  styles: [`
    .header-link {
      text-decoration: none;
      color: inherit;
    }
  `]
})
export class AppComponent {}
