package cn.ac.iie.dao;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import com.nis.exception.DaoException;





/**
 * 
 * <p>JDBC 连接管理类</p>
 * <p>使用该类需要具备两个条件：
 * <li>1.数据库驱动jar包</li>
 * <li>2.在src目录下生成db.properties，具有如下内容
 * drivers=* 数据库连接驱动类
 * idb.url=* 数据库连接URL
 * idb.maxconn=* 线程最大连接数
 * idb.user=* 数据库连接用户名
 * idb.password=* 数据库连接密码
 * </li>
 * </p>
 * @author 中科智源育成信息有限公司 E-mail: doufenghu@gmail.com
 * @version 1.0 创建时间：2010-11-8 下午04:54:15
 *
 */
public final class JdbcPzConnectionManager {
	private static final JdbcPzConnectionManager jdbcPzConnectionManager = new JdbcPzConnectionManager() ; // 唯一实例
	private static int clients;
	private Vector drivers = new Vector();
	private Hashtable pools = new Hashtable();
	
	//抑制默认的构造器,避免实例化对象
	private JdbcPzConnectionManager(){
		init();
	}
	
	/**
	 * 读取属性完成初始化
	 */
	private void init() {
		
		//InputStream is = getClass().getResourceAsStream("/db.properties");
		
		Properties dbProps = new Properties();
		try {
			InputStream is = JdbcPzConnectionManager.class.getClassLoader().getResourceAsStream("db_pz.properties");
			//InputStream is = new FileInputStream(System.getProperty("user.dir")+File.separator+"config"+File.separator+"db.properties");
			dbProps.load(is);
		    System.out.println("db_pz.properties读取数据成功！");
		} catch (Exception e) {
			System.err.println("不能读取属性文件. "
				+ "请确保db_pz.properties在CLASSPATH指定的路径中");
			throw new RuntimeException(new FileNotFoundException("unknow db_pz.properties"));
		}
		loadDrivers(dbProps);
		createPools(dbProps);
	}

	/**
	 * 装载和注册所有JDBC驱动程序
	 * 
	 * @param props
	 *            属性
	 */
	private void loadDrivers(Properties props) {
		String driverClasses = props.getProperty("drivers");
		StringTokenizer st = new StringTokenizer(driverClasses);
		while (st.hasMoreElements()) {
			String driverClassName = st.nextToken().trim();
			
			try {
				
				Driver driver = (Driver) Class.forName(driverClassName)
						.newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
			} catch (Exception e) {
				System.out.println("无法装载驱动，异常信息:" + e.getMessage());
			}
		}
	}
	
	/**
	 * 根据指定属性创建连接池实例.
	 * 
	 * @param props
	 *            连接池属性
	 */
	private void createPools(Properties props) {
		Enumeration propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					System.out.println("没有为连接池" + poolName + "指定URL");
					continue;
				}
				String user = props.getProperty(poolName + ".user");
				String password = props.getProperty(poolName + ".password");
				String maxconn = props.getProperty(poolName + ".maxconn", "0");

				int max;
				try {
					max = Integer.valueOf(maxconn).intValue();
				} catch (NumberFormatException e) {
					System.out.println("错误的最大连接数限制: " + maxconn + " .连接池: "
							+ poolName);
					max = 0;
				}
				DBConnectionPool pool = new DBConnectionPool(poolName, url,
						user, password, max);
				pools.put(poolName, pool);

			}
		}
	}


	/**
	 * 返回singleton实例.如果是第一次调用此方法,则创建实例
	 * 
	 * @return JdbcConnectionManager 唯一实例
	 */
	 public  static synchronized JdbcPzConnectionManager getInstance() {
		clients++;
		return jdbcPzConnectionManager;
	}

	

	/**
	 * 将连接对象返回给由名字指定的连接池
	 * 
	 * @param name
	 *            在属性文件中定义的连接池名字
	 * @param conn
	 *            连接对象
	 */
	public void freeConnection(String name, Connection conn) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			pool.freeConnection(conn);
		}
	}

	/**
	 * 获得一个可用的(空闲的)连接.如果没有可用连接,且已有连接数小于最大连接数 限制,则创建并返回新连接
	 * 
	 * @param name
	 *            在属性文件中定义的连接池名字
	 * @return Connection 可用连接或null
	 */
	public Connection getConnection(String name) throws DaoException{
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection();
		}
		return null;
	}

	/**
	 * 获得一个可用连接.若没有可用连接,且已有连接数小于最大连接数限制, 则创建并返回新连接.否则,在指定的时间内等待其它线程释放连接.
	 * 
	 * @param name
	 *            连接池名字
	 * @param time
	 *            以毫秒计的等待时间
	 * @return Connection 可用连接或null
	 */
	public Connection getConnection(String name, long time)throws DaoException {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection(time);
		}
		return null;
	}

	//返回多少个连接
	private int getClient() {
		return clients;
	}

	/**
	 * 关闭所有连接,撤销驱动程序的注册
	 */
	public synchronized void release() {
		// 等待直到最后一个客户程序调用
		if (--clients != 0) {
			return;
		}

		Enumeration allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}

		Enumeration allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = (Driver) allDrivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
	//			System.out.println("撤销JDBC驱动程序 " + driver.getClass().getName()
	//					+ "的注册");
			} catch (SQLException e) {
				System.out.println("无法撤销下列JDBC驱动程序的注册: "
					+ driver.getClass().getName() + "。错误信息："
						+ e.getMessage());
			}
		}
	}

	/**
	 * 
	 * 清空PreparedStatement、Statement、ResultSet对象，未定义的置空。
	 * @param pstmt  PreparedStatement对象
	 * @param stmt  Statement对象
	 * @param rs  ResultSet对象
	 */
	public void clear(PreparedStatement pstmt,Statement stmt,ResultSet rs) {
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	

	/**
	 * 此内部类定义了一个连接池.它能够根据要求创建新连接,直到预定的最大连接数为止.在返回连接给客户程序之前,它能够验证连接的有效性.
	 */
	class DBConnectionPool {
		private int checkedOut;
		private Vector freeConnections = new Vector();
		private int maxConn;
		private String name;
		private String password;
		private String URL;
		private String user;

		/**
		 * 创建新的连接池
		 * 
		 * @param name
		 *            连接池名字
		 * @param URL
		 *            数据库的JDBC URL
		 * @param user
		 *            数据库帐号,或 null
		 * @param password
		 *            密码,或 null
		 * @param maxConn
		 *            此连接池允许建立的最大连接数
		 */
		public DBConnectionPool(String name, String URL, String user,
				String password, int maxConn) {
			this.name = name;
			this.URL = URL;
			this.user = user;
			this.password = password;
			this.maxConn = maxConn;
		}

		/**
		 * 将不再使用的连接返回给连接池
		 * 
		 * @param con
		 *            客户程序释放的连接
		 */
		public synchronized void freeConnection(Connection con) {
			// 将指定连接加入到向量末尾
			freeConnections.addElement(con);
			checkedOut--;
			//release();
			notifyAll();
		}

		/**
		 * 从连接池获得一个可用连接.如没有空闲的连接且当前连接数小于最大连接 数限制,则创建新连接.如原来登记为可用的连接不再有效,则从向量删除之,
		 * 然后递归调用自己以尝试新的可用连接.
		 */
		public synchronized Connection getConnection() throws DaoException{
			Connection con = null;
			if (freeConnections.size() > 0) {
				// 获取向量中第一个可用连接
				con = (Connection) freeConnections.firstElement();
				freeConnections.removeElementAt(0);
				try {
					if (con.isClosed()) {
						// 递归调用自己,尝试再次获取可用连接
						con = getConnection();
					}
				} catch (SQLException e) {
					// 递归调用自己,尝试再次获取可用连接
					con = getConnection();
				}
			} else if (maxConn == 0 || checkedOut < maxConn) {
				con = newConnection();
			}
			if (con != null) {
				checkedOut++;
			}
			return con;
		}

		/**
		 * 从连接池获取可用连接.可以指定客户程序能够等待的最长时间 参见前一个getConnection()方法.
		 * 
		 * @param timeout
		 *            以毫秒计的等待时间限制
		 */
		public synchronized Connection getConnection(long timeout) throws DaoException {
			long startTime = new Date().getTime();
			Connection con;
			while ((con = getConnection()) == null) {
				try {
					wait(timeout);
				} catch (InterruptedException e) {
				}
				if ((new Date().getTime() - startTime) >= timeout) {
					// wait()返回的原因是超时
					return null;
				}
			}
			return con;
		}

		/**
		 * 关闭所有连接
		 */
		public synchronized void release() {
			Enumeration allConnections = freeConnections.elements();
			while (allConnections.hasMoreElements()) {
				Connection con = (Connection) allConnections.nextElement();
				try {
					con.close();
				} catch (SQLException e) {
             		System.out.println("无法关闭连接池" + name + "中的连接，错误信息："
							+ e.getMessage());
				}
			}
			freeConnections.removeAllElements();
		}

		/**
		 * 创建新的连接
		 */
		private Connection newConnection() throws DaoException {
			Connection con = null;
			try {				
				if (user == null) {
					con = DriverManager.getConnection(URL);
				} else {
					con = DriverManager.getConnection(URL, user, password);
				}				
				System.out.println("连接池" + name + "创建一个新的连接");
			} catch (SQLException e) {				
				throw new DaoException("无法创建下列URL的连接:" + URL + "\n错误信息："+ e.getMessage());				
			}
			//freeConnections.addElement(con);
			return con;
		}
	}

}
