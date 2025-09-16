# Banking Application (Java Swing)

![Java](https://img.shields.io/badge/Java-8%252B-orange) ![MySQL](https://img.shields.io/badge/MySQL-5.7%252B-blue) ![Java Swing](https://img.shields.io/badge/Java-Swing-lightgrey) ![License](https://img.shields.io/badge/License-MIT-green)

A comprehensive desktop banking application built with Java Swing that provides secure banking operations with an intuitive graphical user interface.

## 📋 Table of Contents
- [Features](#-features)
- [Screenshots](#-screenshots)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Database Setup](#-database-setup)
- [Configuration](#-configuration)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Security Notes](#-security-notes)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)
- [Support](#-support)

## ✨ Features
- **User Authentication**: Secure login system with credential validation  
- **Account Management**: View multiple bank accounts with balances  
- **Transaction Processing**: Deposit and withdrawal operations  
- **User Registration**: New customer account creation  
- **Animated UI**: Gradient backgrounds and interactive elements  
- **Real-time Updates**: Instant balance updates after transactions  
- **Form Validation**: Comprehensive input validation  
- **Database Integration**: MySQL backend for data persistence  

## 📸 Screenshots
- **Login Screen**  
  ![Login Screen](https://via.placeholder.com/400x250/667eea/ffffff?text=Login+Screen)  

- **Dashboard**  
  ![Dashboard](https://via.placeholder.com/400x250/764ba2/ffffff?text=Dashboard)  

- **Registration Form**  
  ![Registration Form](https://via.placeholder.com/400x250/2E86AB/ffffff?text=Registration)  

## 🛠 Prerequisites
Ensure the following are installed before running the application:
- Java Development Kit (JDK) 8 or higher  
- MySQL Server 5.7 or higher  
- MySQL Connector/J (JDBC driver)  
- A Java IDE (Eclipse, IntelliJ IDEA) or command line tools  

## 📥 Installation
1. Clone or download the project files:
    ```bash
    git clone <repository-url>
    cd banking-application
    ```

2. Import the project into your IDE or compile manually using `javac`.  
3. Add MySQL Connector/J to your classpath:  
   Download from: [MySQL Connector/J Download](https://dev.mysql.com/downloads/connector/j/)  
4. Add the JAR file to your project's build path.

## 🗄 Database Setup
1. Start your MySQL server.  
2. Create the database and user in the MySQL client:
    ```sql
    CREATE DATABASE banking_db;
    CREATE USER 'banking_user'@'localhost' IDENTIFIED BY 'securepassword';
    GRANT ALL PRIVILEGES ON banking_db.* TO 'banking_user'@'localhost';
    FLUSH PRIVILEGES;
    ```

3. Create the required tables:
    ```sql
    USE banking_db;

    CREATE TABLE users (
        user_id INT PRIMARY KEY AUTO_INCREMENT,
        username VARCHAR(50) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(50) NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        phone VARCHAR(20),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    CREATE TABLE accounts (
        account_id INT PRIMARY KEY AUTO_INCREMENT,
        user_id INT NOT NULL,
        account_number VARCHAR(20) UNIQUE NOT NULL,
        account_type ENUM('SAVINGS', 'CHECKING', 'BUSINESS') NOT NULL,
        balance DECIMAL(15, 2) DEFAULT 0.00,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    );

    CREATE TABLE transactions (
        transaction_id INT PRIMARY KEY AUTO_INCREMENT,
        account_id INT NOT NULL,
        transaction_type ENUM('DEPOSIT', 'WITHDRAWAL', 'TRANSFER') NOT NULL,
        amount DECIMAL(15, 2) NOT NULL,
        description TEXT,
        transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE
    );
    ```

## ⚙ Configuration
Update the database connection details in the Java code:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_db";
private static final String DB_USER = "banking_user";
private static final String DB_PASSWORD = "securepassword";
```
Replace with your actual database credentials.

## 🚀 Usage
### Compilation
```bash
javac -cp ".;path/to/mysql-connector-java.jar" BankingApplicationSwing.java
```

### Execution
```bash
java -cp ".;path/to/mysql-connector-java.jar" BankingApplicationSwing
```

### Application Flow
1. Launch the application – The login screen appears with a gradient background  
2. Login – Use existing credentials or register a new account  
3. Dashboard – View account summaries and balances  
4. Transactions – Perform deposits and withdrawals  
5. Registration – Create new user accounts with automatic account generation  

## 📁 Project Structure
```
banking-application/
├── BankingApplicationSwing.java    # Main application class
├── lib/
│   └── mysql-connector-java.jar    # MySQL JDBC driver
├── database/
│   └── schema.sql                   # Database schema script
└── README.md                        # This file
```

## 🔒 Security Notes
⚠️ Important Security Considerations:
- **Password Storage**: Currently storing passwords in plain text. Use secure password hashing (bcrypt, Argon2, etc.) in production.  
- **Database Credentials**: Avoid hardcoding credentials; use environment variables or config files.  
- **SQL Injection**: PreparedStatement is used, but always validate user inputs.  
- **Network Security**: Ensure MySQL connections are encrypted (SSL/TLS) in production.  
- **Error Handling**: Avoid revealing sensitive information in error messages.  

## 🐛 Troubleshooting
### Common Issues
- **ClassNotFoundException: com.mysql.jdbc.Driver**  
  → Ensure the MySQL Connector/J JAR is in your classpath.

- **SQLException: Access denied for user**  
  → Verify database credentials and privileges.

- **Communications link failure**  
  → Ensure MySQL server is running and accessible.

- **Unknown database 'banking_db'**  
  → Create the database using the provided schema.sql script.

### Debug Mode
Add print statements to debug database operations.

## 🤝 Contributing
Contributions are welcome!  
Steps:
1. Fork the repository  
2. Create a feature branch  
   ```bash
   git checkout -b feature/improvement
   ```
3. Commit your changes  
   ```bash
   git commit -am 'Add new feature'
   ```
4. Push the branch  
   ```bash
   git push origin feature/improvement
   ```
5. Create a Pull Request  

### Areas for Improvement
- Enhanced password hashing  
- Additional transaction types (transfers, payments)  
- Transaction history view  
- Admin functionality  
- Report generation  
- More UI animations  

## 📄 License
MIT License – see [LICENSE](./LICENSE) file for details.

## 🙋‍♂️ Support
For issues or questions:
- Review the troubleshooting section  
- Ensure prerequisites are installed  
- Verify database connection settings  
- Create an issue in the GitHub repository
