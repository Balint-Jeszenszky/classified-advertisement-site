import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ApiModule as GatewayApiModule, Configuration as GatewayApiConfiguration } from './openapi/gateway';
import { ApiModule as UserserviceApiModule, Configuration as UserserviceApiConfiguration } from './openapi/userservice';
import { ApiModule as AdvertisementserviceApiModule, Configuration as AdvertisementserviceApiConfiguration } from './openapi/advertisementservice';
import { NavbarComponent } from './components/navbar/navbar.component';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { SubmenuComponent } from './components/navbar/submenu/submenu.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    SubmenuComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    GatewayApiModule.forRoot(() => new GatewayApiConfiguration({ basePath: '' })),
    UserserviceApiModule.forRoot(() => new UserserviceApiConfiguration({ basePath: '/api/user' })),
    AdvertisementserviceApiModule.forRoot(() => new AdvertisementserviceApiConfiguration({ basePath: '/api/advertisement' })),
    MatToolbarModule,
    MatMenuModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
