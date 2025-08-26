export * from './books.service';
import { BooksService } from './books.service';
export * from './categories.service';
import { CategoriesService } from './categories.service';
export * from './users.service';
import { UsersService } from './users.service';
export const APIS = [BooksService, CategoriesService, UsersService];
