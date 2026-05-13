package model.service;

import java.sql.Connection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

import model.Boat;
import model.BoatType;
import model.Member;

public class SqlOperator {
	private static Connection connection;
	private static final String user = System.getenv("DB_USER");
	private static final String password = System.getenv("DB_PASSWORD");
	private static final String databaseURL = System.getenv("DB_URL");

	public static Connection getConnection() throws SQLException {
		if (connection == null) {
			connection = DriverManager.getConnection(databaseURL, user, password);
		}
		return connection;
	}

	public ArrayList<Member> getMembers() {
		try {
			Connection myConnection = getConnection();
			String query = "SELECT * FROM memberBoats";
			Statement statement = myConnection.createStatement();
			ResultSet results = statement.executeQuery(query);
			ArrayList<Member> members = new ArrayList<Member>();
			while (results.next()) {
				int memberId = results.getInt("id");
				Optional<Member> member = members.stream().filter(m -> m.getId() == memberId).findFirst();
				if (!member.isPresent()) {
					Member memberNew = new Member(results.getString("Name"), results.getString("personNum"),
							results.getInt("id"));
					members.add(memberNew);
					member = Optional.of(memberNew);
				}
				if (results.getInt("boatid") > 0) {
					Boat boat = new Boat(BoatType.valueOf(results.getString("type")), results.getDouble("length"),
							results.getInt("memberId"));
					boat.setId(results.getInt("boatid"));
					member.get().getBoatList().add(boat);
				}
			}
			return members;
		} catch (SQLException i) {
			System.err.println(i.getMessage());
			return null;
		}
	}

	public void saveMember(Member member) {
		if (member == null) {
			return;
		}
		try {
			Connection myConnection = getConnection();
			PreparedStatement statement;

			// BUG FIX 1a: Condition was `> 0` for UPDATE but new members start at id=0,
			// so INSERT was used correctly — however the prepared statement was not created
			// with RETURN_GENERATED_KEYS, and execute() was used instead of executeUpdate().
			// execute() always returns false for INSERT/UPDATE, so the generated key block
			// (which retrieves the new DB id back into the member object) NEVER ran.
			// This left every newly created member with id=0 for the rest of the session,
			// which is why a restart was required to reload the real IDs from the DB.
			//
			// FIX: Use RETURN_GENERATED_KEYS on INSERT, and use executeUpdate() which
			// returns rows-affected (> 0 on success) instead of execute().
			if (member.getId() <= 0) {
				String query = "INSERT INTO member(`personNum`,`Name`) VALUES(?,?)";
				statement = myConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			} else {
				String query = "UPDATE member SET `personNum`= ?, `Name` = ? WHERE id=?";
				statement = myConnection.prepareStatement(query);
			}

			statement.setString(1, member.getPersonNum());
			statement.setString(2, member.getName());
			if (member.getId() > 0) {
				statement.setInt(3, member.getId());
			}

			int rowsAffected = statement.executeUpdate(); // was: boolean sucess = statement.execute();

			// BUG FIX 1b: Condition was `member.getId() < 0` but new members have id=0,
			// so the generated-key read never triggered even if sucess had been true.
			if (rowsAffected > 0 && member.getId() <= 0) {
				ResultSet set = statement.getGeneratedKeys();
				if (set.next()) {
					member.setId(set.getInt(1));
				}
			}

			for (Boat boat : member.getBoatList()) {
				boat.setMember(member);
				saveBoat(boat);
			}
		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
	}

	public void deleteMember(Member member) {
		if (member == null || member.getId() == 0) {
			return;
		}
		try {
			Connection myConnection = getConnection();

			// BUG FIX 3: The original only deleted the member row, leaving all of that
			// member's boats as orphaned rows (or causing a foreign-key constraint
			// violation if one is configured). This is why the workaround required
			// manually removing all boats before deleting the member.
			// FIX: Delete the member's boats first, then delete the member.
			String deleteBoatsQuery = "DELETE FROM boat WHERE memberId = ?";
			PreparedStatement deleteBoatsStmt = myConnection.prepareStatement(deleteBoatsQuery);
			deleteBoatsStmt.setInt(1, member.getId());
			deleteBoatsStmt.execute();

			String query = "DELETE FROM member WHERE id = ?";
			PreparedStatement statement = myConnection.prepareStatement(query);
			statement.setInt(1, member.getId());
			statement.execute();
		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
	}

	public void saveBoat(Boat boat) {
		try {
			Connection myConnection = getConnection();
			PreparedStatement statement;

			// BUG FIX 2: Same root cause as saveMember (Bug 1).
			// execute() always returned false for INSERT, so the boat's DB-assigned id
			// was never written back into the Boat object. The boat stayed at id=-1 for
			// the rest of the session, making any update or delete on it target a
			// non-existent row — requiring a restart to reload real IDs from the DB.
			// FIX: RETURN_GENERATED_KEYS + executeUpdate(), same pattern as saveMember.
			if (boat.getId() <= 0) {
				String query = "INSERT INTO boat(`length`,`type`,`memberId`) VALUES(?,?,?)";
				statement = myConnection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			} else {
				String query = "UPDATE boat SET `length`= ?, `type` = ?, `memberId` = ? WHERE id=?";
				statement = myConnection.prepareStatement(query);
			}

			statement.setDouble(1, boat.getLength());
			statement.setString(2, boat.getType().toString());
			statement.setInt(3, boat.getMemberId());
			if (boat.getId() > 0) {
				statement.setInt(4, boat.getId());
			}

			int rowsAffected = statement.executeUpdate(); // was: boolean sucess = statement.execute();

			if (rowsAffected > 0 && boat.getId() <= 0) {
				ResultSet set = statement.getGeneratedKeys();
				if (set.next()) {
					boat.setId(set.getInt(1));
				}
			}
		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
	}

	public void deleteBoat(Boat boat) {
		try {
			Connection myConnection = getConnection();
			String query = "DELETE FROM boat WHERE id = ?";
			if (boat.getId() == 0) {
				return;
			}
			PreparedStatement statement = myConnection.prepareStatement(query);
			statement.setInt(1, boat.getId());
			statement.execute();
		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
	}

	public static boolean login(String userName, String password) {
		try {
			Connection myConnection = getConnection();
			// Fetch the stored BCrypt hash for the given username
			String query = "SELECT password FROM staff WHERE name = ?";
			PreparedStatement statement = myConnection.prepareStatement(query);
			statement.setString(1, userName);
			ResultSet results = statement.executeQuery();
			if (results.next()) {
				String storedHash = results.getString("password");
				// BCrypt.checkpw handles the salt and comparison securely
				return BCrypt.checkpw(password, storedHash);
			}
		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
		return false;
	}

	public static String hashPassword(String password) {
		// gensalt() default cost factor is 10 - increase for more security
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public double getRatio() {
		try {
			Connection myConnection = getConnection();
			String query = "SELECT SUM(members)/SUM(boats) AS ratio FROM ((SELECT 1 AS 'members', 0 AS 'boats' FROM member) UNION ALL (SELECT 0 AS 'members', 1 AS 'boats' FROM boat )) ratio";

			PreparedStatement statement = myConnection.prepareStatement(query);
			boolean sucess = statement.execute();
			if (sucess) {
				ResultSet set = statement.getResultSet();
				set.next();
				return set.getDouble("ratio");
			}
		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
		return 0;
	}

	public int[] getMaxAndMin() {
		try {
			Connection myConnection = getConnection();
			String query = "SELECT id, SUM(COUNT) AS 'COUNT' FROM ((SELECT member.*, 1 AS 'COUNT' FROM member INNER JOIN boat ON member.id = boat.memberid) UNION ALL (SELECT member.*, 0 AS 'COUNT' FROM member LEFT JOIN boat ON member.id = boat.memberid)) A GROUP BY id";

			PreparedStatement statement = myConnection.prepareStatement(query);
			boolean sucess = statement.execute();
			if (sucess) {
				ResultSet set = statement.getResultSet();
				int max = -1;
				int min = 10000;
				while (set.next()) {
					int count = set.getInt("COUNT");
					if (count > max) max = count;
					if (count < min) min = count;
				}
				return new int[] { max, min };
			}
		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
		return null;
	}

	public boolean ValidatePNum(String pNum) {
		try {
			Connection myConnection = getConnection();
			String query = "SELECT * FROM member WHERE personNum = ?";

			PreparedStatement statement = myConnection.prepareStatement(query);
			statement.setString(1, pNum);

			// BUG FIX 4: getFetchSize() is a fetch-hint, not a row count — it returns 0
			// by default regardless of how many rows the query matched. This made
			// ValidatePNum always return true, so duplicate personal numbers were
			// never detected. FIX: use results.next() to check if any row actually exists.
			ResultSet results = statement.executeQuery();
			return !results.next(); // true = pNum not found = valid to use

		} catch (SQLException i) {
			System.err.println(i.getMessage());
		}
		return false;
	}
}
