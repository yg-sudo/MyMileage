/*
 * MyMileage – Your Smart Vehicle Mileage Tracker
 * Copyright (C) 2025  Yojit Ghadi
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

package com.yg.mileage.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.runtime.Composable

object MyMileageShapeDefaults {
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun topListItemShape(): RoundedCornerShape =
        RoundedCornerShape(
            topStart = shapes.large.topStart,
            topEnd = shapes.large.topEnd,
            bottomStart = shapes.extraSmall.bottomStart,
            bottomEnd = shapes.extraSmall.bottomEnd
        )

    @Composable
    fun middleListItemShape(): RoundedCornerShape =
        RoundedCornerShape(shapes.extraSmall.topStart)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun bottomListItemShape(): RoundedCornerShape =
        RoundedCornerShape(
            topStart = shapes.extraSmall.topStart,
            topEnd = shapes.extraSmall.topEnd,
            bottomStart = shapes.large.bottomStart,
            bottomEnd = shapes.large.bottomEnd
        )
    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    val cardShape: CornerBasedShape
        @Composable get() = shapes.largeIncreased
}
