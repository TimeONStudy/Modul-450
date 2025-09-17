import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Book } from '../../../generated/model/book';
import { RentBookRequest } from '../../../generated/model/rentBookRequest';
import { RentBookResponse } from '../../../generated/model/rentBookResponse';
import { ReturnBookRequest } from '../../../generated/model/returnBookRequest';
import { ReturnBookResponse } from '../../../generated/model/returnBookResponse';

@Component({
  selector: 'app-book-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="card">
      <div class="card-header">
        <div class="book-header-actions">
          <a routerLink="/books" class="btn btn-secondary">← Back to Library</a>
          <h2 class="card-title">{{ book()?.name || '📖 Book Details' }}</h2>
        </div>
      </div>
      
      <div *ngIf="loading()" class="loading">
        <p>Loading book details...</p>
      </div>
      
      <div *ngIf="error()" class="error">
        <p>{{ error() }}</p>
        <button class="btn btn-primary" (click)="loadBook()">
          🔄 Try Again
        </button>
      </div>
      
      <div *ngIf="book() && !loading() && !error()" class="book-detail-content">
        <div class="book-info">
          <div class="book-main-info">
            <h3>{{ book()!.name }}</h3>
            <span class="badge" [ngClass]="book()!.available ? 'badge-success' : 'badge-danger'">
              {{ book()!.available ? '✨ Available' : '📖 Rented' }}
            </span>
          </div>
          
          <div class="book-details-grid">
            <div class="detail-item">
              <label>👤 Author</label>
              <span>{{ book()!.author }}</span>
            </div>
            
            <div class="detail-item">
              <label>📂 Category</label>
              <span>{{ book()!.category.name }}</span>
            </div>
            
            <div class="detail-item">
              <label>🆔 Book ID</label>
              <span>{{ book()!.id }}</span>
            </div>
            
            <div class="detail-item" *ngIf="book()!.rentedBy">
              <label>👥 Rented by</label>
              <span>{{ book()!.rentedBy }}</span>
            </div>
            
            <div class="detail-item" *ngIf="book()!.rentedDate">
              <label>📅 Rented on</label>
              <span>{{ formatDate(book()!.rentedDate) }}</span>
            </div>
          </div>
        </div>
        
        <div class="book-actions">
          <button 
            *ngIf="book()!.available" 
            class="btn btn-success btn-large" 
            (click)="rentBook()"
            [disabled]="renting()">
            {{ renting() ? '⏳ Renting...' : '📖 Rent This Book' }}
          </button>
          
          <button 
            *ngIf="!book()!.available" 
            class="btn btn-danger btn-large" 
            (click)="returnBook()"
            [disabled]="returning()">
            {{ returning() ? '⏳ Returning...' : '↩️ Return This Book' }}
          </button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .book-header-actions {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    
    .book-detail-content {
      display: flex;
      flex-direction: column;
      gap: 2rem;
    }
    
    .book-main-info {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1.5rem;
    }
    
    .book-main-info h3 {
      font-size: 1.5rem;
      font-weight: 600;
      color: #333;
      margin: 0;
    }
    
    .book-details-grid {
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
    
    .book-actions {
      display: flex;
      justify-content: center;
      padding-top: 1rem;
      border-top: 1px solid #eee;
    }
    
    .btn-large {
      padding: 0.75rem 2rem;
      font-size: 1rem;
    }
    
    @media (max-width: 768px) {
      .book-header-actions {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
      }
      
      .book-main-info {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
      }
      
      .book-details-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class BookDetailComponent implements OnInit {
  book = signal<Book | null>(null);
  loading = signal(false);
  error = signal<string | null>(null);
  renting = signal(false);
  returning = signal(false);

  private readonly baseUrl = 'http://localhost:8080/api/book';

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const bookId = params['id'];
      if (bookId) {
        this.loadBook(bookId);
      }
    });
  }

  loadBook(bookId?: string) {
    const id = bookId || this.route.snapshot.params['id'];
    if (!id) return;
    
    this.loading.set(true);
    this.error.set(null);
    
    this.http.get<Book>(`${this.baseUrl}/getBookById/${id}`).subscribe({
      next: (book) => {
        this.book.set(book);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load book details. Please try again.');
        this.loading.set(false);
        console.error('Error loading book:', err);
      }
    });
  }

  rentBook() {
    const userId = prompt('Enter User ID to rent this book:');
    if (!userId) return;
    
    this.renting.set(true);
    
    const request: RentBookRequest = {
      bookId: this.book()!.id,
      userId: userId
    };
    
    this.http.post<RentBookResponse>(`${this.baseUrl}/rentBook`, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadBook(); // Reload to get updated data
          alert('Book rented successfully!');
        } else {
          alert('Failed to rent book: ' + response.message);
        }
        this.renting.set(false);
      },
      error: (err) => {
        alert('Error renting book: ' + (err.error?.message || err.message));
        this.renting.set(false);
        console.error('Error renting book:', err);
      }
    });
  }

  returnBook() {
    const userId = prompt('Enter User ID to return this book:');
    if (!userId) return;
    
    this.returning.set(true);
    
    const request: ReturnBookRequest = {
      bookId: this.book()!.id,
      userId: userId
    };
    
    this.http.post<ReturnBookResponse>(`${this.baseUrl}/returnBook`, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadBook(); // Reload to get updated data
          alert('Book returned successfully!');
        } else {
          alert('Failed to return book: ' + response.message);
        }
        this.returning.set(false);
      },
      error: (err) => {
        alert('Error returning book: ' + (err.error?.message || err.message));
        this.returning.set(false);
        console.error('Error returning book:', err);
      }
    });
  }

  formatDate(dateString: string | null | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
  }
}
