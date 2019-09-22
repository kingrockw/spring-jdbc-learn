package jdbctest;

import cn.rock.entity.Blog;
import cn.rock.jdbc.BeanUtil;
import cn.rock.jdbc.DBUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 隔离级别测试 幻读
 * 先执行t1 再执行t2
 */
public class Isolation3Test {


    @Test
    public void t1() throws Exception {
        //t1 查询更新
        Connection connection = DBUtil.getConnection();
        connection.setAutoCommit(false);
//        读取未提交
//        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//        读取已提交
//        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//        可重复读 mysql默认
//        connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//        串行化, t2 结束之后才能执行
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        String sql = "update blog  set title =  CONCAT('aaa-',title),addDate = now() where blogId > ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 0);
        preparedStatement.executeUpdate();
        String sqlQuery = "SELECT * FROM blog WHERE blogId > ? " ;
        preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, 0);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Blog> blog = BeanUtil.toBlogList(resultSet);
        System.out.println("更新未提交,第一次读取：" + blog);
        Thread.sleep(20 * 1000);
        connection.commit();
        preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, 0);
        resultSet = preparedStatement.executeQuery();
        blog = BeanUtil.toBlogList(resultSet);
        System.out.println("更新已提交，第二次读取：" + blog);
//        先不提交，以免修改数据
//        connection.commit();
        DBUtil.closeAll(preparedStatement, resultSet, connection);
    }

    @Test
    public void t2() {
        //t2 新增
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);
            Blog blog = new Blog();
            blog.setBlogId(18);
            blog.setContent("我的第一篇blog");
            blog.setTitle("456");
            blog.setUserId(12);
            String sql = "insert into blog(userId,title,content,addDate) values(?,?,?,now())";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, blog.getUserId());
            preparedStatement.setString(2, blog.getTitle());
            preparedStatement.setString(3, blog.getContent());
            int b = preparedStatement.executeUpdate();
            System.out.println("新增已执行，未提交");
//            Thread.sleep(20 * 1000);
//            if (true) {
//                throw new NullPointerException("null");
//            }
            connection.commit();
            System.out.println("事务已提交");
        } catch (Exception e) {
            try {
                connection.rollback();
                System.out.println("发生异常回滚事务");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            DBUtil.closeAll(preparedStatement, null, connection);
        }
    }

}
