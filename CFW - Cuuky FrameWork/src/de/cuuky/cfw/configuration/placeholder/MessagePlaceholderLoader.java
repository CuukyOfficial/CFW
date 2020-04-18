package de.cuuky.cfw.configuration.placeholder;

import de.cuuky.cfw.configuration.placeholder.placeholder.GeneralMessagePlaceholder;
import de.cuuky.cfw.configuration.placeholder.placeholder.PlayerMessagePlaceholder;
import de.cuuky.cfw.configuration.placeholder.placeholder.util.DateInfo;
import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.version.VersionUtils;

public class MessagePlaceholderLoader {

	public MessagePlaceholderLoader() {
		loadMessagePlaceHolder();
		loadPlayerPlaceholder();
	}

	private void loadMessagePlaceHolder() {
		new GeneralMessagePlaceholder("online", 1, "Ersetzt durch die Anzahl aller online Spieler") {

			@Override
			protected String getValue() {
				return String.valueOf(VersionUtils.getOnlinePlayer().size());
			}
		};

		new GeneralMessagePlaceholder("currYear", 1, "Ersetzt durch das Jahr der jetzigen Zeit") {

			@Override
			protected String getValue() {
				return getLastDateRefresh(DateInfo.YEAR);
			}
		};

		new GeneralMessagePlaceholder("currMonth", 1, "Ersetzt durch den Monat der jetzigen Zeit") {

			@Override
			protected String getValue() {
				return getLastDateRefresh(DateInfo.MONTH);
			}
		};

		new GeneralMessagePlaceholder("currDay", 1, "Ersetzt durch den Tag der jetzigen Zeit") {

			@Override
			protected String getValue() {
				return getLastDateRefresh(DateInfo.DAY);
			}
		};

		new GeneralMessagePlaceholder("currHour", 1, "Ersetzt durch die Stunde der jetzigen Zeit") {

			@Override
			protected String getValue() {
				return getLastDateRefresh(DateInfo.HOUR);
			}
		};

		new GeneralMessagePlaceholder("currMin", 1, "Ersetzt durch die Minute der jetzigen Zeit") {

			@Override
			protected String getValue() {
				return getLastDateRefresh(DateInfo.MINUTES);
			}
		};

		new GeneralMessagePlaceholder("currSec", 1, "Ersetzt durch die Sekunden der jetzigen Zeit") {

			@Override
			protected String getValue() {
				return getLastDateRefresh(DateInfo.SECONDS);
			}
		};

		new GeneralMessagePlaceholder("&", -1, true, "Ersetzt durch §") {

			@Override
			protected String getValue() {
				return "§";
			}
		};

		new GeneralMessagePlaceholder("heart", -1, "Ersetzt durch ♥") {

			@Override
			protected String getValue() {
				return "♥";
			}
		};

		new GeneralMessagePlaceholder("nextLine", -1, "Fuegt neue Zeile ein") {

			@Override
			protected String getValue() {
				return "\n";
			}
		};

		new GeneralMessagePlaceholder("null", -1, "Ersetzt durch nichts") {

			@Override
			protected String getValue() {
				return "";
			}
		};
	}

	private void loadPlayerPlaceholder() {
		new PlayerMessagePlaceholder("locale", 1, "Ersetzt durch die Sprache des Spielers") {

			@Override
			protected String getValue(CustomPlayer player) {
				return player.getNetworkManager().getLocale();
			}
		};
	}
}