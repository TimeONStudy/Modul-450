import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { User } from '../../../generated/model/user';
import { UserList } from '../../../generated/model/userList';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">👥 Library Members</h2>
        <p>Manage and view all library members and their information</p>
      </div>
      
      <div *ngIf="loading()" class="loading">
        <p>Loading members...</p>
      </div>
      
      <div *ngIf="error()" class="error">
        <p>{{ error() }}</p>
        <button class="btn btn-primary" (click)="loadUsers()">
          🔄 Try Again
        </button>
      </div>
      
      <div *ngIf="!loading() && !error()" class="users-container">
        <div class="users-grid grid grid-2">
          <div *ngFor="let user of users()" class="user-card card">
            <div class="user-header">
              <h3 class="user-name">{{ user.name }}</h3>
              <span class="badge" [ngClass]="user.authenticated ? 'badge-success' : 'badge-secondary'">
                {{ user.authenticated ? '✅ Verified' : '⏳ Pending' }}
              </span>
            </div>
            
            <div class="user-details">
              <p><strong>📧 Email:</strong> {{ user.email }}</p>
              <p><strong>🆔 User ID:</strong> {{ user.id }}</p>
            </div>
            
            <div class="user-actions">
              <a [routerLink]="['/users', user.id]" class="btn btn-primary">
                👁️ View Profile
              </a>
              <button class="btn btn-secondary" (click)="viewRentedBooks(user)">
                📚 View Books
              </button>
            </div>
          </div>
        </div>
        
        <div *ngIf="users().length === 0" class="no-users">
          <p>👥 No members found in the system.</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .users-container {
      margin-top: 1rem;
    }
    
    .users-grid {
      margin-bottom: 2rem;
    }
    
    .user-card {
      transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
    
    .user-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    
    .user-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1rem;
    }
    
    .user-name {
      font-size: 1.1rem;
      font-weight: 600;
      color: #333;
      margin: 0;
      flex: 1;
      margin-right: 0.5rem;
    }
    
    .user-details {
      margin-bottom: 1rem;
    }
    
    .user-details p {
      margin: 0.25rem 0;
      font-size: 0.9rem;
      color: #666;
    }
    
    .user-actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
    
    .no-users {
      text-align: center;
      padding: 2rem;
      color: #666;
    }
  `]
})
export class UsersComponent implements OnInit {
  users = signal<User[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);

  private readonly baseUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.loading.set(true);
    this.error.set(null);
    
    this.http.get<UserList>(`${this.baseUrl}/getUsers`).subscribe({
      next: (response) => {
        this.users.set(response.users || []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load users. Please try again.');
        this.loading.set(false);
        console.error('Error loading users:', err);
      }
    });
  }

  viewRentedBooks(user: User) {
    // Navigate to user detail page which will show rented books
    window.location.href = `/users/${user.id}`;
  }
}
