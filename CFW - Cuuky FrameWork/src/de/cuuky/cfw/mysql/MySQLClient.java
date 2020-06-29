package de.cuuky.cfw.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.cuuky.cfw.mysql.request.PreparedStatementHandler;

public class MySQLClient {

	private Connection connection;
	private String host, database, user, password;
	private int port;

	private ExecutorService threadPool;
	private volatile CopyOnWriteArrayList<MySQLRequest> queries;

	public MySQLClient(String host, int port, String database, String user, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.queries = new CopyOnWriteArrayList<MySQLRequest>();
		this.threadPool = Executors.newCachedThreadPool();

		startConnecting();
		this.threadPool.execute(this::prepareAsyncHandler);
	}

	private void startConnecting() {
		threadPool.execute(() -> {
			while (true) {
				if (isConnected()) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}

				try {
					connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("[MySQL] Couldn't connect to MySQL-Database!");
				}
			}
		});
	}

	private boolean getQuery(MySQLRequest mqr) {
		if (!isConnected())
			return false;

		try {
			PreparedStatement statement = connection.prepareStatement(mqr.getSql());
			mqr.getHandler().onStatementPrepared(statement);
			mqr.doRequest(statement);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("[MySQL] Connection to MySQL-Database lost!");
			disconnect();
			return false;
		}

		return true;
	}

	private Runnable prepareAsyncHandler() {
		while (true) {
			try {
				Thread.sleep(isConnected() ? 10 : 100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!isConnected())
				continue;

			MySQLRequest[] loop = queries.toArray(new MySQLRequest[0]);
			for (int i = loop.length - 1; i != 0; i--) {
				MySQLRequest mqr = loop[i];
				queries.remove(mqr);
				threadPool.execute(() -> {
					if (!getQuery(mqr))
						queries.add(mqr);
				});
			}
		}
	}

	public void disconnect() {
		if (!isConnected())
			return;

		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		this.connection = null;
	}

	public boolean getQuery(String query, PreparedStatementHandler handler) {
		return getQuery(new MySQLRequest(query, handler));
	}

	public boolean getAsyncPreparedQuery(String query, PreparedStatementHandler dr) {
		return this.queries.add(new MySQLRequest(query, dr));
	}

	public boolean isConnected() {
		return this.connection != null;
	}
}