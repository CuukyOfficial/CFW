package de.cuuky.cfw.inventory;

import java.util.*;

abstract class InfoProviderHolder extends DefaultInfoProvider {

    private final List<InfoProvider> savedProvider = new ArrayList<>();
    private final Map<InfoProvider, List<Info<?>>> provider = new LinkedHashMap<>();

    InfoProviderHolder() {
        this.savedProvider.add(this);
    }

    protected List<InfoProvider> getCurrentProvider() {
        return null;
    }

    private List<InfoProvider> getSortedProvider() {
        List<InfoProvider> providerList = new ArrayList<>(this.savedProvider), current = this.getCurrentProvider();
        if (current != null) providerList.addAll(current);
        providerList.sort((o1, o2) -> Integer.compare(o1.getPriority(), o2.getPriority()) * -1);
        return providerList;
    }

    protected void updateProvider() {
        this.provider.clear();
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
        for (InfoProvider ip : provider.keySet()) {
            if (!this.provider.get(ip).contains(type))
                continue;

            return type.apply(ip);
        }

        return null;
    }
}