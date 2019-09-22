package jdbctest;


import cn.rock.entity.Blog;
import cn.rock.jdbc.BlogDaoImpl;
import org.junit.Test;

public class TestDB {

    @Test
    public void saveBlog() {
        Blog blog = new Blog();
        blog.setContent("我的第二篇blog");
        blog.setTitle("TX");
        blog.setUserId(12);
        BlogDaoImpl blogDao = new BlogDaoImpl();
        blogDao.addBlog(blog);
    }

    @Test
    public void getBlog() {
        BlogDaoImpl blogDao = new BlogDaoImpl();
        Blog blog = blogDao.getBlogById(19);
        System.out.println(blog.getContent());
    }
}
