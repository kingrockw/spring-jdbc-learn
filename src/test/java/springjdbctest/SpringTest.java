package springjdbctest;

import cn.rock.Application;
import cn.rock.entity.Blog;
import cn.rock.jdbc.BeanUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
public class SpringTest {

    @Autowired
    DataSource dataSource;

    @Test
    public void testDataSource() throws Exception {

         Connection connection;
         PreparedStatement preparedStatement;
         ResultSet resultSet;
         connection = dataSource.getConnection();
        String sql = "SELECT * FROM blog WHERE blogId = ? " ;
        preparedStatement =connection.prepareStatement(sql);
        preparedStatement.setInt(1, 19);
        resultSet =preparedStatement.executeQuery();
        Blog blog = BeanUtil.toBlog(resultSet);
        //.prepareStatement("");
    }

    @Test
    public void springjdbcTemplate(){
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM blog WHERE blogId = 19 ");

        jdbcTemplate.execute("SELECT * FROM blog WHERE blogId = 19 ");
    }
}
