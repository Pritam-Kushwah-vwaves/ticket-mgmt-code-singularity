import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, of } from 'rxjs';
import { map, tap, switchMap, catchError } from 'rxjs/operators';
import { User, Role } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8082/auth';
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private permissions: string[] = [];
  constructor(private http: HttpClient) {
    this.loadPermissionsFromStorage();
  }


setPermissions(perms: string[]) {
  this.permissions = perms;
  localStorage.setItem('permissions', JSON.stringify(perms));
}

loadPermissionsFromStorage() {
  const stored = localStorage.getItem('permissions');
  this.permissions = stored ? JSON.parse(stored) : [];
}

hasPermission(permission: string): boolean {
  return this.permissions.includes(permission);
}

  // Check if user is logged in (check localStorage)
  isLoggedIn(): boolean {
    return localStorage.getItem('isAuthenticated') === 'true';
  }


  clearAuth() {
    this.permissions = [];
    localStorage.removeItem('permissions');
  }

  // Get current username
  getCurrentUsername(): string | null {
    return localStorage.getItem('username');
  }

  // Get current user role
  getCurrentUserRole(): Role | null {
    const roleStr = localStorage.getItem('userRole');
    return roleStr ? (roleStr as Role) : null;
  }

  // Check if current user is admin
  isAdmin(): boolean {
    return this.getCurrentUserRole() === Role.ADMIN;
  }

  // Check if current user is general user
  isGeneral(): boolean {
    return this.getCurrentUserRole() === Role.GENERAL;
  }

  // Get current user info (fetches from backend)
  getCurrentUserInfo(): Observable<User> {
    return this.http.get<User>('http://localhost:8082/users/me');
  }

  // Refresh user role from backend
  refreshUserRole(): Observable<Role | null> {
    return this.getCurrentUserInfo().pipe(
      tap((userInfo: User) => {
        if (userInfo.role) {
          console.log('Role refreshed:', userInfo.role);
          localStorage.setItem('userRole', userInfo.role);
        }
      }),
      map((userInfo: User) => userInfo.role || null),
      catchError((error) => {
        console.error('Failed to refresh role:', error);
        return of(null);
      })
    );
  }

  // Get authorization header (Basic Auth)
  getAuthHeader(): string | null {
    const username = localStorage.getItem('username');
    const password = sessionStorage.getItem('password');
    
    if (username && password) {
      // Create Basic Auth header: base64(username:password)
      const credentials = btoa(`${username}:${password}`);
      return `Basic ${credentials}`;
    }
    return null;
  }

  // Register new user
  register(user: User): Observable<string> {
    return this.http.post(`${this.baseUrl}/register`, user, {
      responseType: 'text'
    }).pipe(
      map((response: any) => response as string)
    );
  }

  // Login user
  login(username: string, password: string): Observable<string> {
    const user = { username, password };
  
    return this.http.post(`${this.baseUrl}/login`, user, {
      responseType: 'text'
    }).pipe(
      switchMap((response: string) => {
  
        if (response !== 'LOGIN_SUCCESS') {
          return of(response);
        }
  
        // ✅ Auth success
        localStorage.setItem('isAuthenticated', 'true');
        localStorage.setItem('username', username);
  
        // ⚠️ Only for Basic Auth (temporary)
        sessionStorage.setItem('password', password);
  
        this.isAuthenticatedSubject.next(true);
  
        // ✅ Fetch logged-in user info (ROLE + PERMISSIONS)
        return this.getCurrentUserInfo().pipe(
          tap((userInfo: User) => {
            console.log('User info:', userInfo);
  
            // ✅ ROLE
            if (userInfo.role) {
              localStorage.setItem('userRole', userInfo.role);
            }
  
            // ✅ PERMISSIONS (IMPORTANT)
            if (userInfo.permissions) {
              this.setPermissions(userInfo.permissions);
            } else {
              this.setPermissions([]);
            }
          }),
          map(() => response),
          catchError((error) => {
            console.warn('Failed to fetch /users/me', error);
  
            // Fail-safe (do NOT break login)
            this.setPermissions([]);
            return of(response);
          })
        );
      })
    );
  }
  

  // Logout user
  logout(): void {
    localStorage.removeItem('isAuthenticated');
    localStorage.removeItem('username');
    localStorage.removeItem('userRole');
    sessionStorage.removeItem('password');
    this.isAuthenticatedSubject.next(false);
  }
}
