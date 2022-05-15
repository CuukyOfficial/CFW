package de.cuuky.cfw.utils.listener;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityDamageByEntityUtil {

    private final EntityDamageByEntityEvent event;

    private Player damager;

    public EntityDamageByEntityUtil(EntityDamageByEntityEvent event) {
        this.event = event;

        this.registerDamager();
    }

    private void registerDamager() {
        if (event.getDamager() instanceof Arrow) {
            if ((((Arrow) event.getDamager()).getShooter() instanceof Player))
                this.damager = ((Player) ((Arrow) event.getDamager()).getShooter());
        } else if (event.getDamager() instanceof Player)
            this.damager = (Player) event.getDamager();
    }

    /**
     *
     * @return Returns the player who damaged another player regardless the weapon he used
     */
    public Player getDamager() {
        return this.damager;
    }
}