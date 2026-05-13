#!/bin/bash

# Get the directory where this script is located
PROJECT="$(cd "$(dirname "$0")" && pwd)"

LIBS="/usr/share/java/mariadb-java-client.jar:/usr/share/java/javafx-controls.jar:/usr/share/java/javafx-graphics.jar:/usr/share/java/javafx-base.jar:/usr/share/java/javafx-fxml.jar:/usr/share/java/javafx-media.jar:/usr/share/java/jbcrypt-0.4.jar"

echo "=== Boat Club Application Setup ==="
echo

# Prompt user for database credentials
read -p "Database username: " DB_USER
read -sp "Database password: " DB_PASSWORD
echo
read -p "Database host (press Enter for default localhost): " DB_HOST
if [ -z "$DB_HOST" ]; then
  DB_HOST="localhost"
fi

export DB_USER
export DB_PASSWORD
export DB_URL="jdbc:mysql://$DB_HOST:3306/BoatClub?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"

# Write temp config file so password is never visible in process list
MYSQL_CONF=$(mktemp)
chmod 600 $MYSQL_CONF
cat > $MYSQL_CONF << CONF
[client]
user=$DB_USER
password=$DB_PASSWORD
host=$DB_HOST
CONF

# Create database and tables if they don't exist
echo "Setting up database..."
mysql --defaults-file=$MYSQL_CONF << SQL
CREATE DATABASE IF NOT EXISTS BoatClub;
USE BoatClub;

CREATE TABLE IF NOT EXISTS member (
  id INT NOT NULL AUTO_INCREMENT,
  personNum VARCHAR(12) NOT NULL,
  Name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS boat (
  id INT NOT NULL AUTO_INCREMENT,
  length DOUBLE NOT NULL,
  type VARCHAR(50) NOT NULL,
  memberId INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (memberId) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS staff (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  displayname VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE OR REPLACE VIEW memberBoats AS
  SELECT member.id, member.Name, member.personNum, boat.id AS boatid, boat.length, boat.type, boat.memberId
  FROM member
  LEFT JOIN boat ON member.id = boat.memberId;
SQL

if [ $? -ne 0 ]; then
  echo "Error: Could not connect to database. Check your username and password."
  rm -f $MYSQL_CONF
  exit 1
fi

echo "Database setup complete!"

echo
echo "Compiling..."
mkdir -p $PROJECT/out
javac -cp "$LIBS" -d $PROJECT/out $(find $PROJECT/src -name "*.java")

echo "Building fat jar..."
mkdir -p $PROJECT/fatjar
cd $PROJECT/fatjar
jar xf /usr/share/java/mariadb-java-client.jar
jar xf /usr/share/java/javafx-controls.jar
jar xf /usr/share/java/javafx-graphics.jar
jar xf /usr/share/java/javafx-base.jar
jar xf /usr/share/java/javafx-fxml.jar
jar xf /usr/share/java/javafx-media.jar
jar xf /usr/share/java/jbcrypt-0.4.jar
cp -r $PROJECT/out/* .
jar cvfe $PROJECT/wsfin_final.jar appStart.Appstart .

# Check if any staff users exist, if not prompt to create one
STAFF_COUNT=$(mysql --defaults-file=$MYSQL_CONF -se "SELECT COUNT(*) FROM BoatClub.staff;" 2>/dev/null)

if [ "$STAFF_COUNT" = "0" ]; then
  echo
  echo "No admin user found. Please create one:"
  read -p "New admin username: " ADMIN_USER
  read -sp "New admin password: " ADMIN_PASSWORD
  echo
  read -p "Display name: " ADMIN_DISPLAY

  # Use the jar itself to hash the password with BCrypt
  BCRYPT_HASH=$(java -cp "$PROJECT/wsfin_final.jar:/usr/share/java/jbcrypt-0.4.jar" org.mindrot.jbcrypt.BCrypt 2>/dev/null || \
    java -cp "/usr/share/java/jbcrypt-0.4.jar" -e "System.out.println(org.mindrot.jbcrypt.BCrypt.hashpw(\"$ADMIN_PASSWORD\", org.mindrot.jbcrypt.BCrypt.gensalt()));" 2>/dev/null)

  # Simpler: use a small inline Java program to hash
  BCRYPT_HASH=$(java -cp "/usr/share/java/jbcrypt-0.4.jar" org.mindrot.jbcrypt.BCrypt "$ADMIN_PASSWORD" 2>/dev/null)

  # Most reliable: write a tiny helper class
  cat > /tmp/HashPassword.java << JAVA
import org.mindrot.jbcrypt.BCrypt;
public class HashPassword {
  public static void main(String[] args) {
    System.out.println(BCrypt.hashpw(args[0], BCrypt.gensalt()));
  }
}
JAVA
  javac -cp "/usr/share/java/jbcrypt-0.4.jar" /tmp/HashPassword.java -d /tmp/
  BCRYPT_HASH=$(java -cp "/usr/share/java/jbcrypt-0.4.jar:/tmp" HashPassword "$ADMIN_PASSWORD")

  mysql --defaults-file=$MYSQL_CONF -e "INSERT INTO BoatClub.staff (name, password, displayname) VALUES ('$ADMIN_USER', '$BCRYPT_HASH', '$ADMIN_DISPLAY');"
  echo "Admin user '$ADMIN_USER' created successfully!"
fi

# Delete temp config file
rm -f $MYSQL_CONF

echo
echo "Setup complete! Run ./start.sh to launch the application."
