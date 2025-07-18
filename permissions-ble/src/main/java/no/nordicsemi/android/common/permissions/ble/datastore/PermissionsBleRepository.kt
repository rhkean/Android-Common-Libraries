package no.nordicsemi.android.common.permissions.ble.datastore

import android.content.Context
import androidx.datastore.core.DataStore

class PermissionsBleRepository(
    private val permissionsBleDataStore: DataStore<PermissionsBleData>,
    context: Context
) {
    suspend fun updateLocationPermissionRequested(requested: Boolean) {
        permissionsBleDataStore.updateData { permissionsBleData ->
            permissionsBleData
                .toBuilder()
                .setLocationPermissionRequested(requested)
                .build()
        }
    }

    suspend fun updateBluetoothPermissionsRequested(requested: Boolean) {
        permissionsBleDataStore.updateData { permissionsBleData ->
            permissionsBleData
                .toBuilder()
                .setBluetoothPermissionRequested(requested)
                .build()
        }
    }
}