package de.cuuky.cfw.mysql.request;

import java.sql.PreparedStatement;

@Deprecated
public interface PreparedStatementHandler {

	public void onStatementPrepared(PreparedStatement statement);

}