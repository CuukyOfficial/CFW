package de.cuuky.cfw.mysql.request;

public interface PreparedStatementExec extends PreparedStatementHandler {

	public void onStatementExec(boolean exec);
}