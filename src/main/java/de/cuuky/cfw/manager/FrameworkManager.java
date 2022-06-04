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

package de.cuuky.cfw.manager;

import org.bukkit.plugin.java.JavaPlugin;

public class FrameworkManager {

    protected JavaPlugin ownerInstance;
    protected String consolePrefix;
    protected FrameworkManagerType type;

    public FrameworkManager(FrameworkManagerType type, JavaPlugin ownerInstance) {
        this.type = type;
        this.consolePrefix = "[" + ownerInstance.getName() + "] [CFW] ";
        this.ownerInstance = ownerInstance;
    }

    public void disable() {
    }

    public JavaPlugin getOwnerInstance() {
        return this.ownerInstance;
    }

    public FrameworkManagerType getType() {
        return this.type;
    }

    public String getConsolePrefix() {
        return consolePrefix;
    }
}
