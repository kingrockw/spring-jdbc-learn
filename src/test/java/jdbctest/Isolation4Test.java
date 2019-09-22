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
 * 幻读错误的理解：说幻读是 事务A 执行两次 select 操作得到不同的数据集，
 *  即 select 1 得到 10 条记录，select 2 得到 11 条记录。
 * 这其实并不是幻读，这是不可重复读的一种，只会在 R-U R-C 级别下出现，而在 mysql 默认的 RR 隔离级别是不会出现的。
 *
 * 幻读，并不是说两次读取获取的结果集不同，幻读侧重的方面是某一次的
 * select 操作得到的结果所表征的数据状态无法支撑后续的业务操作。
 * 更为具体一些：select 某记录是否存在，不存在，准备插入此记录，
 * 但执行 insert 时发现此记录已存在，无法插入，此时就发生了幻读。
 */
public class Isolation4Test {


    @Test
    public void t1() throws Exception {
        //t1 查询更新
        Connection connection = DBUtil.getConnection();
        connection.setAutoCommit(false);
//        读取未提交 幻读
//        connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
//        读取已提交 幻读
//        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
//        可重复读 mysql默认，不会出现幻读
        connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
//        串行化, t2 结束之后才能执行
//        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        String sqlQuery = "SELECT * FROM blog WHERE blogId > ? " ;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, 0);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Blog> blog = BeanUtil.toBlogList(resultSet);
        System.out.println("第一次读取：" + blog);
        Thread.sleep(20 * 1000);
        preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, 0);
        resultSet = preparedStatement.executeQuery();
        blog = BeanUtil.toBlogList(resultSet);
        System.out.println("第二次读取：" + blog);
        connection.commit();
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
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
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
