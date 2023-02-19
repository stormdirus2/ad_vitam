/*
 * -------------------------------------------------------------------
 * Ad Vitam
 * Copyright (c) 2023 SciRave
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * -------------------------------------------------------------------
 */

package net.scirave.advitam.helpers;

import ladysnake.requiem.api.v1.remnant.RemnantState;

public interface RemnantComponentInterface {

    RemnantState advitam$getState();

    void advitam$setPreventFracture(boolean prevent);

}
