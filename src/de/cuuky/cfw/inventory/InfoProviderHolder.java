package de.cuuky.cfw.inventory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

abstract class InfoProviderHolder {

    // A little bit messy ik
    private final Map<String, InfoProvider> savedProvider = new HashMap<>();
    private final Map<Info<?>, List<Map.Entry<InfoProvider, Supplier<Integer>>>> provider = new HashMap<>();
    private final Map<Info<?>, Object> cache = new HashMap<>();

    private List<InfoProvider> getActiveProvider() {
        List<InfoProvider> providerList = new LinkedList<>(this.savedProvider.values()), current = this.getTemporaryProvider();
        if (current != null) providerList.addAll(current);
        Collections.reverse(providerList);
        return providerList;
    }

    private void clearPrevious() {
        this.provider.clear();
        this.cache.clear();
    }

    private List<PrioritisedInfo> collectInfos(InfoProvider provider) {
        List<PrioritisedInfo> pInfos = provider.getPrioritisedInfos();
        if (pInfos == null) pInfos = new LinkedList<>();
        else pInfos = new LinkedList<>(pInfos);
        List<Info<?>> infos = provider.getProvidedInfos();
        if (infos != null) infos.stream().map(info -> new PrioritisedInfo(() -> 0, info)).forEach(pInfos::add);
        return pInfos;
    }

    protected List<InfoProvider> getTemporaryProvider() {
        return null;
    }

    protected void updateProvider() {
        this.clearPrevious();
        for (InfoProvider provider : this.getActiveProvider()) {
            for (PrioritisedInfo pInfo : this.collectInfos(provider)) {
                Map.Entry<InfoProvider, Supplier<Integer>>
                        current = new AbstractMap.SimpleEntry<>(provider, pInfo.getPriority());
                synchronized (this.cache) {
                    this.provider.computeIfAbsent(pInfo.getInfo(), (k) -> new LinkedList<>()).add(current);
                }
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
        return this.provider.get(info).stream().max(Comparator.comparingInt(e -> e.getValue().get())).get().getKey();
    }

    public <T> T getInfo(Info<T> type) {
        synchronized (this.cache) {
            return (T) this.cache.computeIfAbsent(type, (typ) -> typ.apply(this.getProvider(typ)));
        }
    }
}