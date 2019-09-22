package cn.rock.jdbc;



import cn.rock.entity.Blog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BlogDaoImpl {
	private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

	public int addBlog(Blog blog) {
		int b;
		try {
			connection = DBUtil.getConnection();
			connection.setAutoCommit(false);
			String sql = "insert into blog(userId,title,content,addDate) values(?,?,?,now())";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, blog.getUserId());
			preparedStatement.setString(2, blog.getTitle());
			preparedStatement.setString(3, blog.getContent());
			b = preparedStatement.executeUpdate();
			if (true){
				throw new NullPointerException("null");
			}
			connection.commit();
			return b;
		} catch (Exception e) {
			System.out.println("写日志失败");
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} finally {
			DBUtil.closeAll(preparedStatement, resultSet, connection);
		}
		return 0;
	}


	// 分页 每页显示十条记录
	public ArrayList<Blog> pagingBlog(int userId, int page) {
		ArrayList<Blog> blogList = new ArrayList<Blog>();
		try {
			connection = DBUtil.getConnection();
			// 分页语句
			String sql = "SELECT * FROM blog WHERE userId = ?  order BY addDate desc limit ?,10";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, (page - 1) * 10);
			resultSet = preparedStatement.executeQuery();
			blogList = BeanUtil.toBlogList(resultSet);
			return blogList;
		} catch (Exception e) {
			System.out.println("日志分页失败");
		} finally {
			DBUtil.closeAll(preparedStatement, resultSet, connection);
		}
		return blogList;
	}

	public int sumBlog(int userId) {
		int sumBlog = 0;
		try {
			connection = DBUtil.getConnection();
			// 分页语句
			String sql = "SELECT count(*) FROM blog WHERE userId = ? ";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1, userId);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				sumBlog = resultSet.getInt(1);
			}
		} catch (Exception e) {
			System.out.println("获取日志记录数失败");
		} finally {
			DBUtil.closeAll(preparedStatement, resultSet, connection);
		}
		return sumBlog;
	}



	public Blog getBlogById(int blogId) {
		Blog blog = new Blog();
		try {
			 connection = DBUtil.getConnection();

			 String sql = "SELECT * FROM blog WHERE blogId = ? " ;
			 preparedStatement =connection.prepareStatement(sql);
			 preparedStatement.setInt(1, blogId);
			 resultSet =preparedStatement.executeQuery();
			 blog = BeanUtil.toBlog(resultSet);

	      }catch (Exception e) {
				System.out.println("获取日志内容失败");
			}finally{
				DBUtil.closeAll(preparedStatement, resultSet, connection);
			}
			return blog;
	}
	
	//通过title和content查询记录的blogId，一遍插入数据到fresh表中
	public Blog getBlogByBlog(Blog blog) {
		Blog b = new Blog();
		try {
			 connection = DBUtil.getConnection();
			 String sql = "SELECT * FROM blog WHERE title = ?  and content = ? and userId= ? order by addDate DESC" ;
			 preparedStatement =connection.prepareStatement(sql);
			 preparedStatement.setString(1, blog.getTitle());
			 preparedStatement.setString(2, blog.getContent());
			 preparedStatement.setInt(3, blog.getUserId());
			 resultSet =preparedStatement.executeQuery();
			 b = BeanUtil.toBlog(resultSet);
	      }catch (Exception e) {
				System.out.println("获取日志内容失败");
			}finally{
				DBUtil.closeAll(preparedStatement, resultSet, connection);
			}
			return b;
	}



	public int updateBlog(Blog blog) {
		int b ;
		try {
		    connection = DBUtil.getConnection();
			String sql = "update blog  set title = ?,content = ?,addDate = now() where blogId = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, blog.getTitle());
			preparedStatement.setString(2, blog.getContent());
			preparedStatement.setLong(3, blog.getBlogId());
			b = preparedStatement.executeUpdate();
			return b;
		} catch (Exception e) {
			System.out.println("更新日志失败");
		}finally{
			DBUtil.closeAll(preparedStatement, resultSet, connection);
		}
		return 0;
	}


	public int deleteBlog(int blogId) {
		int b ;
		try {
		    connection = DBUtil.getConnection();
			String sql = "delete from blog  where blogId = ?";
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setInt(1,blogId);
			b = preparedStatement.executeUpdate();
			return b;
		} catch (Exception e) {
			System.out.println("删除日志失败");
		}finally{
			DBUtil.closeAll(preparedStatement, resultSet, connection);
		}
		return 0;
	}
}
