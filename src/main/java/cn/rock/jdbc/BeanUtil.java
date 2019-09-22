package cn.rock.jdbc;


import cn.rock.entity.Blog;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BeanUtil {
	

	public static Blog toBlog(int blogId, int userId, String title, String content, Date addDate){
		Blog blog = new Blog();
		blog.setBlogId(blogId);
		blog.setUserId(userId);
		blog.setTitle(title);
		blog.setContent(content);
		blog.setAddDate(addDate);
		return blog;
	}


	
	public static Blog toBlog(ResultSet resultSet) throws SQLException {
		Blog blog ;
		if(resultSet.next()){
			blog = new Blog();
			blog.setBlogId(resultSet.getLong(1));
			blog.setUserId(resultSet.getInt(2));
			blog.setTitle(resultSet.getString(3));
			blog.setContent(resultSet.getString(4));
			blog.setAddDate(resultSet.getDate(5));
			return blog;
		}
		return null;
	}
	public static ArrayList<Blog> toBlogList(ResultSet resultSet) throws Exception {
		ArrayList<Blog> blogList = new ArrayList<Blog>();
		while(resultSet.next()){
			int blogId = resultSet.getInt(1);
			int userId = resultSet.getInt(2);
			String title = resultSet.getString(3);
			String content = resultSet.getString(4);
			Date addDate =resultSet.getDate(5);
			blogList.add(toBlog(blogId,userId, title, content, addDate));
		}
		return blogList;
	}
}
