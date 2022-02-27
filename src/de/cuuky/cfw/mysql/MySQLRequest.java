package de.cuuky.cfw.mysql;

import java.sql.PreparedStatement;

import de.cuuky.cfw.mysql.request.PreparedStatementExec;
import de.cuuky.cfw.mysql.request.PreparedStatementHandler;
import de.cuuky.cfw.mysql.request.PreparedStatementQuery;

@Deprecated
public class MySQLRequest {

	private String sql;
	private PreparedStatementHandler handler;

	public MySQLRequest(String sql, PreparedStatementHandler handler) {
		this.sql = sql;
		this.handler = handler;
	}

	public void doRequest(PreparedStatement statement) {
		try {
			if (handler == null) {
				statement.execute();
				return;
			}

			if (handler instanceof PreparedStatementExec)
				((PreparedStatementExec) handler).onStatementExec(statement.execute());
			else if (handler instanceof PreparedStatementQuery)
				((PreparedStatementQuery) handler).onResultRecieve(statement.executeQuery());
			else
				statement.execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSql() {
		return sql;
	}

	public PreparedStatementHandler getHandler() {
		return handler;
	}
}