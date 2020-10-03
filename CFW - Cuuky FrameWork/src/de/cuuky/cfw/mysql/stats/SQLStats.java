package de.cuuky.cfw.mysql.stats;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import de.cuuky.cfw.mysql.MySQLClient;

/**
 * 
 * @author Almighty-Satan
 *
 */
public class SQLStats<T> extends MySQLClient {

	private String table;
	private Class<T> statsClazz;
	private Map<String, Field> fields;

	public SQLStats(String host, int port, String database, String table, String user, String password, Class<T> statsClazz) throws SQLException {
		super(host, port, database, user, password, new Object());

		this.table = table;
		this.statsClazz = statsClazz;

		loadFields();

		try {
			synchronized (this.connectWait) {
				this.connectWait.wait();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		createTable();
	}

	public SQLStats(String host, int port, String database, String table, String user, String password, Class<T> statsClazz, Object connectWait) throws SQLException {
		super(host, port, database, user, password, connectWait);
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

		this.getQuery(command.toString());
	}

	public T loadStats(UUID uuid) throws SQLException, InstantiationException, IllegalAccessException {
		ResultSet result = this.connection.createStatement().executeQuery("SELECT * from `" + this.database + "`.`" + this.table + "` WHERE `uuid` = '" + uuid.toString() + "';");
		T stats = (T) this.statsClazz.newInstance();

		if (result.next())
			for (Entry<String, Field> entry : this.fields.entrySet())
				entry.getValue().set(stats, result.getInt(entry.getKey()));

		return stats;
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

		this.getQuery(command.toString());
	}

	@Retention(RetentionPolicy.RUNTIME)
	public @interface StatsInt {

		String value();

	}
}
