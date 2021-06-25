package de.cuuky.cfw.inventory;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class InfoProviderHolder extends DefaultInfoProvider {

    private final Map<InfoProvider, List<Info<?>>> provider = new LinkedHashMap<>();

    protected List<InfoProvider> getInfoProvider() {
        return null;
    }

    private List<InfoProvider> getSortedProvider() {
        List<InfoProvider> providerList = this.getInfoProvider();
        providerList = providerList != null ? new LinkedList<>(providerList) : new LinkedList<>();
        providerList.add(this);
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

    public <T> T getInfo(Info<T> type) {
        for (InfoProvider ip : provider.keySet()) {
            if (!this.provider.get(ip).contains(type))
                continue;

            return type.apply(ip);
        }

        return null;
    }
}