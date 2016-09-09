/**
    Copyright (C) 2015 by jabelar

    This file is part of jabelar's Minecraft Forge modding examples; as such,
    you can redistribute it and/or modify it under the terms of the GNU
    General Public License as published by the Free Software Foundation,
    either version 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    For a copy of the GNU General Public License see <http://www.gnu.org/licenses/>.
*/

package com.blogspot.jabelarminecraft.wildanimals.entities.ai.birdofprey;

/**
 * @author jabelar
 *
 */
public class AIStates
{
    // create state constants, did not use enum because need to cast to int anyway for packets
    public final static int STATE_PERCHED = 0;
    public final static int STATE_TAKING_OFF = 1;
    public final static int STATE_SOARING = 2;
    public final static int STATE_DIVING = 3;
    public final static int STATE_LANDING = 4;
    public final static int STATE_TRAVELLING = 5;
    public final static int STATE_ATTACKING = 6;
    public final static int STATE_SOARING_TAMED = 7;
    public final static int STATE_PERCHED_TAMED = 8;
    public static final int STATE_SEEKING = 9;

}
