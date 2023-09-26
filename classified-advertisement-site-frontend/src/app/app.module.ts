import { NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ApiModule as GatewayApiModule, Configuration as GatewayApiConfiguration } from './openapi/gateway';
import { ApiModule as UserserviceApiModule, Configuration as UserserviceApiConfiguration } from './openapi/userservice';
import { ApiModule as AdvertisementserviceApiModule, Configuration as AdvertisementserviceApiConfiguration } from './openapi/advertisementservice';
import { ApiModule as ImageserviceApiModule, Configuration as ImageserviceApiConfiguration } from './openapi/imageprocessingservice';
import { ApiModule as WebscraperserviceApiModule, Configuration as WebscraperserviceApiConfiguration } from './openapi/webscraperservice';
import { ApiModule as NotificationserviceApiModule, Configuration as NotificationserviceApiConfiguration } from './openapi/notificationservice';
import { NavbarComponent } from './components/navbar/navbar.component';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatMenuModule } from '@angular/material/menu';
import { SubmenuComponent } from './components/navbar/submenu/submenu.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { ChatGraphQLModule } from './graphql/chat/graphql.module';

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
    ImageserviceApiModule.forRoot(() => new ImageserviceApiConfiguration({ basePath: '/api/images' })),
    WebscraperserviceApiModule.forRoot(() => new WebscraperserviceApiConfiguration({ basePath: '' })),
    NotificationserviceApiModule.forRoot(() => new NotificationserviceApiConfiguration({ basePath: '' })),
    MatToolbarModule,
    MatMenuModule,
    ServiceWorkerModule.register('ngsw-worker.js', {
      // enabled: !isDevMode(),
      // Register the ServiceWorker as soon as the application is stable
      // or after 30 seconds (whichever comes first).
      registrationStrategy: 'registerWhenStable:30000'
    }),
    ChatGraphQLModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
