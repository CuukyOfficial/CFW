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

package de.cuuky.cfw.version;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class OneThirteenVersionAdapter extends OneTwelveVersionAdapter {

	@Override
	protected void initTablist()
			throws NoSuchFieldException, SecurityException, ClassNotFoundException, NoSuchMethodException {
	}
	
	@Override
	protected void initChat() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
	}

	@Override
	protected void initNetworkManager() throws IllegalArgumentException, IllegalAccessException {
	}
	
	@Override
	public void sendActionbar(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
	}

    @Override
    public void sendClickableMessage(Player player, String message, Action action, String value) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText(message));
        component.setClickEvent(new ClickEvent(action, value));
        player.spigot().sendMessage(ChatMessageType.CHAT, component);
    }
	
	@Override
	public void sendTablist(Player player, String header, String footer) {
		player.setPlayerListHeaderFooter(header, footer);
	}
}
