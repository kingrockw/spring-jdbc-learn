package cn.rock.entity;
/**
 * 日志类
 */
import java.sql.Date;

public class Blog {


	private long  blogId;
	//作者
	private int userId;
	//标题
	private String title;
	//内容
	private String content;
	//发表时间
	private Date addDate ;

	public long getBlogId() {
		return blogId;
	}

	public void setBlogId(long blogId) {
		this.blogId = blogId;
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getAddDate() {
		return addDate;
	}
	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	@Override
	public String toString() {
		return "Blog{" + "blogId=" + blogId + ", userId=" + userId + ", title='" + title + '\'' + ", content='"
				+ content + '\'' + ", addDate=" + addDate + '}';
	}
}
