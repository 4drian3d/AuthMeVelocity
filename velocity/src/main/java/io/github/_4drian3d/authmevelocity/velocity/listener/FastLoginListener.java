/*
 * Copyright (C) 2023 AuthMeVelocity Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github._4drian3d.authmevelocity.velocity.listener;

import com.github.games647.fastlogin.velocity.event.VelocityFastLoginAutoLoginEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github._4drian3d.authmevelocity.velocity.AuthMeVelocityPlugin;

public class FastLoginListener {
    private final ProxyServer proxy;
    private final AuthMeVelocityPlugin plugin;
    public FastLoginListener(ProxyServer proxy, AuthMeVelocityPlugin plugin){
        this.proxy = proxy;
        this.plugin = plugin;
    }
    @Subscribe
    public void onAutoLogin(VelocityFastLoginAutoLoginEvent event){
        plugin.logDebug("VelocityFastLoginAutoLoginEvent | Attempt to auto register player");
        proxy.getPlayer(event.getProfile().getName()).ifPresent(plugin::addPlayer);
    }
}
