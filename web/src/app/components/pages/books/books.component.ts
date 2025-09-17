import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Book } from '../../../generated/model/book';
import { BookList } from '../../../generated/model/bookList';
import { RentBookRequest } from '../../../generated/model/rentBookRequest';
import { RentBookResponse } from '../../../generated/model/rentBookResponse';
import { ReturnBookRequest } from '../../../generated/model/returnBookRequest';
import { ReturnBookResponse } from '../../../generated/model/returnBookResponse';

@Component({
  selector: 'app-books',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">📚 Library Collection</h2>
        <p>Discover and manage our extensive book collection</p>
      </div>
      
      <div *ngIf="loading()" class="loading">
        <p>Loading your library...</p>
      </div>
      
      <div *ngIf="error()" class="error">
        <p>{{ error() }}</p>
        <button class="btn btn-primary" (click)="loadBooks()">
          🔄 Try Again
        </button>
      </div>
      
      <div *ngIf="!loading() && !error()" class="books-container">
        <div class="books-grid grid grid-3">
          <div *ngFor="let book of books()" class="book-card card">
            <div class="book-header">
              <h3 class="book-title">{{ book.name }}</h3>
              <span class="badge" [ngClass]="book.available ? 'badge-success' : 'badge-danger'">
                {{ book.available ? '✨ Available' : '📖 Rented' }}
              </span>
            </div>
            
            <div class="book-details">
              <p><strong>👤 Author:</strong> {{ book.author }}</p>
              <p><strong>📂 Category:</strong> {{ book.category.name }}</p>
              <p *ngIf="book.rentedBy"><strong>👥 Rented by:</strong> {{ book.rentedBy }}</p>
              <p *ngIf="book.rentedDate"><strong>📅 Rented on:</strong> {{ formatDate(book.rentedDate) }}</p>
            </div>
            
            <div class="book-actions">
              <a [routerLink]="['/books', book.id]" class="btn btn-primary">
                👁️ View Details
              </a>
              <button 
                *ngIf="book.available" 
                class="btn btn-success" 
                (click)="rentBook(book)"
                [disabled]="renting()">
                {{ renting() ? '⏳ Renting...' : '📖 Rent Book' }}
              </button>
              <button 
                *ngIf="!book.available" 
                class="btn btn-danger" 
                (click)="returnBook(book)"
                [disabled]="returning()">
                {{ returning() ? '⏳ Returning...' : '↩️ Return Book' }}
              </button>
            </div>
          </div>
        </div>
        
        <div *ngIf="books().length === 0" class="no-books">
          <p>📚 No books found in the library. Check back later!</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .books-container {
      margin-top: 1rem;
    }
    
    .books-grid {
      margin-bottom: 2rem;
    }
    
    .book-card {
      transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
    
    .book-card:hover {
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
      font-size: 1.1rem;
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
      font-size: 0.9rem;
      color: #666;
    }
    
    .book-actions {
      display: flex;
      gap: 0.5rem;
      flex-wrap: wrap;
    }
    
    .no-books {
      text-align: center;
      padding: 2rem;
      color: #666;
    }
  `]
})
export class BooksComponent implements OnInit {
  books = signal<Book[]>([]);
  loading = signal(false);
  error = signal<string | null>(null);
  renting = signal(false);
  returning = signal(false);

  private readonly baseUrl = 'http://localhost:8080/api/book';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.loading.set(true);
    this.error.set(null);
    
    this.http.get<BookList>(`${this.baseUrl}/getBooks`).subscribe({
      next: (response) => {
        this.books.set(response.books || []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load books. Please try again.');
        this.loading.set(false);
        console.error('Error loading books:', err);
      }
    });
  }

  rentBook(book: Book) {
    const userId = prompt('Enter User ID to rent this book:');
    if (!userId) return;
    
    this.renting.set(true);
    
    const request: RentBookRequest = {
      bookId: book.id,
      userId: userId
    };
    
    this.http.post<RentBookResponse>(`${this.baseUrl}/rentBook`, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadBooks(); // Reload to get updated data
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

  returnBook(book: Book) {
    const userId = prompt('Enter User ID to return this book:');
    if (!userId) return;
    
    this.returning.set(true);
    
    const request: ReturnBookRequest = {
      bookId: book.id,
      userId: userId
    };
    
    this.http.post<ReturnBookResponse>(`${this.baseUrl}/returnBook`, request).subscribe({
      next: (response) => {
        if (response.success) {
          this.loadBooks(); // Reload to get updated data
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
