import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class BankingApplicationSwing {
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/DATABASE_NAME";
    private static final String DB_USER = "USERNAME(EG: root)";
    private static final String DB_PASSWORD = "DATABASE_PASSWORD";
    
    private Connection connection;
    private JFrame frame;
    private String currentUser;
    private int currentUserId;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new BankingApplicationSwing().initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void initialize() {
        initializeDatabase();
        createLoginScreen();
    }
    
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connection established successfully!");
        } catch (SQLException e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
            showMessage("Database Error", "Cannot connect to database. Please check your connection.", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void createLoginScreen() {
        frame = new JFrame("Advanced Banking Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        
        // Create main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(102, 126, 234), getWidth(), getHeight(), new Color(118, 75, 162));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        // Create login form
        JPanel loginForm = new JPanel();
        loginForm.setLayout(new BoxLayout(loginForm, BoxLayout.Y_AXIS));
        loginForm.setBackground(new Color(255, 255, 255, 230));
        loginForm.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        loginForm.setMaximumSize(new Dimension(350, 400));
        
        // App logo (using text instead of emoji to avoid encoding issues)
        JLabel logo = new JLabel("BANK", SwingConstants.CENTER);
        logo.setFont(new Font("Arial", Font.BOLD, 40));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title
        JLabel title = new JLabel("BANKING PORTAL", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 0, 139));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Username field
        JTextField usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(250, 40));
        usernameField.setMaximumSize(new Dimension(250, 40));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Password field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setMaximumSize(new Dimension(250, 40));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Login button
        JButton loginButton = new JButton("LOGIN");
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setMaximumSize(new Dimension(120, 40));
        loginButton.setBackground(new Color(46, 134, 171));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Register link
        JButton registerButton = new JButton("Don't have an account? Register here");
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setForeground(Color.BLUE);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add components to form
        loginForm.add(logo);
        loginForm.add(Box.createRigidArea(new Dimension(0, 20)));
        loginForm.add(title);
        loginForm.add(Box.createRigidArea(new Dimension(0, 20)));
        loginForm.add(new JLabel("Username:"));
        loginForm.add(Box.createRigidArea(new Dimension(0, 5)));
        loginForm.add(usernameField);
        loginForm.add(Box.createRigidArea(new Dimension(0, 15)));
        loginForm.add(new JLabel("Password:"));
        loginForm.add(Box.createRigidArea(new Dimension(0, 5)));
        loginForm.add(passwordField);
        loginForm.add(Box.createRigidArea(new Dimension(0, 20)));
        loginForm.add(loginButton);
        loginForm.add(Box.createRigidArea(new Dimension(0, 10)));
        loginForm.add(registerButton);
        
        // Add form to main panel
        mainPanel.add(loginForm);
        
        // Login action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (authenticateUser(username, password)) {
                currentUser = username;
                createDashboard();
            } else {
                showMessage("Login Failed", "Invalid username or password", JOptionPane.ERROR_MESSAGE);
                
                // Shake animation
                Timer timer = new Timer(50, new ActionListener() {
                    private int count = 0;
                    private int originalX = loginForm.getX();
                    
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (count < 8) {
                            loginForm.setLocation(originalX + (count % 2 == 0 ? 10 : -10), loginForm.getY());
                            count++;
                        } else {
                            loginForm.setLocation(originalX, loginForm.getY());
                            ((Timer) e.getSource()).stop();
                        }
                    }
                });
                timer.start();
            }
        });
        
        // Register action
        registerButton.addActionListener(e -> createRegistrationScreen());
        
        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
    
    private boolean authenticateUser(String username, String password) {
        try {
            String query = "SELECT user_id, password FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (password.equals(storedPassword)) {
                    currentUserId = rs.getInt("user_id");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }
        return false;
    }
    
    private void createDashboard() {
        frame.getContentPane().removeAll();
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Top navigation bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(46, 134, 171));
        topBar.setPreferredSize(new Dimension(frame.getWidth(), 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser + "!");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        
        String[] buttonLabels = {"Dashboard", "Transfer", "History", "Settings", "Logout"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setForeground(Color.WHITE);
            button.setBackground(new Color(0, 0, 0, 0));
            button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
            button.setFocusPainted(false);
            
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(255, 255, 255, 50));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(0, 0, 0, 0));
                }
            });
            
            if (label.equals("Logout")) {
                button.addActionListener(e -> createLoginScreen());
            }
            
            buttonPanel.add(button);
        }
        
        topBar.add(welcomeLabel, BorderLayout.WEST);
        topBar.add(buttonPanel, BorderLayout.EAST);
        
        // Account summary
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
        accountPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        accountPanel.setBackground(Color.WHITE);
        
        try {
            String query = "SELECT account_number, account_type, balance FROM accounts WHERE user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                JPanel accountCard = createAccountCard(accountNumber, accountType, balance);
                accountPanel.add(accountCard);
                accountPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching account data: " + e.getMessage());
        }
        
        JScrollPane scrollPane = new JScrollPane(accountPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    private JPanel createAccountCard(String accountNumber, String accountType, double balance) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(400, 150));
        card.setBackground(Color.WHITE);
        
        JLabel typeLabel = new JLabel(accountType + " ACCOUNT");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        typeLabel.setForeground(new Color(46, 134, 171));
        
        JLabel numberLabel = new JLabel("Account: " + accountNumber);
        numberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel balanceLabel = new JLabel("Balance: ₹" + String.format("%,.2f", balance));
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton depositButton = new JButton("Deposit");
        styleActionButton(depositButton, new Color(76, 175, 80));
        depositButton.addActionListener(e -> showTransactionDialog("DEPOSIT", accountNumber));
        
        JButton withdrawButton = new JButton("Withdraw");
        styleActionButton(withdrawButton, new Color(244, 67, 54));
        withdrawButton.addActionListener(e -> showTransactionDialog("WITHDRAWAL", accountNumber));
        
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        
        card.add(typeLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(numberLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(balanceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 15)));
        card.add(buttonPanel);
        
        return card;
    }
    
    private void styleActionButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }
    
    private void showTransactionDialog(String type, String accountNumber) {
        JDialog dialog = new JDialog(frame, type + " Transaction", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();
        
        panel.add(amountLabel);
        panel.add(amountField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton executeButton = new JButton("Execute");
        JButton cancelButton = new JButton("Cancel");
        
        executeButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText());
                if (type.equals("DEPOSIT")) {
                    processDeposit(accountNumber, amount);
                } else {
                    processWithdrawal(accountNumber, amount);
                }
                dialog.dispose();
                createDashboard(); // Refresh dashboard
            } catch (NumberFormatException ex) {
                showMessage("Invalid Input", "Please enter a valid amount", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(executeButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void processDeposit(String accountNumber, double amount) {
        try {
            String updateQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, accountNumber);
            updateStmt.executeUpdate();
            
            recordTransaction(accountNumber, "DEPOSIT", amount, "Cash deposit");
            
            showMessage("Success", "Deposit of ₹" + amount + " processed successfully", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            showMessage("Error", "Failed to process deposit: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void processWithdrawal(String accountNumber, double amount) {
        try {
            String checkQuery = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, accountNumber);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (balance < amount) {
                    showMessage("Insufficient Funds", "Your account does not have sufficient funds for this withdrawal", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            String updateQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setDouble(1, amount);
            updateStmt.setString(2, accountNumber);
            updateStmt.executeUpdate();
            
            recordTransaction(accountNumber, "WITHDRAWAL", amount, "Cash withdrawal");
            
            showMessage("Success", "Withdrawal of ₹" + amount + " processed successfully", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            showMessage("Error", "Failed to process withdrawal: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void recordTransaction(String accountNumber, String type, double amount, String description) {
        try {
            String accountQuery = "SELECT account_id FROM accounts WHERE account_number = ?";
            PreparedStatement accountStmt = connection.prepareStatement(accountQuery);
            accountStmt.setString(1, accountNumber);
            ResultSet rs = accountStmt.executeQuery();
            
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                
                String insertQuery = "INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES (?, ?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setInt(1, accountId);
                insertStmt.setString(2, type);
                insertStmt.setDouble(3, amount);
                insertStmt.setString(4, description);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error recording transaction: " + e.getMessage());
        }
    }
    
    private void createRegistrationScreen() {
        frame.getContentPane().removeAll();
        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(102, 126, 234), getWidth(), getHeight(), new Color(118, 75, 162));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        JPanel registrationForm = new JPanel();
        registrationForm.setLayout(new BoxLayout(registrationForm, BoxLayout.Y_AXIS));
        registrationForm.setBackground(new Color(255, 255, 255, 230));
        registrationForm.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        registrationForm.setMaximumSize(new Dimension(400, 600));
        
        JLabel title = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 0, 139));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        String[] fieldLabels = {"First Name:", "Last Name:", "Email:", "Phone:", "Username:", "Password:", "Confirm Password:"};
        JTextField[] fields = new JTextField[fieldLabels.length];
        
        for (int i = 0; i < fieldLabels.length; i++) {
            JLabel label = new JLabel(fieldLabels[i]);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            if (i == 5 || i == 6) {
                fields[i] = new JPasswordField();
            } else {
                fields[i] = new JTextField();
            }
            
            fields[i].setPreferredSize(new Dimension(250, 40));
            fields[i].setMaximumSize(new Dimension(250, 40));
            fields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            fields[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            
            registrationForm.add(label);
            registrationForm.add(Box.createRigidArea(new Dimension(0, 5)));
            registrationForm.add(fields[i]);
            registrationForm.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        
        JLabel accountTypeLabel = new JLabel("Account Type:");
        accountTypeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JComboBox<String> accountTypeCombo = new JComboBox<>(new String[]{"SAVINGS", "CHECKING", "BUSINESS"});
        accountTypeCombo.setPreferredSize(new Dimension(250, 40));
        accountTypeCombo.setMaximumSize(new Dimension(250, 40));
        accountTypeCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        registrationForm.add(accountTypeLabel);
        registrationForm.add(Box.createRigidArea(new Dimension(0, 5)));
        registrationForm.add(accountTypeCombo);
        registrationForm.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton registerButton = new JButton("REGISTER");
        registerButton.setPreferredSize(new Dimension(150, 40));
        registerButton.setMaximumSize(new Dimension(150, 40));
        registerButton.setBackground(new Color(76, 175, 80));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton backButton = new JButton("Back to Login");
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.setMaximumSize(new Dimension(150, 40));
        backButton.setBackground(new Color(244, 67, 54));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        backButton.addActionListener(e -> createLoginScreen());
        
        registerButton.addActionListener(e -> {
            String password = fields[5].getText();
            String confirmPassword = fields[6].getText();
            
            if (password.equals(confirmPassword)) {
                registerUser(
                    fields[0].getText(),
                    fields[1].getText(),
                    fields[2].getText(),
                    fields[3].getText(),
                    fields[4].getText(),
                    password,
                    (String) accountTypeCombo.getSelectedItem()
                );
            } else {
                showMessage("Registration Error", "Passwords do not match", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        registrationForm.add(registerButton);
        registrationForm.add(Box.createRigidArea(new Dimension(0, 10)));
        registrationForm.add(backButton);
        
        mainPanel.add(registrationForm);
        
        frame.setContentPane(mainPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    private void registerUser(String firstName, String lastName, String email, String phone, 
                             String username, String password, String accountType) {
        try {
            String userQuery = "INSERT INTO users (first_name, last_name, email, phone, username, password) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement userStmt = connection.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, firstName);
            userStmt.setString(2, lastName);
            userStmt.setString(3, email);
            userStmt.setString(4, phone);
            userStmt.setString(5, username);
            userStmt.setString(6, password);
            userStmt.executeUpdate();
            
            ResultSet rs = userStmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                
                String accountNumber = generateAccountNumber();
                String accountQuery = "INSERT INTO accounts (user_id, account_number, account_type, balance) VALUES (?, ?, ?, ?)";
                PreparedStatement accountStmt = connection.prepareStatement(accountQuery);
                accountStmt.setInt(1, userId);
                accountStmt.setString(2, accountNumber);
                accountStmt.setString(3, accountType);
                accountStmt.setDouble(4, 0.0);
                accountStmt.executeUpdate();
                
                showMessage("Registration Successful", 
                    "Your account has been created successfully!\nAccount Number: " + accountNumber, 
                    JOptionPane.INFORMATION_MESSAGE);
                createLoginScreen();
            }
        } catch (SQLException e) {
            showMessage("Registration Error", "Failed to create account: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String generateAccountNumber() {
        return String.valueOf((long) (Math.random() * 9_000_000_000L) + 1_000_000_000L);
    }
    
    private void showMessage(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(frame, message, title, messageType);
    }
}