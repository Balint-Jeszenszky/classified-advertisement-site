import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { ApiModule as GatewayApiModule, Configuration as GatewayApiConfiguration } from './openapi/gateway';
import { ApiModule as UserserviceApiModule, Configuration as UserserviceApiConfiguration } from './openapi/userservice';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    GatewayApiModule.forRoot(() => new GatewayApiConfiguration({ basePath: '' })),
    UserserviceApiModule.forRoot(() => new UserserviceApiConfiguration({ basePath: '/api/user' })),
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
