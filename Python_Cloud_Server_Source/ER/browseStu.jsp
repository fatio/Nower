<%@page import="sun.nio.cs.US_ASCII"%>
<%@ page import="java.util.*,java.io.*,java.sql.*,java.util.regex.*"%>
<%@ page language="java" import="java.util.*"
	contentType="text/html; charset=utf-8"%>
<%
	request.setCharacterEncoding("utf-8");
%>
<%!//用户类
	class User {
		String pid;
		String num;
		String name;
		int age;
		String grade;
		String hobby;
	}

	//获取数据库连接 防止同一连接多线程操作
	public static Connection connect() {
		String connectString = "jdbc:mysql://202.116.76.22:3306/test"
				+ "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(connectString,
					"user", "123456");
			return conn;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	//执行SQL查询语句, 返回结果集
	public static ResultSet executeQuery(Connection conn, String sqlSentence) {
		Statement stat;
		ResultSet rs = null;
		try {
			stat = conn.createStatement(); //获取执行sql语句的对象
			rs = stat.executeQuery(sqlSentence); //执行sql查询，返回结果集
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return rs;
	}

	//把字符串转为数字
	public static int getInt(String str) {
		int res;
		try {
			res = Integer.parseInt(str);
		} catch (Exception e) {
			res = 0;
		}
		return res;
	}

	//查询用户
	public ArrayList<User> queryUser(int pgno, int pgcnt) {
		ArrayList<User> users = new ArrayList<User>();
		Connection conn = connect();
		if (conn != null) {
			pgno = pgno * pgcnt;
			try {
				String sql = "select * from stu limit " + pgno + ", " + pgcnt
						+ ";";
				ResultSet rs = executeQuery(conn, sql);
				while (rs.next()) {
					User user = new User();
					user.pid = rs.getString("id");
					user.num = rs.getString("num");
					user.name = rs.getString("name");
					user.age = getInt(rs.getString("age"));
					user.grade = rs.getString("grade");
					user.hobby = rs.getString("hobby");
					users.add(user);
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return users;
	}%>
<%
	ArrayList<User> users = new ArrayList<User>();
	String method = request.getMethod();
	int next_pgno = 0;
	int last_pgno = 0;
	if (method.equalsIgnoreCase("get")) {
		String s1 = request.getParameter("pgno");
		String s2 = request.getParameter("pgcnt");
		int pgno = s1==null? 0 : getInt(s1);
		int pgcnt = s2==null? 4 : getInt(s2);
		users = queryUser(pgno, pgcnt);
		last_pgno = pgno==0? 0 : pgno-1;//上一页 ps边界
		next_pgno = users.size()==pgcnt? pgno+1 : pgno;//下一页 ps边界
	} else {
		System.out.println("method error");
	}
%>
<!DOCTYPE HTML>
<html>
<head>
<title>浏览学生名单</title>
<style>
table {
	border-collapse: collapse;
	border: none;
	width: 500px;
}

td, th {
	border: solid grey 1px;
	margin: 0 0 0 0;
	padding: 5px 5px 5px 5px
}

.c {
	width: 200px
}

a:link, a:visited {
	color: blue
}

.container {
	margin: 0 auto;
	width: 500px;
	text-align: center;
}
</style>
</head>
<body>
	<div class="container">
		<h1>浏览学生名单</h1>
		<table>
			<tr>
				<th class='c'>学号</th>
				<th class='c'>姓名</th>
				<th class='c'>年龄</th>
				<th class='c'>年级</th>
				<th class='c'>爱好</th>
				<th class='c'>-</th>
			</tr>
			<%
				for (User user : users) {
			%>
			<tr>
				<td><%=user.num %></td>
				<td><%=user.name %></td>
				<td><%=user.age %></td>
				<td><%=user.grade %></td>
				<td><%=user.hobby %></td>
				<td><a href='updateStu.jsp?pid=<%=user.pid%>'>修改</a>&nbsp;<a
					href='deleteStu.jsp?pid=<%=user.pid%>'>删除</a></td>
			</tr>
			<%
				}
			%>

		</table>
		<br> <br>

		<div style="float: left">
			<a href="addStu.jsp">新增</a>
		</div>
		<div style="float: right">
			<a href="browseStu.jsp?pgno=<%=last_pgno %>&pgcnt=4"> 上一页</a> &nbsp; <a
				href="browseStu.jsp?pgno=<%=next_pgno %>&pgcnt=4"> 下一页</a>
		</div>
		<br> <br> <br> <br>
	</div>
</body>
</html>
