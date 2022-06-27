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

package de.varoplugin.cfw.inventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

abstract class InfoProviderHolder {

    // A little bit messy ik
    private final Map<String, InfoProvider> savedProvider = new HashMap<>();
    private final Map<Info<?>, List<Map.Entry<InfoProvider, Supplier<Integer>>>> provider = new ConcurrentHashMap<>();
    private final Map<Info<?>, Object> cache = new ConcurrentHashMap<>();

    private List<InfoProvider> getActiveProvider() {
        List<InfoProvider> providerList = new LinkedList<>(this.savedProvider.values()), current = this.getTemporaryProvider();
        if (current != null)
            providerList.addAll(current);
        Collections.reverse(providerList);
        return providerList;
    }

    private void clearPrevious() {
        this.provider.clear();
        this.cache.clear();
    }

    private List<PrioritisedInfo> collectInfos(InfoProvider provider) {
        List<PrioritisedInfo> pInfos = provider.getPrioritisedInfos();
        if (pInfos == null)
            pInfos = new LinkedList<>();
        else
            pInfos = new LinkedList<>(pInfos);
        List<Info<?>> infos = provider.getProvidedInfos();
        if (infos != null)
            infos.stream().map(info -> new PrioritisedInfo(() -> 0, info)).forEach(pInfos::add);
        return pInfos;
    }

    protected List<InfoProvider> getTemporaryProvider() {
        return null;
    }

    protected void updateProvider() {
        this.clearPrevious();
        for (InfoProvider provider : this.getActiveProvider()) {
            for (PrioritisedInfo pInfo : this.collectInfos(provider)) {
                Map.Entry<InfoProvider, Supplier<Integer>> current = new AbstractMap.SimpleEntry<>(provider, pInfo.getPriority());
                this.provider.computeIfAbsent(pInfo.getInfo(), (k) -> new LinkedList<>()).add(current);
            }
        }
    }

    public void addProvider(String id, InfoProvider provider) {
        this.savedProvider.put(id, provider);
    }

    public void removeProvider(String id) {
        this.savedProvider.remove(id);
    }

    private InfoProvider getProvider(Info<?> info) {
        return Objects.requireNonNull(this.provider.get(info).stream().max(
            Comparator.comparingInt(e -> e.getValue().get())).orElse(null)).getKey();
    }

    @SuppressWarnings("unchecked")
    public <T> T getInfo(Info<T> info) {
        T type = (T) this.cache.get(info);
        if (type == null) {
            type = info.apply(this.getProvider(info));
            if (type == null)
                return null;

            this.cache.put(info, type);
        }
        return type;
    }
}
