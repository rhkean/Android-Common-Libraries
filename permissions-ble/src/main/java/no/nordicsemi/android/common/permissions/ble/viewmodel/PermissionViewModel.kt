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

package no.nordicsemi.android.common.permissions.ble.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import no.nordicsemi.android.common.permissions.ble.BlePermissionNotAvailableReason
import no.nordicsemi.android.common.permissions.ble.bluetooth.BluetoothStateManager
import no.nordicsemi.android.common.permissions.ble.datastore.PermissionsBleRepository
import no.nordicsemi.android.common.permissions.ble.location.LocationStateManager
import no.nordicsemi.android.common.permissions.ble.util.BlePermissionState
import javax.inject.Inject

/**
 * Needed for injecting to @Composable functions.
 */
@HiltViewModel
internal class PermissionViewModel @Inject internal constructor(
    private val bluetoothManager: BluetoothStateManager,
    private val locationManager: LocationStateManager,
    private val permissionsBleRepository: PermissionsBleRepository
) : ViewModel() {
    val bluetoothState = bluetoothManager.bluetoothState()
        .stateIn(
            viewModelScope, SharingStarted.Lazily,
            BlePermissionState.NotAvailable(BlePermissionNotAvailableReason.NOT_AVAILABLE)
        )

    val locationPermission = locationManager.locationState()
        .stateIn(
            viewModelScope, SharingStarted.Lazily,
            BlePermissionState.NotAvailable(BlePermissionNotAvailableReason.NOT_AVAILABLE)
        )

    fun refreshBluetoothPermission() {
        bluetoothManager.refreshPermission()
    }

    fun refreshLocationPermission() {
        locationManager.refreshPermission()
    }

    fun markLocationPermissionRequested() {
        locationManager.markLocationPermissionRequested()
    }

    fun markBluetoothPermissionRequested() {
        bluetoothManager.markBluetoothPermissionRequested()
    }

    fun isBluetoothScanPermissionDeniedForever(context: Context): Boolean {
        return bluetoothManager.isBluetoothScanPermissionDeniedForever(context)
    }

    fun isLocationPermissionDeniedForever(context: Context): Boolean {
        return locationManager.isLocationPermissionDeniedForever(context)
    }
}
