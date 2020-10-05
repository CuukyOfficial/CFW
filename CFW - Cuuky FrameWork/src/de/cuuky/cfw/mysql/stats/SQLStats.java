package de.cuuky.cfw.mysql.stats;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.Set;
import java.util.UUID;

import de.cuuky.cfw.mysql.MySQLClient;
import de.cuuky.cfw.mysql.request.PreparedStatementQuery;

/**
 * 
 * @author Almighty-Satan
 *
 */
public class SQLStats<T> extends MySQLClient {

	private String table;
	private Class<T> statsClazz;
	private Map<String, Field> fields;
	private boolean async;

	public SQLStats(String host, int port, String database, String table, String user, String password, Class<T> statsClazz) throws SQLException {
		super(host, port, database, user, password, new Object());

		this.table = table;
		this.statsClazz = statsClazz;

		this.loadFields();
		this.setupDatabase();
	}

	public SQLStats(String host, int port, String database, String table, String user, String password, Class<T> statsClazz, Object connectWait) throws SQLException {
		super(host, port, database, user, password, connectWait);
	}

	public SQLStats(String host, int port, String database, String table, String user, String password, Class<T> statsClazz, boolean async) throws SQLException {
		super(host, port, database, user, password, async ? null : new Object());
	}

	private void setupDatabase() throws SQLException {
		this.waitForConnection();
		this.createTable();
	}

	private void loadFields() {
		this.fields = new HashMap<String, Field>();

		for (Field field : this.statsClazz.getDeclaredFields()) {
			StatsInt annotation = field.getAnnotation(StatsInt.class);
			if (annotation != null) {
				String key = annotation.value();

				if (this.fields.containsKey(key))
					throw new Error("Duplicate key: " + key);

				field.setAccessible(true);
				this.fields.put(key, field);
			}
		}
	}

	private void createTable() throws SQLException {
		StringBuilder command = new StringBuilder().append("CREATE DATABASE IF NOT EXISTS `").append(this.database).append("`;").append("CREATE TABLE IF NOT EXISTS `").append(this.database).append("`.`").append(this.table).append("` (").append("`index` INT NOT NULL AUTO_INCREMENT,").append("`uuid` VARCHAR(36) NOT NULL,");

		for (String key : this.fields.keySet())
			command.append("`" + key + "` INT NULL,");

		command.append("PRIMARY KEY (`index`), UNIQUE KEY `uuid_key` (`uuid`));");

		this.getQuery(command.toString(), async);
	}

	private String getLoadStatsQuery(UUID uuid) {
		return "SELECT * from `" + this.database + "`.`" + this.table + "` WHERE `uuid` = '" + uuid.toString() + "' FOR UPDATE;";
	}

	private T processStats(ResultSet result) throws SQLException, InstantiationException, IllegalAccessException {
		T stats = (T) this.statsClazz.newInstance();

		if (result.next())
			for (Entry<String, Field> entry : this.fields.entrySet())
				entry.getValue().set(stats, result.getInt(entry.getKey()));

		return stats;
	}

	public T loadStats(UUID uuid) throws SQLException, InstantiationException, IllegalAccessException {
		ResultSet result = this.connection.createStatement().executeQuery(this.getLoadStatsQuery(uuid));
		return processStats(result);
	}

	public boolean loadStatsAsync(UUID uuid, Consumer<T> consumer) throws SQLException, InstantiationException, IllegalAccessException {
		return this.getAsyncPreparedQuery(this.getLoadStatsQuery(uuid), new PreparedStatementQuery() {

			@Override
			public void onStatementPrepared(PreparedStatement statement) {}

			@Override
			public void onResultRecieve(ResultSet result) {
				try {
					consumer.accept(processStats(result));
				} catch (InstantiationException | IllegalAccessException | SQLException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void saveStats(UUID uuid, T stats) throws SQLException, IllegalArgumentException, IllegalAccessException {
		Set<Entry<String, Field>> entrys = this.fields.entrySet();
		int[] values = new int[entrys.size()];

		StringBuilder command = new StringBuilder().append("INSERT INTO `").append(this.database).append("`.`").append(this.table).append("` (uuid");
		entrys.forEach(entry -> command.append(", ").append(entry.getKey()));
		command.append(") VALUES ('").append(uuid.toString()).append("'");

		int i = 0;
		for (Entry<String, Field> entry : entrys)
			command.append(", ").append(values[i++] = (int) entry.getValue().get(stats));

		command.append(") ON DUPLICATE KEY UPDATE ");

		i = 0;
		Iterator<Entry<String, Field>> iterator = entrys.iterator();
		if (iterator.hasNext())
			while (true) {
				Entry<String, Field> entry = iterator.next();
				command.append(entry.getKey()).append("=").append(values[i++]);

				if (iterator.hasNext())
					command.append(", ");
				else
					break;
			}

		this.getQuery(command.toString() + "; COMMIT;", async);
	}

	public void setAsync(boolean async) {
		this.async = async;
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface StatsInt {

		String value();

	}
}