/*
 * Copyright (C) 2024 AuthMeVelocity Contributors
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

package io.github._4drian3d.authmevelocity.velocity.utils;

public record Pair<O>(String string, O object) {
    public boolean isPresent() {
        return this.object != null;
    }

    public boolean isEmpty() {
        return object == null;
    }

    public static <O> Pair<O> of(String string, O object) {
        return new Pair<>(string, object);
    }
}
