import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, tap } from "rxjs";
import { LoggedInUserService } from "../service/logged-in-user.service";

@Injectable()
export class ApiInterceptor implements HttpInterceptor {

  constructor(private readonly loggedInUserService: LoggedInUserService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!req.url.startsWith('/api/')) {
      return next.handle(req);
    }
    
    const token = this.loggedInUserService.accessToken;
    if (token) {
      return next.handle(req.clone({
        setHeaders: {
          authorization: `bearer ${token}`
        }
      }));
    }

    return next.handle(req);
  }
}