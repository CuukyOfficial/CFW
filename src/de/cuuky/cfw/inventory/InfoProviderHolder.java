package de.cuuky.cfw.inventory;

import java.util.*;

abstract class InfoProviderHolder {

    private final List<InfoProvider> savedProvider = new ArrayList<>();
    private final Map<InfoProvider, List<Info<?>>> provider = new LinkedHashMap<>();
    private final Map<Info<?>, Object> cache = new HashMap<>();

    protected List<InfoProvider> getTemporaryProvider() {
        return null;
    }

    private List<InfoProvider> getSortedProvider() {
        List<InfoProvider> providerList = new ArrayList<>(this.savedProvider), current = this.getTemporaryProvider();
        if (current != null) providerList.addAll(current);
        providerList.sort((o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority()) * -1);
        return providerList;
    }

    protected void updateProvider() {
        this.provider.clear();
        this.cache.clear();
        for (InfoProvider provider : this.getSortedProvider()) {
            List<Info<?>> infos = provider.getProvidedInfos();
            if (infos != null && !infos.isEmpty())
                this.provider.put(provider, provider.getProvidedInfos());
        }
    }

    protected boolean addProvider(InfoProvider provider) {
        return this.savedProvider.add(provider);
    }

    protected boolean removeProvider(InfoProvider provider) {
        return this.savedProvider.remove(provider);
    }

    public <T> T getInfo(Info<T> type) {
        T info = (T) this.cache.get(type);
        if (info == null) {
            for (InfoProvider ip : provider.keySet()) {
                if (!this.provider.get(ip).contains(type))
                    continue;

                this.cache.put(type, (info = type.apply(ip)));
                break;
            }
        }

        return info;
    }
}