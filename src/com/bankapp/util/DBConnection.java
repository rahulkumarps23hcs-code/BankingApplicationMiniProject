package com.bankapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    
	private static final String URL="jdbc:mysql://localhost:3306/bankdb?useSSL=false&allowPublicKeyRetrieval=true&SereverTimeZone=UTC";
	private static final String USERNAME="root";
	private static final String PASSWORD="pillai";
	
	private DBConnection() {}
	
	public static Connection getConnection() throws SQLException{
		return DriverManager.getConnection(URL,USERNAME,PASSWORD);
	}
	}