# Boat Club Application

A Java application for managing a boat club's members and boats, built as a learning project for SQL and JavaFX.

## Original project
The original project is available as a zip file in this repository: [Download original project](2dv513-Assigment3-main.zip)

### Changes made from original
- Fixed member and boat ID not being retrieved after INSERT, requiring a restart
- Fixed deleting a member with boats causing errors (now cascades automatically)
- Fixed personal number validation always returning true
- Fixed Statistics button falling through to default case in switch
- Fixed NullPointerException when clicking OK with no member selected
- Grayed out Change Member button when no members exist
- Removed hardcoded database credentials, now uses environment variables
- Fixed SQL injection vulnerabilities in UPDATE queries
- Upgraded password hashing from SHA-1 to BCrypt
- Database and tables are created automatically on first run via setup.sh

---

## Features
- Add, update and delete members
- Add and delete boats per member
- View statistics on member to boat ratio
- Secure staff login with BCrypt password hashing

---

## Requirements
- Linux (Debian/Ubuntu)
- Java 11 or higher
- MariaDB or MySQL

---

## Installation

### Step 1 — Install Java
```bash
sudo apt install default-jdk -y
```

### Step 2 — Install MariaDB
```bash
sudo apt install mariadb-server -y
sudo systemctl start mariadb
sudo systemctl enable mariadb
```

Set the root password:
```bash
sudo mysql
```
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
EXIT;
```

### Step 3 — Clone the project
```bash
git clone https://github.com/yourusername/boat-club-app.git
cd boat-club-app
```

### Step 4 — Run setup (first time only)
```bash
chmod +x setup.sh start.sh
./setup.sh
```

The setup script will:
- Ask for your database username and password
- Create the database and all tables automatically
- Prompt you to create an admin login for the application
- Compile the code and build the jar

---

## Running the application

Every time you want to start the application after the initial setup:
```bash
./start.sh
```

It will ask for your database credentials and launch the app.

---

## Project structure
```
boat-club-app/
├── src/
│   ├── appStart/         # Entry point
│   ├── controllers/      # Application logic
│   ├── model/            # Data models and database access
│   │   └── service/      # SQL operations
│   └── views/            # JavaFX UI scenes
├── setup.sh              # First time setup script
├── start.sh              # Launch script
└── README.md
```
=======
# 2dv513-Assigment3 (old)
To run the project: 
  1. Download the project
  2. Start with coping the data dump into SQL Workbench
  3. Run the jar-file (if it complaing about user name and password when connecting to the database go to the SqlOperator.java and change the password and      user string - recreate the jar-file or run the program from your ide)
  4. If it complains about the log-in for the staff double check so that the staff is created and that you have entered correct user name and password
  5. When running the program (If these instructions are not followed a few bugs will appear): 
     A. Due to a few bugs when creating a new member or boat please restart the program after a newly created member or the newly created boat.
     B. When deleting A member if the member has any boats please remove all boats that the member might have before removing the member.
  
