package no.nordicsemi.android.common.permissions.ble.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object PermissionsBleDataSerializer : Serializer<PermissionsBleData> {
    override val defaultValue: PermissionsBleData = PermissionsBleData.getDefaultInstance()
    override suspend fun readFrom(input: InputStream): PermissionsBleData {
        try {
            return PermissionsBleData.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: PermissionsBleData, output: OutputStream) = t.writeTo(output)
}