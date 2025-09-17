import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { User } from '../../../generated/model/user';
import { Book } from '../../../generated/model/book';
import { BookList } from '../../../generated/model/bookList';

@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="card">
      <div class="card-header">
        <div class="user-header-actions">
          <a routerLink="/users" class="btn btn-secondary">← Back to Members</a>
          <h2 class="card-title">{{ user()?.name || '👤 Member Profile' }}</h2>
        </div>
      </div>
      
      <div *ngIf="loading()" class="loading">
        <p>Loading member details...</p>
      </div>
      
      <div *ngIf="error()" class="error">
        <p>{{ error() }}</p>
        <button class="btn btn-primary" (click)="loadUser()">
          🔄 Try Again
        </button>
      </div>
      
      <div *ngIf="user() && !loading() && !error()" class="user-detail-content">
        <div class="user-info">
          <div class="user-main-info">
            <h3>{{ user()!.name }}</h3>
            <span class="badge" [ngClass]="user()!.authenticated ? 'badge-success' : 'badge-secondary'">
              {{ user()!.authenticated ? '✅ Verified' : '⏳ Pending' }}
            </span>
          </div>
          
          <div class="user-details-grid">
            <div class="detail-item">
              <label>📧 Email</label>
              <span>{{ user()!.email }}</span>
            </div>
            
            <div class="detail-item">
              <label>🆔 User ID</label>
              <span>{{ user()!.id }}</span>
            </div>
          </div>
        </div>
        
        <div class="rented-books-section">
          <h4>📚 Currently Rented Books</h4>
          
          <div *ngIf="loadingRentedBooks()" class="loading">
            <p>Loading rented books...</p>
          </div>
          
          <div *ngIf="rentedBooksError()" class="error">
            <p>{{ rentedBooksError() }}</p>
            <button class="btn btn-primary" (click)="loadRentedBooks()">
              🔄 Try Again
            </button>
          </div>
          
          <div *ngIf="!loadingRentedBooks() && !rentedBooksError()" class="rented-books-content">
            <div *ngIf="rentedBooks().length > 0" class="rented-books-grid grid grid-3">
              <div *ngFor="let book of rentedBooks()" class="rented-book-card card">
                <div class="book-header">
                  <h5 class="book-title">{{ book.name }}</h5>
                  <span class="badge badge-danger">📖 Rented</span>
                </div>
                
                <div class="book-details">
                  <p><strong>👤 Author:</strong> {{ book.author }}</p>
                  <p><strong>📂 Category:</strong> {{ book.category.name }}</p>
                  <p><strong>📅 Rented on:</strong> {{ formatDate(book.rentedDate) }}</p>
                </div>
                
                <div class="book-actions">
                  <a [routerLink]="['/books', book.id]" class="btn btn-primary btn-sm">
                    👁️ View Book
                  </a>
                </div>
              </div>
            </div>
            
            <div *ngIf="rentedBooks().length === 0" class="no-rented-books">
              <p>📚 This member has no rented books.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .user-header-actions {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .user-detail-content {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }
    
    .user-main-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    
    .user-main-info h3 {
      font-size: 1.5rem;
      font-weight: 600;
      color: #333;
      margin: 0;
    }
    
    .user-details-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1rem;
    }
    
    .detail-item {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }
    
    .detail-item label {
      font-weight: 600;
      color: #555;
      font-size: 0.9rem;
    }
    
    .detail-item span {
      color: #333;
      font-size: 1rem;
    }
    
    .rented-books-section {
      border-top: 1px solid #eee;
      padding-top: 2rem;
    }
    
    .rented-books-section h4 {
      font-size: 1.25rem;
      font-weight: 600;
      color: #333;
      margin-bottom: 1rem;
    }
    
    .rented-books-content {
      margin-top: 1rem;
    }
    
    .rented-books-grid {
      margin-bottom: 2rem;
    }
    
    .rented-book-card {
      transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
    
    .rented-book-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    
    .book-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1rem;
    }
    
    .book-title {
      font-size: 1rem;
      font-weight: 600;
      color: #333;
      margin: 0;
      flex: 1;
      margin-right: 0.5rem;
    }
    
    .book-details {
      margin-bottom: 1rem;
    }
    
    .book-details p {
      margin: 0.25rem 0;
      font-size: 0.85rem;
      color: #666;
    }
    
    .book-actions {
      display: flex;
      gap: 0.5rem;
    }
    
    .btn-sm {
      padding: 0.375rem 0.75rem;
      font-size: 0.8rem;
    }
    
    .no-rented-books {
      text-align: center;
      padding: 2rem;
      color: #666;
      background-color: #f8f9fa;
      border-radius: 5px;
    }
    
    @media (max-width: 768px) {
      .user-header-actions {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
      }
      
      .user-main-info {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
      }
      
      .user-details-grid {
        grid-template-columns: 1fr;
      }
      
      .rented-books-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class UserDetailComponent implements OnInit {
  user = signal<User | null>(null);
  rentedBooks = signal<Book[]>([]);
  loading = signal(false);
  loadingRentedBooks = signal(false);
  error = signal<string | null>(null);
  rentedBooksError = signal<string | null>(null);

  private readonly userBaseUrl = 'http://localhost:8080/api/user';

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const userId = params['id'];
      if (userId) {
        this.loadUser(userId);
        this.loadRentedBooks(userId);
      }
    });
  }

  loadUser(userId?: string) {
    const id = userId || this.route.snapshot.params['id'];
    if (!id) return;
    
    this.loading.set(true);
    this.error.set(null);
    
    this.http.get<User>(`${this.userBaseUrl}/getUserById/${id}`).subscribe({
      next: (user) => {
        this.user.set(user);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load user details. Please try again.');
        this.loading.set(false);
        console.error('Error loading user:', err);
      }
    });
  }

  loadRentedBooks(userId?: string) {
    const id = userId || this.route.snapshot.params['id'];
    if (!id) return;
    
    this.loadingRentedBooks.set(true);
    this.rentedBooksError.set(null);
    
    this.http.get<BookList>(`${this.userBaseUrl}/getUserRentedBooks/${id}`).subscribe({
      next: (response) => {
        this.rentedBooks.set(response.books || []);
        this.loadingRentedBooks.set(false);
      },
      error: (err) => {
        this.rentedBooksError.set('Failed to load rented books. Please try again.');
        this.loadingRentedBooks.set(false);
        console.error('Error loading rented books:', err);
      }
    });
  }

  formatDate(dateString: string | null | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
  }
}
