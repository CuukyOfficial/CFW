package de.cuuky.cfw.mysql.request;

import java.sql.ResultSet;

@Deprecated
public interface PreparedStatementQuery extends PreparedStatementHandler {

	public void onResultRecieve(ResultSet result);

}