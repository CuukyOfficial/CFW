package de.cuuky.cfw.mysql.request;

@Deprecated
public interface PreparedStatementExec extends PreparedStatementHandler {

	public void onStatementExec(boolean exec);
}