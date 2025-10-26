# Personal Expense Tracker (PET)

A desktop application for managing personal budgets, tracking income and expenses, built with Java Swing following Domain-Driven Design principles and Clean Architecture.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
  - [Architectural Layers](#architectural-layers)
  - [Design Principles](#design-principles)
  - [Directory Structure](#directory-structure)
- [Functional Design](#functional-design)
  - [Core Features](#core-features)
  - [Domain Model](#domain-model)
  - [User Workflows](#user-workflows)
- [Technical Design](#technical-design)
  - [Technology Stack](#technology-stack)
  - [Component Details](#component-details)
  - [Data Persistence](#data-persistence)
  - [Design Patterns](#design-patterns)
- [Deployment](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Building the Project](#building-the-project)
  - [Running the Application](#running-the-application)
- [Testing](#testing)
- [Project Structure](#project-structure)

---

## Overview

Personal Expense Tracker (PET) is a desktop application designed to help users manage their personal finances through budget creation and transaction tracking. The application emphasizes clean code, maintainability, and domain-driven design principles.

**Key Highlights:**
- Currency support
- Income and expense categorization
- File-based persistence with caching
- MVC architecture with separation of concerns
- Domain-driven design with rich domain models

---

## Architecture

### Architectural Layers

The application follows a **Clean Architecture** approach with clear separation between layers:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│              (Views, Controllers, UI Logic)             │
├─────────────────────────────────────────────────────────┤
│                    Application Layer                    │
│           (Models, Input/Output DTOs, Validators)       │
├─────────────────────────────────────────────────────────┤
│                   Infrastructure Layer                  │
│        (Repositories, Storage, Cache, Services)         │
├─────────────────────────────────────────────────────────┤
│                      Domain Layer                       │
│         (Business Logic, Entities, Value Objects)       │
└─────────────────────────────────────────────────────────┘
```

### Design Principles

1. **Separation of Concerns**: Each layer has distinct responsibilities
2. **Dependency Rule**: Dependencies point inward (Domain ← Infrastructure ← Application ← Presentation)
3. **Domain-Driven Design**: Rich domain models with business logic encapsulation
4. **SOLID Principles**: 
   - Single Responsibility: Each class has one reason to change
   - Open/Closed: Extensible through interfaces
   - Liskov Substitution: Transaction hierarchy is substitutable
   - Interface Segregation: Small, focused interfaces
   - Dependency Inversion: Depend on abstractions (interfaces)

### Directory Structure

```
src/main/java/com/blendwerk/pet/
├── application/                                # Application layer
│   ├── Program.java                            # Entry point & dependency injection
│   ├── controllers/                            # MVC Controllers
│   ├── models/                                 # MVC Models
│   └── views/                                  # MVC Views
│       ├── controls/                           
│       └── windows/                            
├── domain/                                     # Domain layer (core business logic)
│   ├── budgeting/                              # Budget aggregate
│   │   ├── Budget.java                         # Aggregate root
│   │   ├── Transaction.java                    # Abstracts incomes/expenses
│   │   ├── Income.java                         
│   │   ├── Expense.java                        
│   │   ├── Money.java                          
│   │   └── Currency.java                       
│   └── core/                                   # Domain primitives
│       ├── Entity.java
│       ├── ValueObject.java
│       ├── Identifier.java
│       └── DomainException.java
└── infrastructure/                             # Infrastructure layer
    ├── repositories/                           # Data access
    │   ├── BudgetRepository.java
    │   └── FileBudgetRepository.java
    └── services/                               # Technical services
        ├── Storage.java                        # File storage service
        ├── Cache.java                          # Caching service
        └── StorageScanner.java                 # File discovery service
```

---

## Functional Design

### Core Features

#### 1. Budget Management
- **Create Budget**: Users can create named budgets with specific currencies (currently only MXN is supported)
- **View Budgets**: List all budgets with summary information
- **Select Budget**: Work with individual budgets
- **Delete Budget**: Remove budgets and all associated transactions

#### 2. Transaction Management
- **Add Income**: Record income with amount and category
- **Add Expense**: Record expenses with amount and category
- **Delete Transaction**: Remove individual transactions
- **Categorization**: Organize transactions by predefined categories

#### 3. Financial Analysis
- **Budget Balance**: Real-time calculation of total balance
- **Total Income**: Sum of all income transactions
- **Total Expenses**: Sum of all expense transactions

### Domain Model

The domain model follows **Domain-Driven Design** principles with clear aggregates and value objects:

#### Budget Aggregate Root
```
Budget (Entity)
├── Identifier (id)
├── String (name)
├── Currency
└── Transactions (collection)
    ├── Income (Entity)
    │   ├── Identifier
    │   ├── Money (amount)
    │   └── IncomeCategory
    └── Expense (Entity)
        ├── Identifier
        ├── Money (amount)
        └── ExpenseCategory
```

#### Key Domain Concepts

**Entity**: Objects with unique identity that persist over time
- `Budget`: The aggregate root
- `Transaction`, `Income`, `Expense`: Transaction entities

**Value Objects**: Immutable objects defined by their attributes
- `Money`: Amount + Currency (immutable record)
- `Identifier`: UUID-based unique identifier
- `Currency`: Enum of supported currencies

**Domain Invariants**:
- Transaction currency must match budget currency
- Money values use precise decimal arithmetic (8-digit internal scale)
- All entities validate their state through `ensure()` method
- Transactions must be associated with a budget

### User Workflows

#### Creating and Managing a Budget

```
1. User launches application
2. User clicks "New Budget"
3. User enters budget name and selects currency
4. System validates input
5. System creates budget and saves to storage
6. System displays success message
7. User can now add transactions
```

#### Adding Transactions

```
1. User selects a budget
2. User clicks "Add Income" or "Add Expense"
3. User enters amount and selects category
4. System validates transaction
5. System adds transaction to budget
6. System updates balance calculations
7. System persists changes
8. System refreshes view
```

---

## Technical Design

### Technology Stack

- **Language**: Java 21+
- **UI Framework**: Java Swing
- **Build Tool**: Java compiler (javac)
- **Storage**: File-based JSON/text persistence
- **Testing**: JUnit (tests in `src/test/java`)

### Component Details

#### 1. Application Layer

**Program.java** - Application Bootstrap
```java
- Dependency injection container
- Initializes infrastructure services (Cache, Storage, Scanner)
- Creates Repository with injected dependencies
- Instantiates MVC components (Model, View, Controller)
- Sets up Swing Look and Feel
```

**Controller.java** - MVC Controller
```java
- Implements ModelListener and ViewListener interfaces
- Mediates between Model and View
- Handles user actions from View
- Updates View based on Model changes
- Error handling and user feedback
```

**Model.java** - Application Model
```java
- Manages application state
- Wraps domain operations
- Handles repository interactions
- Maintains selected budget/transaction
- Notifies listeners of state changes
```

#### 2. Domain Layer

**Budget.java** - Aggregate Root
```
 - Aggregate root of the bound context for Budgeting
 - Manages the budget information and its transactions
 - Enforces invariants (currency consistency, transaction management)
```

**Transaction.java** - Abstract Base Entity
```
 - An entity that represents a financial transaction that affects the budget
 - Abstract class with common properties (id, amount)
 - Template method pattern for sign() to differentiate incomes and expenses
 - Subclasses: Income, Expense
```

**Money.java** - Value Object (Record)
```
 - A value object to represent monetary amounts with currency
 - Implemented as an immutable record with BigDecimal amount and Currency
 - Currently only supports MXN currency.
```

**Design Decisions**:
- Uses `BigDecimal` for precise monetary calculations
- Immutable value objects prevent accidental state changes
- Entity validation through `ensure()` enforces invariants
- Template method pattern in Transaction hierarchy

#### 3. Infrastructure Layer

**BudgetRepository Interface**
```
 - Defines common repository operations for loading, saving, deleting budgets
 - Abstracts data access layer
 - Provides interface for finding budgets
```

**FileBudgetRepository Implementation**
```
- Uses text file storage for budget data
- Implements caching layer for performance
- Custom serialization/deserialization (INI format)
- Parses budget files using regex patterns
- Rebuilds domain objects from storage
- Handles file I/O exceptions
```

**Storage Services**:
- `Storage`: File read/write operations
- `Cache`: In-memory caching (key-value store)
- `StorageScanner`: Discovers available budget files
- `MemoryCache`: HashMap-based cache implementation
- `FileStorage`: File system adapter

### Data Persistence

**Storage Format**:
```
Budget files stored in user directory with structured text format:
- Budget ID, name, currency
- Transaction entries with ID, type, amount, category
- Parsed using regex patterns
- Rebuilds domain aggregates using BudgetRebuilder
```

**Caching Strategy**:
```
- Read-through cache
- Cache by budget Identifier
- Invalidate on save/delete
- In-memory HashMap storage
```

**Data Flow**:
```
Repository → Cache (check) → Storage (read) → Deserialize → Domain Object
Domain Object → Serialize → Storage (write) → Cache (invalidate)
```

### Design Patterns

1. **Repository Pattern**: Abstracts data access (BudgetRepository)
2. **MVC Pattern**: Separates concerns in UI layer
3. **Observer Pattern**: Model/View listener interfaces
4. **Factory Methods**: Transaction creation (`Budget.credit()`, `Budget.debit()`)
5. **Template Method**: Transaction base class with abstract `sign()`
6. **Strategy Pattern**: Currency formatting with Locale
7. **Dependency Injection**: Constructor injection throughout
8. **Value Object Pattern**: Immutable Money record
9. **Aggregate Pattern**: Budget as aggregate root
10. **Builder Pattern**: BudgetRebuilder for reconstruction from storage

---

## Deployment

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Java compiler (javac)
- Text editor or IDE (VS Code, IntelliJ IDEA, Eclipse)

### Building the Project

```powershell
# Navigate to project directory
cd expenses

# Compile the project
javac -d build/classes -sourcepath src/main/java src/main/java/com/blendwerk/pet/application/Program.java

# Or if using a build tool, run the appropriate command
```

### Running the Application

```powershell
# Run from compiled classes
java -cp build/classes com.blendwerk.pet.application.Program
```

---

## Testing

The project includes unit tests for core domain logic:

**Test Coverage**:
- `MoneyTests.java`: Money value object operations
- `IdentifierTests.java`: Unique identifier generation
- `IncomeTests.java`: Income transaction logic
- `ExpenseTests.java`: Expense transaction logic
- `TransactionTests.java`: Base transaction behavior
- `FileBudgetRepositoryTests.java`: Repository operations
- `FileStorageTests.java`: File I/O operations
- `MemoryCacheTests.java`: Cache functionality

**Running Tests**:
```powershell
# Compile tests
javac -cp "build/classes;lib/*" -d build/classes src/test/java/com/blendwerk/pet/**/*.java

# Run tests (assuming JUnit on classpath)
java -cp "build/classes;lib/*" org.junit.runner.JUnitCore com.blendwerk.pet.domain.MoneyTests
```

---

## Project Structure

```
expenses/
├── src/
│   ├── main/
│   │   ├── java/              # Application source code
│   │   └── resources/         # Images and resources
│   └── test/
│       └── java/              # Unit tests
├── build/
│   └── classes/               # Compiled .class files
├── lib/                       # External dependencies
├── docs/                      # Documentation
│   └── icons.md
└── README.md                  # This file
```

---

## Future Enhancements

- **Reporting**: Generate financial reports and charts
- **Export**: Export budget data to CSV/PDF
- **Database Support**: Alternative persistence using SQLite/PostgreSQL
- **Multi-user**: Support for multiple user profiles
- **Cloud Sync**: Synchronize budgets across devices
- **Budget Goals**: Set and track savings goals
- **Recurring Transactions**: Automatic transaction generation
- **Data Import**: Import from bank statements

---

## License

This project is for personal educational use.

---

## Contact

For questions or feedback about this project, please refer to the source repository.
