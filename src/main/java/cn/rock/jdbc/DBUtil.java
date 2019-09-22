/**
 * 与数据库连接的工具类
 */
package cn.rock.jdbc;

import java.sql.*;

public class DBUtil {
	private static String url;
	private static String user;
	private static String password ;

	/**
	 CREATE TABLE `blog` (
	 `blogId` bigint(20) NOT NULL AUTO_INCREMENT,
	 `userId` int(11) NOT NULL,
	 `title` varchar(255) NOT NULL,
	 `content` varchar(1000) NOT NULL,
	 `addDate` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP,
	 PRIMARY KEY (`blogId`)
	 ) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

	 */


	static{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			url="jdbc:mysql://localhost:3306/testdb?characterEncoding=utf-8&serverTimezone=GMT%2B8";
			user = "root";
			password = "rock";
		} catch (Exception e) {
			System.out.println("驱动加载失败");
			e.printStackTrace();
		}
	}
    
    public static Connection getConnection(){
    	Connection connection= null;
    	try {
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println("创建连接失败");
			e.printStackTrace();
		}    	
		return connection;
    }
    
    public static void closeAll(PreparedStatement preparedStatement,ResultSet resultSet,Connection connection) {
		 try {
			 if(preparedStatement!=null){
				 preparedStatement.close();
			 }
			 if(resultSet!=null){
				 resultSet.close();
			 }
			 if(connection!=null&&(!connection.isClosed())){
				 connection.close();
			 }
		 } catch (SQLException e) {
			 System.out.println("关闭连接失败");
			 e.printStackTrace();
		 }
	 }

}

