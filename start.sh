#!/bin/bash

# Get the directory where this script is located
PROJECT="$(cd "$(dirname "$0")" && pwd)"

# Check if jar exists
if [ ! -f "$PROJECT/wsfin_final.jar" ]; then
  echo "Error: wsfin_final.jar not found. Please run ./setup.sh first."
  exit 1
fi

echo "=== Boat Club Application ==="
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

echo "Starting application..."
java -jar $PROJECT/wsfin_final.jar
