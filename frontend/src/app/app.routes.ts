import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./components/resort-list/resort-list.component').then(
        (m) => m.ResortListComponent
      )
  },
  {
    path: 'resort/:id',
    loadComponent: () =>
      import('./components/resort-detail/resort-detail.component').then(
        (m) => m.ResortDetailComponent
      )
  },
  {
    path: '**',
    redirectTo: ''
  }
];
