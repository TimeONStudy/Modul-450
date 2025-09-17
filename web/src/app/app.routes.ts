import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/books', pathMatch: 'full' },
  { path: 'books', loadComponent: () => import('./components/pages/books/books.component').then(m => m.BooksComponent) },
  { path: 'books/:id', loadComponent: () => import('./components/pages/book-detail/book-detail.component').then(m => m.BookDetailComponent) },
  { path: 'users', loadComponent: () => import('./components/pages/users/users.component').then(m => m.UsersComponent) },
  { path: 'users/:id', loadComponent: () => import('./components/pages/user-detail/user-detail.component').then(m => m.UserDetailComponent) },
  { path: 'categories', loadComponent: () => import('./components/pages/categories/categories.component').then(m => m.CategoriesComponent) },
  { path: '**', redirectTo: '/books' }
];
