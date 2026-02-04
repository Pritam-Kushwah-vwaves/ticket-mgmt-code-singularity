import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const authHeader = authService.getAuthHeader();

  // Add auth header to all requests (including OPTIONS preflight for CORS)
  if (authHeader) {
    req = req.clone({
      setHeaders: {
        Authorization: authHeader
      }
    });
  } else {
    // If no auth header and trying to access protected endpoint, redirect to login
    const url = req.url;
    if (url.includes('/tickets') && !url.includes('/check') && !url.includes('/auth')) {
      console.warn('No authentication header found for protected endpoint:', url);
    }
  }

  return next(req);
};
