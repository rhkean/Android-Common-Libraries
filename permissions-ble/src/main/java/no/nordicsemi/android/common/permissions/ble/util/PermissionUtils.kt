/*
 * Copyright (c) 2023, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list
 * of conditions and the following disclaimer in the documentation and/or other materials
 * provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be
 * used to endorse or promote products derived from this software without specific prior
 * written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

@file:Suppress("unused")

package no.nordicsemi.android.common.permissions.ble.util

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat

@Suppress("MemberVisibilityCanBePrivate")
internal class PermissionUtils(
    private val context: Context,
    private val dataProvider: LocalDataProvider,
) {
    val isBleEnabled: Boolean
        get() = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
            .adapter
            .isEnabled

    val isLocationEnabled: Boolean
        get() = if (dataProvider.isMarshmallowOrAbove) {
            val lm = context.getSystemService(LocationManager::class.java)
            LocationManagerCompat.isLocationEnabled(lm)
        } else true

    val isBluetoothAvailable: Boolean
        get() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)

    val isLocationPermissionRequired: Boolean
        get() = dataProvider.isMarshmallowOrAbove && !dataProvider.isSOrAbove

    val isBluetoothScanPermissionGranted: Boolean
        get() = !dataProvider.isSOrAbove ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED

    val isBluetoothConnectPermissionGranted: Boolean
        get() = !dataProvider.isSOrAbove ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED

    val isLocationPermissionGranted: Boolean
        get() = !isLocationPermissionRequired ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    val areNecessaryBluetoothPermissionsGranted: Boolean
        get() = isBluetoothScanPermissionGranted && isBluetoothConnectPermissionGranted

    fun markBluetoothPermissionRequested() {
        dataProvider.bluetoothPermissionRequested = true
    }

    fun markLocationPermissionRequested() {
        dataProvider.locationPermissionRequested = true
    }

    fun isBluetoothScanPermissionDeniedForever(context: Context): Boolean {
        return dataProvider.isSOrAbove &&
                !isBluetoothScanPermissionGranted && // Bluetooth Scan permission must be denied
                dataProvider.bluetoothPermissionRequested && // Permission must have been requested before
                !context.findActivity()
                    .shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN)
    }

    fun isLocationPermissionDeniedForever(context: Context): Boolean {
        return dataProvider.isMarshmallowOrAbove &&
                !isLocationPermissionGranted // Location permission must be denied
                && dataProvider.locationPermissionRequested // Permission must have been requested before
                && !context.findActivity()
                    .shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Finds the activity from the given context.
     *
     * https://github.com/google/accompanist/blob/6611ebda55eb2948eca9e1c89c2519e80300855a/permissions/src/main/java/com/google/accompanist/permissions/PermissionsUtil.kt#L99
     *
     * @throws IllegalStateException if no activity was found.
     * @return the activity.
     */
    fun Context.findActivity(): Activity {
        var context = this
        while (context is ContextWrapper) {
            if (context is Activity) return context
            context = context.baseContext
        }
        throw IllegalStateException("no activity")
    }
}
