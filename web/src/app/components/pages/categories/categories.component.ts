import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Category } from '../../../generated/model/category';
import { Book } from '../../../generated/model/book';
import { CategoryList } from '../../../generated/model/categoryList';
import { BookList } from '../../../generated/model/bookList';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">📂 Book Categories</h2>
        <p>Explore our collection organized by categories</p>
      </div>
      
      <div *ngIf="loading()" class="loading">
        <p>Loading categories...</p>
      </div>
      
      <div *ngIf="error()" class="error">
        <p>{{ error() }}</p>
        <button class="btn btn-primary" (click)="loadCategories()">
          🔄 Try Again
        </button>
      </div>
      
      <div *ngIf="!loading() && !error()" class="categories-container">
        <div class="categories-grid grid grid-3">
          <div *ngFor="let category of categories()" class="category-card card">
            <div class="category-header">
              <h3 class="category-name">{{ category.name }}</h3>
              <span class="badge badge-secondary">#{{ category.id }}</span>
            </div>
            
            <div class="category-actions">
              <button 
                class="btn btn-primary" 
                (click)="loadBooksByCategory(category)"
                [disabled]="loadingBooks()">
                {{ loadingBooks() ? '⏳ Loading...' : '📚 View Books' }}
              </button>
            </div>
          </div>
        </div>
        
        <div *ngIf="categories().length === 0" class="no-categories">
          <p>📂 No categories found.</p>
        </div>
      </div>
    </div>
    
    <!-- Books by Category Section -->
    <div *ngIf="selectedCategory() && !loadingBooks() && !booksError()" class="card books-by-category">
      <div class="card-header">
        <h3 class="card-title">📚 Books in "{{ selectedCategory()!.name }}"</h3>
        <button class="btn btn-secondary" (click)="clearSelectedCategory()">
          ❌ Close
        </button>
      </div>
      
      <div class="books-grid grid grid-3">
        <div *ngFor="let book of booksInCategory()" class="book-card card">
          <div class="book-header">
            <h4 class="book-title">{{ book.name }}</h4>
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
          </div>
        </div>
      </div>
      
      <div *ngIf="booksInCategory().length === 0" class="no-books">
        <p>📚 No books found in this category.</p>
      </div>
    </div>
    
    <!-- Loading Books State -->
    <div *ngIf="loadingBooks()" class="card">
      <div class="loading">
        <p>Loading books for {{ selectedCategory()?.name }}...</p>
      </div>
    </div>
    
    <!-- Books Error State -->
    <div *ngIf="booksError()" class="card">
      <div class="error">
        <p>{{ booksError() }}</p>
        <button class="btn btn-primary" (click)="loadBooksByCategory(selectedCategory()!)">
          🔄 Try Again
        </button>
      </div>
    </div>
  `,
  styles: [`
    .categories-container {
      margin-top: 1rem;
    }
    
    .categories-grid {
      margin-bottom: 2rem;
    }
    
    .category-card {
      transition: transform 0.2s ease, box-shadow 0.2s ease;
    }
    
    .category-card:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    
    .category-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 1rem;
    }
    
    .category-name {
      font-size: 1.1rem;
      font-weight: 600;
      color: #333;
      margin: 0;
      flex: 1;
      margin-right: 0.5rem;
    }
    
    .category-actions {
      display: flex;
      gap: 0.5rem;
    }
    
    .no-categories {
      text-align: center;
      padding: 2rem;
      color: #666;
    }
    
    .books-by-category {
      margin-top: 2rem;
    }
    
    .books-by-category .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .books-grid {
      margin-top: 1rem;
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
      font-size: 0.9rem;
      color: #666;
    }
    
    .book-actions {
      display: flex;
      gap: 0.5rem;
    }
    
    .no-books {
      text-align: center;
      padding: 2rem;
      color: #666;
      background-color: #f8f9fa;
      border-radius: 5px;
    }
    
    @media (max-width: 768px) {
      .books-by-category .card-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
      }
      
      .category-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.5rem;
      }
      
      .books-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class CategoriesComponent implements OnInit {
  categories = signal<Category[]>([]);
  booksInCategory = signal<Book[]>([]);
  selectedCategory = signal<Category | null>(null);
  loading = signal(false);
  loadingBooks = signal(false);
  error = signal<string | null>(null);
  booksError = signal<string | null>(null);

  private readonly baseUrl = 'http://localhost:8080/api/category';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loadCategories();
  }

  loadCategories() {
    this.loading.set(true);
    this.error.set(null);
    
    this.http.get<CategoryList>(`${this.baseUrl}/getCategories`).subscribe({
      next: (response) => {
        this.categories.set(response.categories || []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load categories. Please try again.');
        this.loading.set(false);
        console.error('Error loading categories:', err);
      }
    });
  }

  loadBooksByCategory(category: Category) {
    this.selectedCategory.set(category);
    this.loadingBooks.set(true);
    this.booksError.set(null);
    
    this.http.get<BookList>(`${this.baseUrl}/getBooksByCategory/${category.id}`).subscribe({
      next: (response) => {
        this.booksInCategory.set(response.books || []);
        this.loadingBooks.set(false);
      },
      error: (err) => {
        this.booksError.set('Failed to load books for this category. Please try again.');
        this.loadingBooks.set(false);
        console.error('Error loading books by category:', err);
      }
    });
  }

  clearSelectedCategory() {
    this.selectedCategory.set(null);
    this.booksInCategory.set([]);
    this.booksError.set(null);
  }

  formatDate(dateString: string | null | undefined): string {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString();
  }
}
