/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.cuuky.cfw.mysql.request.PreparedStatementHandler;

@Deprecated
public class MySQLClient {

	private static final ExecutorService THREAD_POOL;
	private static final long HEARTBEAT_DELAY = 60 * 60 * 1000;

	static {
		THREAD_POOL = Executors.newCachedThreadPool();
	}

	protected Connection connection;
	protected String host, database, user, password;
	protected int port;
	protected Object connectWait;
	protected boolean autoReconnect, keepAlive;

	private volatile CopyOnWriteArrayList<MySQLRequest> queries;

	public MySQLClient(String host, int port, String database, String user, String password) {
		this(host, port, database, user, password, new Object());
	}

	public MySQLClient(String host, int port, String database, String user, String password, Object connectWait) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.autoReconnect = true;
		this.keepAlive = true;
		this.queries = new CopyOnWriteArrayList<MySQLRequest>();
		this.connectWait = connectWait;

		startConnecting();
		startKeepingAlive();
		THREAD_POOL.execute(this::prepareAsyncHandler);
	}

	private void startKeepingAlive() {
		THREAD_POOL.execute(() -> {
			try {
				Thread.sleep(HEARTBEAT_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if (!keepAlive)
				return;

			waitForConnection();
			getQuery(new MySQLRequest("SHOW TABLES;", null));
		});
	}

	private void startConnecting() {
		THREAD_POOL.execute(() -> {
			try {
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?allowMultiQueries=true&autoReconnect=true&testWhileIdle=true&testOnBorrow=true", user, password);

				if (connectWait != null) {
					synchronized (this.connectWait) {
						this.connectWait.notifyAll();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("[MySQL] Couldn't connect to MySQL-Database!");
			}
		});
	}

	private boolean getQuery(MySQLRequest mqr) {
		this.waitForConnection();

		try {
			PreparedStatement statement = connection.prepareStatement(mqr.getSql());
			if (mqr.getHandler() != null)
				mqr.getHandler().onStatementPrepared(statement);
			mqr.doRequest(statement);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[MySQL] An error occured on executing a query!");
			System.err.println("[MySQL] Query: " + mqr.getSql());
			return false;
		}

		return true;
	}

	private Runnable prepareAsyncHandler() {
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.waitForConnection();

			MySQLRequest[] loop = queries.toArray(new MySQLRequest[0]);
			for (int i = loop.length - 1; i >= 0; i--) {
				MySQLRequest mqr = loop[i];
				queries.remove(mqr);
				THREAD_POOL.execute(() -> {
					if (!getQuery(mqr))
						queries.add(mqr);
				});
			}
		}
	}

	protected void waitForConnection() {
		if (isConnected())
			return;

		synchronized (this.connectWait) {
			try {
				this.connectWait.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
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

	public boolean getQuery(String query) {
		return getQuery(new MySQLRequest(query, null));
	}

	public boolean getQuery(String query, PreparedStatementHandler handler) {
		return getQuery(new MySQLRequest(query, handler));
	}

	public boolean getAsyncPreparedQuery(String query) {
		return this.queries.add(new MySQLRequest(query, null));
	}

	public boolean getAsyncPreparedQuery(String query, PreparedStatementHandler dr) {
		return this.queries.add(new MySQLRequest(query, dr));
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isConnected() {
		return this.connection != null;
	}
}