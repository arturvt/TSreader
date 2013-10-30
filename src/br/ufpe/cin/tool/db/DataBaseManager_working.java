package br.ufpe.cin.tool.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.postgresql.util.PSQLException;

import br.ufpe.cin.tool.extras.Constants;


public class DataBaseManager_working {

	private static Connection con = null;
	private static Statement st = null;
	private static ResultSet rs = null;
	
	public static void startConnection() throws SQLException {

		String url = "jdbc:postgresql://localhost/TwitterDB";
		String user = "postgres";
//		String password = "postgres";
		String password = "261286";
		// Estabelece a conexão
		con = DriverManager.getConnection(url, user, password);
		// Cria a comunicação
		st = con.createStatement();
		// Envio do query
		rs = st.executeQuery("SELECT VERSION()");
		// Se OK, volta uma string com a resposta
		if (rs.next()) {
			System.out.println("Connection established. PostGresql Version:"+rs.getString(1));
		}
	}
	
	public static void closeConnection() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DataBaseManager_working.class.getName());
			lgr.log(Level.WARNING, ex.getMessage(), ex);
		}
	}

	public static void insertEPGvalue(
			String country, String dateStart, String timeStart, String duration, String channel_name, String program_name, String descriptor) throws SQLException {
        PreparedStatement pst = null;

        String stm = Constants.EPG_QUERY;

        pst = con.prepareStatement(stm);
        pst.setString(1, country);
        pst.setString(2, dateStart);
        pst.setString(3, timeStart);
        pst.setString(4, duration);
        pst.setString(5, channel_name);
        pst.setString(6, program_name);
        pst.setString(7, descriptor);
        
        
        
        try {
            pst.executeUpdate();
        } catch(PSQLException e) {
        	System.out.println("-------");
			System.out.println("Could not include current EPG. Msg: "+e.getMessage());
        }
        	
    }
	
	public static void main(String[] args) {
		try {
			startConnection();
//			insertTweet();
			System.out.println("End of program.");
		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(DataBaseManager_working.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException ex) {
				Logger lgr = Logger.getLogger(DataBaseManager_working.class.getName());
				lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}
}
