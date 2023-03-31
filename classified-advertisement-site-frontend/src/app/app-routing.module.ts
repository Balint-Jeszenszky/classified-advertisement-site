import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { loginGuard, roleGuard } from './auth.guard';
import { Role } from './service/types';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./advertisement/advertisement.module').then(m => m.AdvertisementModule),
  },
  {
    path: 'auth',
    loadChildren: () => import('./authentication/authentication.module').then(m => m.AuthenticationModule),
    data: { login: false },
    canActivate: [loginGuard],
  },
  {
    path: 'profile',
    loadChildren: () => import('./profile/profile.module').then(m => m.ProfileModule),
    data: { login: true },
    canActivate: [loginGuard],
  },
  {
    path: 'admin',
    loadChildren: () => import('./admin/admin.module').then(m => m.AdminModule),
    data: { login: true, role: Role.ROLE_ADMIN },
    canActivate: [loginGuard, roleGuard],
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
