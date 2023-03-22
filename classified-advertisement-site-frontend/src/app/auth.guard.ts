import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivateFn, Router, UrlTree } from "@angular/router";
import { LoggedInUserService } from "./service/logged-in-user.service";
import { Role } from "./service/types";

export const loginGuard: CanActivateFn = async (route: ActivatedRouteSnapshot): Promise<boolean | UrlTree> => {
  const loggedInUserService = inject(LoggedInUserService);
  const router = inject(Router);
  const loggedIn = await new Promise<boolean>(r => {
    loggedInUserService.isLoggedIn.subscribe(res => r(res));
    setTimeout(() => r(false), 2000);
  });

  if (loggedIn === route.data['login']) {
    return true;
  }

  return router.parseUrl(route.data['redirectTo'] || '/');
};

export const roleGuard: CanActivateFn = async (route: ActivatedRouteSnapshot): Promise<boolean | UrlTree> => {
  const loggedInUserService = inject(LoggedInUserService);
  const router = inject(Router);
  const roles = await new Promise<Role[]>(r => {
    loggedInUserService.user.subscribe(res => r(res.roles as Role[]));
    setTimeout(() => r([]), 2000);
  });

  if (roles.includes(route.data['role'])) {
    return true;
  }

  return router.parseUrl(route.data['redirectTo'] || '/');
};
