import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivateFn, Router, UrlTree } from "@angular/router";
import { firstValueFrom, map, timeout } from "rxjs";
import { LoggedInUserService } from "./service/logged-in-user.service";
import { Role } from "./service/types";

const TIMEOUT = 3000;

export const loginGuard: CanActivateFn = async (route: ActivatedRouteSnapshot): Promise<boolean | UrlTree> => {
  const loggedInUserService = inject(LoggedInUserService);
  const router = inject(Router);
  const loggedIn = await firstValueFrom(loggedInUserService.isLoggedIn.pipe(timeout(TIMEOUT))).catch(() => false);

  if (loggedIn === route.data['login']) {
    return true;
  }

  return router.parseUrl(route.data['redirectTo'] || '/');
};

export const roleGuard: CanActivateFn = async (route: ActivatedRouteSnapshot): Promise<boolean | UrlTree> => {
  const loggedInUserService = inject(LoggedInUserService);
  const router = inject(Router);
  const roles = await firstValueFrom(loggedInUserService.user.pipe(timeout(TIMEOUT), map(u => u?.roles))).catch(() => [] as Role[]);

  if (roles?.includes(route.data['role'])) {
    return true;
  }

  return router.parseUrl(route.data['redirectTo'] || '/');
};
