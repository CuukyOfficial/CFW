package de.cuuky.cfw.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

import de.cuuky.cfw.mysql.request.PreparedStatementHandler;

public class MySQLClient {

	private Connection connection;
	private String host, database, user, password;

	private Thread asyncRequestHandler;
	private volatile CopyOnWriteArrayList<MySQLRequest> queries;

	public MySQLClient(String host, String database, String user, String password) {
		this.host = host;
		this.database = database;
		this.user = user;
		this.password = password;
		this.queries = new CopyOnWriteArrayList<MySQLRequest>();

		prepareAsyncHandler();
		startConnecting();
		this.asyncRequestHandler.start();
	}

	private void prepareAsyncHandler() {
		this.asyncRequestHandler = new Thread(() -> {
			while (true) {
				if (!this.isConnected()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}

				ArrayList<MySQLRequest> loop = new ArrayList<>(queries);
				Collections.reverse(loop);
				for (MySQLRequest mqr : loop) {
					queries.remove(mqr);
					new Thread(() -> {
						if (!getQuery(mqr))
							queries.add(mqr);
					}).start();
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void startConnecting() {
		new Thread(() -> {
			while (true) {
				if (isConnected()) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}

				try {
					this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database + "?autoReconnect=true", user, password);
				} catch (SQLException e) {
					e.printStackTrace();
					System.err.println("[MySQL] MYSQL USERNAME, IP ODER PASSWORT FALSCH!");
				}
			}
		}).start();
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
			return false;
		}

		return true;
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