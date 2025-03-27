# BookMark
An Android application developed as part of the ECM2425 Mobile and Ubiquitous Computing coursework.

## Introduction
### Purpose of Application
BookMark is an Android app designed for book lovers who often come across interesting books but aren’t ready to buy them just yet. Inspired by the experience of browsing a bookstore and discovering books to keep in mind, BookMark allows users to search for books using the Google Books API, preview details, and "mark" books they want to track for later.

### Functionality of Application
* Search for Books – Find books using keywords, powered by the Google Books API.

* Preview & Buy – View book details, preview them (if available), or access a purchase link.

* Mark Books – Save books you want to keep an eye on for later reference.

* View Marked Books – Access your saved books in the Marked Books section, with sorting options (by marking date, author, or publication date).

* Share with Others – Use Android’s built-in sharing options (Android Sharesheet) to send book details via your preferred apps.

###  How to Use the Application
1. **Search for Books**  
   - Open the app and enter a keyword in the search bar.  
   - Browse the list of books that match your search.  
   - Each book in the list has two icons at the bottom corner:  
     - 🔖 Bookmark Icon: A quick shortcut to mark the book without opening its details.  
     - 🔗 Share Icon: Opens a menu to share the book using Android’s built-in sharing options.  

2. **View Book Details**  
   - Tap on a book to see more details.  
   - Choose from the following options:  
     - Preview: View the book on Google Books.  
     - Buy: Open a link to purchase the book.  
     - Mark: Save the book for later reference.  

3. **Manage Marked Books**  
   - Go to the Marked Books section to see books you’ve saved.  
   - Sort them by marking date, author, or publication date.  

## Design Rationale
**1. Options Menu for Easy Navigation**
- The options menu** in `MarkedBooksActivity` allows easy switching between Search and Marked Books screens, enhancing navigation and providing a straightforward way to access the main features.

**2. Reusing `BookAdapter` and `BookInfo`**
- `BookAdapter` and `BookInfo` are reused in both `MainActivity` and `MarkedBooksActivity`, avoiding redundant code and ensuring consistency. This simplifies maintenance and improves code reusability.

 **3. Separation of Concerns**
- Activities manage user interface logic, such as displaying book lists and handling interactions.
- The adapter class, `BookAdapter`, acts as intermediary, binding data to views and updating the UI.
- The model class, `BookInfo`, stores the actual data, keeping app logic and data separate and improving maintainability.

 **4. Data Persistence**
- SharedPreferences is used to store marked books, ensuring user preferences persist across app sessions. It provides a simple and efficient way to handle small data sets without the complexity of a database.

 **5. User Interface and Interaction**
- A simple, intuitive UI is designed to cater to users across a wide age range, including elderly users who may not be familiar with more elaborate interfaces. Clear navigation and accessible controls make the app easy to use for everyone.
- RecyclerView and BookAdapter: The `RecyclerView` is used to display a large list of books efficiently, while `BookAdapter` manages data binding and UI updates (e.g., toggling book mark status).
- Context Menu: The context menu allows users to perform additional actions like sharing a book, enhancing functionality without cluttering the interface.

 **6. User Experience**
- Sorting: Sorting options such as "Latest Marked", "Oldest Marked", "Published Date", and "Author" give users control over how marked books are displayed.
- Thumbnail Loading: Glide is used to load book thumbnails efficiently, with placeholders and error images to keep the UI responsive even when images fail to load.

 **7. Intents (Explicit and Implicit)**
- Explicit Intents are used for internal navigation between specific activities, ensuring clear and predictable app flow (e.g., navigating from BookDetails to MainActivity or MarkedBooksActivity).
- Implicit Intents handle actions that interact with external apps (e.g., opening a web browser for "Preview" or "Buy" links), offering flexibility by letting Android choose the best app to handle the action.
- 
## Novel Features
text

## Challenges and Future Imrpovements
### 4.1) Challenges and Solutions
| Challenge        | Solution                                                              |
|---------------|------------------------------------------------------------------------|
| Platform      | Android                                                                |
| Course Code   | ECM2425                                                                |

### 4.2) Improvements and Additional Features
test
