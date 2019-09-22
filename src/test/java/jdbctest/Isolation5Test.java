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
 * 幻读，并不是说两次读取获取的结果集不同，幻读侧重的方面是某一次的
 * select 操作得到的结果所表征的数据状态无法支撑后续的业务操作。
 * 更为具体一些：select 某记录是否存在，不存在，准备插入此记录，
 * 但执行 insert 时发现此记录已存在，无法插入，此时就发生了幻读。
 */
public class Isolation5Test {


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

        String sqlQuery = "SELECT * FROM blog WHERE blogId = ? " ;
        //解决办法 查询时添加  FOR UPDATE
//        String sqlQuery = "SELECT * FROM blog WHERE blogId = ? FOR UPDATE" ;
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
        preparedStatement.setInt(1, 20);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Blog> blogList = BeanUtil.toBlogList(resultSet);
        if(blogList.size() == 0){
            //不存在
            System.out.println("不存在ID为20 的blog");
            //执行插入
            Thread.sleep(20 * 1000);
            Blog blog = new Blog();
            blog.setBlogId(18);
            blog.setContent("我的第一篇blog");
            blog.setTitle("456");
            blog.setUserId(12);
            String sql = "insert into blog(blogid,userId,title,content,addDate) values(?,?,?,?,now())";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1,20);
            preparedStatement.setInt(2, blog.getUserId());
            preparedStatement.setString(3, blog.getTitle());
            preparedStatement.setString(4, blog.getContent());
            int b = preparedStatement.executeUpdate();
            connection.commit();
            System.out.println("插入成功：" + b);
        }

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
            String sql = "insert into blog(blogid,userId,title,content,addDate) values(?,?,?,?,now())";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1,20);
            preparedStatement.setInt(2, blog.getUserId());
            preparedStatement.setString(3, blog.getTitle());
            preparedStatement.setString(4, blog.getContent());
            int b = preparedStatement.executeUpdate();
            connection.commit();
            System.out.println("另一新增事务抢先插入 已提交："+b );
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
