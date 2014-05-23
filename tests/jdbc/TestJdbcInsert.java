import java.sql.*;
import java.lang.*;
import java.util.List;
import java.util.ArrayList;

public class TestJdbcInsert {
	static String gQuery = "insert into aggfunc2 select * from aggfunc limit 1000000";
	static String gQuery2 = "select * from aggfunc2 limit 0";

	static enum ConnType {
		IMPALA_JDBC, HIVE_JDBC, PHOENIX_JDBC, MYSQL_JDBC,
	}

	public static void main(String[] args) throws InterruptedException {
		String sUrl = "";
		int fetchSize = 1024;
		int maxRows = 1024;
		boolean isFetchsize = false;
		boolean isMaxrows = false;
		boolean isSilence = false;
		long startTime = 0;
		long endTime = 0;
		long totalTime = 0;
		ConnType sConnType = ConnType.IMPALA_JDBC;
		List<Long> sProfile = new ArrayList<Long>();
		Connection con = null;

		// if (args.length < 2) {
		// doExit();
		// }

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-query")) {
				gQuery = args[++i];
			} else if (args[i].equals("-type")) {
				String type = args[++i];
				if (type.equals("impala")) {
					sConnType = ConnType.IMPALA_JDBC;
				} else if (type.equals("hive")) {
					sConnType = ConnType.HIVE_JDBC;
				} else if (type.equals("phoenix")) {
					sConnType = ConnType.PHOENIX_JDBC;
				} else if (type.equals("mysql")) {
					sConnType = ConnType.MYSQL_JDBC;
				} else {
					doExit();
				}
			} else if (args[i].equals("-fetchsize")) {
				isFetchsize = true;
				fetchSize = Integer.parseInt(args[++i]);
			} else if (args[i].equals("-maxrows")) {
				isMaxrows = true;
				maxRows = Integer.parseInt(args[++i]);
			} else if (args[i].equals("-silence")) {
				isSilence = true;
			} else {
				doExit();
			}
		}

		if (gQuery.isEmpty()) {
			doExit();
		}

		try {
			// Class.forName("com.skplanet.querycache.jdbc.QCDriver");
			Class.forName("org.apache.hive.jdbc.HiveDriver");
		} catch (ClassNotFoundException e) {
			System.out.println("Class not found " + e);
		}

		try {
			//
			// Connect to Querycache server
			//
			// sUrl = "jdbc:hive2://localhost:21050/;auth=noSasl;SYNC_DDL=true";
			sUrl = "jdbc:hive2://localhost:21050/;auth=noSasl";
			startTime = System.currentTimeMillis();
			System.out.println(sUrl);
			con = DriverManager.getConnection(sUrl);
			endTime = System.currentTimeMillis();
			sProfile.add(endTime - startTime);

			//
			// Create stmt
			//
			startTime = System.currentTimeMillis();
			Statement stmt = con.createStatement();
			endTime = System.currentTimeMillis();
			sProfile.add(endTime - startTime);

			if (isMaxrows == true) {
				stmt.setMaxRows(maxRows);
			}
			if (isFetchsize == true) {
				stmt.setFetchSize(fetchSize);
			}

			//
			// Exec query
			//

			startTime = System.currentTimeMillis();
			ResultSet rs = stmt.executeQuery(gQuery2);
			endTime = System.currentTimeMillis();
			sProfile.add(endTime - startTime);

			//
			// Update query
			//
			// startTime = System.currentTimeMillis();
			// System.out.println(gQuery);
			// stmt.executeUpdate(gQuery);
			// endTime = System.currentTimeMillis();
			// sProfile.add(endTime-startTime);

			//
			// Close
			//
			startTime = System.currentTimeMillis();
			stmt.close();
			con.close();
			endTime = System.currentTimeMillis();
			sProfile.add(endTime - startTime);
			con = null;

			for (int i = 0; i < sProfile.size(); i++) {
				totalTime += sProfile.get(i);
				switch (i) {
				case 0:
					System.out.println("PROFILE: Connection time elapsed : "
							+ sProfile.get(i) + "ms");
					break;
				case 1:
					System.out.println("PROFILE: Get Stmt time elapsed : "
							+ sProfile.get(i) + "ms");
					break;
				case 2:
					System.out.println("PROFILE: InsertQuery time elapsed : "
							+ sProfile.get(i) + "ms");
					break;
				case 3:
					System.out.println("PROFILE: Close time elapsed : "
							+ sProfile.get(i) + "ms");
					break;
				}
			}
			System.out.println("#### Total time elapsed :" + totalTime + "ms");
		} catch (SQLException e) {
			System.out.println("SQL exception occured" + e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				System.out.println("SQL exception occured" + e);
			}
		}
	}

	private static void doExit() {
		System.out
				.println("Usage: -type [impala|hive|phoenix] -limit <query_str> -fetchsize <fetch_num> -maxrows <maxrow_num>");
		System.exit(1);
	}
}
