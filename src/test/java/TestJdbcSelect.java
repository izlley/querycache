import java.sql.*;
import java.lang.*;
import java.util.List;
import java.util.ArrayList;

public class TestJdbcSelect {

   //static String gQuery = "select * from OCB";
   static String gQuery = "select mbid from ocb_behavior where 1=1"	
        +"and (dt between '20140301' and '20140501' "
        +"  or dt = '99991231') " 
        +" and may = 1 " 
        +" and apay = 1 "
        +" and sex = 1 "
        +" and age between 1 and 10 "
        +" and substr(padd,1,2) = '11' "
        +" and px between 45 - 10 and 45 + 10 "
        +" and py between 13 - 10 and 13 + 10 "
        +" and pcl = '20' "
        +" group by mbid "
        +"  having sum(tsac + tspc) > 0 "
        +" ";

   public static void main(String[] args) throws InterruptedException {
      String sLimit = "";
      int fetchSize = 1024;
      int maxRows = 1024;
      boolean isFetchsize = false;
      boolean isMaxrows = false;
      boolean isSilence = false;
      long startTime = 0;
      long endTime = 0;
      long totalTime = 0;
      int  rowcount = 0;
      List<Long> sProfile = new ArrayList<Long>();
      Connection con = null;

      if (args.length < 2) {
        System.out.println("Usage: -limit <query_str> -fetchsize <fetch_num> -maxrows <maxrow_num>");
        System.exit(1);
      }

      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-limit")) {
          sLimit = " limit " + args[++i];
        } else if (args[i].equals("-fetchsize")) {
          isFetchsize = true;
          fetchSize = Integer.parseInt(args[++i]); 
        } else if (args[i].equals("-maxrows")) {
          isMaxrows = true;
          maxRows = Integer.parseInt(args[++i]); 
        } else if (args[i].equals("-silence")) {
          isSilence = true;
        } else {
          System.out.println("Usage: -limit <query_str> -fetchsize <fetch_num> -maxrows <maxrow_num>");
          System.exit(1);
        }
      }

      try {
         Class.forName("com.skplanet.querycache.jdbc.QCDriver");
         //Class.forName("org.apache.hive.jdbc.HiveDriver");
      } catch(ClassNotFoundException e) {
         System.out.println("Class not found "+ e);
      }

      try {
         //
         // Connect to Querycache server
         //
         startTime = System.currentTimeMillis();
         con = DriverManager.getConnection("jdbc:bdb://dicif004:8655");
         endTime = System.currentTimeMillis();
         sProfile.add(endTime-startTime);

         //
         // Create stmt
         //
         startTime = System.currentTimeMillis();
         Statement stmt = con.createStatement();
         endTime = System.currentTimeMillis();
         sProfile.add(endTime-startTime);

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
         ResultSet rs = stmt.executeQuery(gQuery + sLimit);
         endTime = System.currentTimeMillis();
         sProfile.add(endTime-startTime);

         //
         // getMetadata
         //
         startTime = System.currentTimeMillis();
         ResultSetMetaData rsmd = rs.getMetaData();
         endTime = System.currentTimeMillis();
         sProfile.add(endTime-startTime);

         /* Print col names */
         int sColCnt = rsmd.getColumnCount();
         String sColnames = "";
         if (!isSilence) {
           for (int i = 1; i <= sColCnt; i++) {
              sColnames += rsmd.getColumnName(i); 
              sColnames += "  ";
           }
           System.out.println(sColnames);
         }

         //
         // Fetch rows
         //
         startTime = System.currentTimeMillis();
         while (rs.next()) {
            for (int i = 1; i <= sColCnt; i++) {
               int type = rsmd.getColumnType(i);
               switch (type) {
                 case Types.VARCHAR:
                 case Types.CHAR:
                   if (!isSilence) {System.out.print(rs.getString(i));}
                   else {rs.getString(i);}
                   break;
                 case Types.BIGINT:
                   if (!isSilence) {System.out.print(rs.getLong(i));}
                   else {rs.getLong(i);}
                   break;
                 case Types.INTEGER:
                 case Types.TINYINT:
                   if (!isSilence) {System.out.print(rs.getInt(i));}
                   else {rs.getInt(i);}
                   break;
                 case Types.FLOAT:
                 case Types.DOUBLE:
                   if (!isSilence) {System.out.print(rs.getDouble(i));}
                   else {rs.getDouble(i);}
                   break;
               }
               if (!isSilence) {System.out.print("  ");}
            }
            if (!isSilence) {System.out.println();}
         }
         endTime = System.currentTimeMillis();
         sProfile.add(endTime-startTime);

         //
         // close statement
         //
         stmt.close();

         for (int i = 0; i < sProfile.size(); i++) {
           totalTime += sProfile.get(i); 
           switch (i) {
           case 0:System.out.println("PROFILE: Connection time elapsed : " + sProfile.get(i) + "ms");break;
           case 1:System.out.println("PROFILE: Get Stmt time elapsed : " + sProfile.get(i) + "ms");break;
           case 2:System.out.println("PROFILE: ExecuteQuery time elapsed : " + sProfile.get(i) + "ms");break;
           case 3:System.out.println("PROFILE: GetMeta time elapsed : " + sProfile.get(i) + "ms");break;
           case 4:System.out.println("PROFILE: Fetch time elapsed : " + sProfile.get(i) + "ms");break;
           }
         }
         System.out.println("#### Total time elapsed :" + totalTime + "ms");
      } catch(SQLException e){
         System.out.println("SQL exception occured" + e);
      } finally {
         //
         // close connection
         //
         try {
           if (con != null) {
             con.close();
           }
         } catch (SQLException e) {
           System.out.println("SQL exception occured" + e);
         }
      }
   }
}

