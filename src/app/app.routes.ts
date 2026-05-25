import { Routes } from '@angular/router';
import { AuthGuard } from './auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./login/login.page').then((m) => m.LoginPage)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./register/register.page').then((m) => m.RegisterPage)
  },
  {
    path: 'tabs',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./tabs/tabs.page').then((m) => m.TabsPage),
    children: [
      {
        path: 'tab1',
        loadComponent: () =>
          import('./tab1/tab1.page').then((m) => m.Tab1Page)
      },
      {
        path: 'tab2',
        loadComponent: () =>
          import('./tab2/tab2.page').then((m) => m.Tab2Page)
      },
      {
        path: 'tab3',
        loadComponent: () =>
          import('./tab3/tab3.page').then((m) => m.Tab3Page)
      },
       {
    path: 'habit-detail/:habitId',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./habit-detail/habit-detail.page').then((m) => m.HabitDetailPage)
  },
  {
    path: 'create-habit',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./create-habit/create-habit.page').then((m) => m.CreateHabitPage)
  },
  {
    path: 'edit-habit/:habitId',
    canActivate: [AuthGuard],
    loadComponent: () =>
      import('./edit-habit/edit-habit.page').then((m) => m.EditHabitPage)
  },

  {
    path: '',
    redirectTo: '/tabs/tab1',
    pathMatch: 'full'
  }

    ]
  },

  {
    path: '**',
    redirectTo: 'login',
  },
];