package jdbctest;

import cn.rock.entity.Blog;
import cn.rock.jdbc.BeanUtil;
import cn.rock.jdbc.DBUtil;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 隔离级别测试 不可重复读
 * 先执行t1 再执行t2
 */
public class Isolation2Test {


    @Test
    @Transactional
    public void t1() throws SQLException, InterruptedException {
        //t1 读取
        Connection connection = DBUtil.getConnection();
//        connection.setReadOnly();
        connection.setAutoCommit(false);
//        读取未提交
//        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//        读取已提交
//        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//        可重复读 mysql默认
        connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//        串行化, t2 结束之后才能执行
//        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        String sql = "SELECT * FROM blog WHERE blogId = ? ";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 19);
        ResultSet resultSet = preparedStatement.executeQuery();
        Blog blog = BeanUtil.toBlog(resultSet);
        System.out.println("第一次读取：" + blog);
        Thread.sleep(20 * 1000);
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, 19);
        resultSet = preparedStatement.executeQuery();
        blog = BeanUtil.toBlog(resultSet);
        System.out.println("第二次读取：" + blog);
        connection.commit();
        DBUtil.closeAll(preparedStatement, resultSet, connection);
    }

    @Test
    public void t2() {
        //t2 修改
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DBUtil.getConnection();
            connection.setAutoCommit(false);
            Blog blog = new Blog();
            blog.setBlogId(19);
            blog.setContent("我的第一篇blog");
            blog.setTitle("VVVV");
            blog.setUserId(12);
            String sql = "update blog  set title = ?,content = ?,addDate = now() where blogId = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, blog.getTitle());
            preparedStatement.setString(2, blog.getContent());
            preparedStatement.setLong(3, blog.getBlogId());
            int b = preparedStatement.executeUpdate();
            System.out.println("更新已执行，未提交");
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
