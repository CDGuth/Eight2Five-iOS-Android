package expo.modules.kbeaconpro

import com.kkmcn.kbeaconlib2.KBeaconsMgr
import com.kkmcn.kbeaconlib2.KBScanProcessMgr
import com.kkmcn.kbeaconlib2.KBeacon
import com.kkmcn.kbeaconlib2.KBConnPara
import com.kkmcn.kbeaconlib2.KBConnState
import com.kkmcn.kbeaconlib2.KBConnectionEvent
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgBase
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgCommon
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvIBeacon
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvEddyURL
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvEddyUID
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvEddyTLM
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvKSensor
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvEBeacon
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgTrigger
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgTriggerMotion
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgTriggerAngle
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgSensorHT
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgSensorLight
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgSensorGEO
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgSensorScan
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgSensorPIR
import com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgSensorBase
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketBase
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketIBeacon
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketEddyTLM
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketSensor
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketEddyUID
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketEddyURL
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketSystem
import com.kkmcn.kbeaconlib2.KBAdvPackage.KBAdvPacketEBeacon
import com.kkmcn.kbeaconlib2.KBSensorHistory.KBSensorDataMsg
import expo.modules.kotlin.Promise
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import expo.modules.kotlin.toStrongType
import java.util.ArrayList

class ExpoKBeaconProModule : Module() {
    private var beaconManager: KBeaconsMgr? = null

    private fun normalizedMac(macAddress: String): String {
        return macAddress.uppercase()
    }

    private fun normalizedPassword(password: String?): String {
        if (password.isNullOrEmpty()) {
            return "0000000000000000"
        }

        return password
    }

    override fun definition() = ModuleDefinition {
        Name("ExpoKBeaconPro")

        Events("onBeaconDiscovered", "onConnectionStateChanged", "onNotifyDataReceived")

        OnCreate {
            beaconManager = KBeaconsMgr.sharedBeaconManager(appContext.reactContext)
            beaconManager?.delegate = object : KBScanProcessMgr.KBScanProcessDelegate {
                override fun onBeaconDiscovered(beacons: ArrayList<KBeacon>?) {
                    val beaconData = beacons?.map { beacon ->
                        mapOf(
                            "name" to beacon.name,
                            "mac" to beacon.mac,
                            "rssi" to beacon.rssi,
                            "advPackets" to (beacon.allAdvPackets()?.map { packet -> toMap(packet) } ?: emptyList())
                        )
                    } ?: emptyList()
                    sendEvent("onBeaconDiscovered", mapOf("beacons" to beaconData))
                }

                override fun onCentralBleStateChange(newState: Int) {}
            }
        }

        OnDestroy {
            beaconManager?.stopScanning()
            beaconManager?.clearBeacons()
            beaconManager?.delegate = null
            beaconManager = null
        }

        Function("startScanning") {
            beaconManager?.startScanning()
        }

        Function("stopScanning") {
            beaconManager?.stopScanning()
        }

        Function("clearBeacons") {
            beaconManager?.clearBeacons()
        }

        AsyncFunction("connect") { macAddress: String, password: String?, timeout: Int?, promise: Promise ->
            val beacon = findBeacon(macAddress)
            if (beacon != null) {
                val timeoutMs = timeout ?: 15000
                var isSettled = false

                fun settle(result: Boolean) {
                    if (isSettled) {
                        return
                    }

                    isSettled = true
                    promise.resolve(result)
                }

                beacon.connect(normalizedPassword(password), timeoutMs) { state, reason ->
                    sendEvent("onConnectionStateChanged", mapOf(
                        "macAddress" to macAddress,
                        "state" to state,
                        "reason" to reason
                    ))
                    if (state == KBConnState.Connected) {
                        settle(true)
                    } else if (state == KBConnState.Disconnected) {
                        settle(false)
                    }
                }
            } else {
                promise.resolve(false)
            }
        }

        AsyncFunction("connectEnhanced") { macAddress: String, password: String?, timeout: Int?, connParaMap: Map<String, Any?>?, promise: Promise ->
            val beacon = findBeacon(macAddress)
            if (beacon != null) {
                val connPara = KBConnPara().apply {
                    (connParaMap?.get("syncUtcTime") as? Boolean)?.let { syncUtcTime = it }
                    (connParaMap?.get("readCommPara") as? Boolean)?.let { readCommPara = it }
                    (connParaMap?.get("readSlotPara") as? Boolean)?.let { readSlotPara = it }
                    (connParaMap?.get("readTriggerPara") as? Boolean)?.let { readTriggerPara = it }
                    (connParaMap?.get("readSensorPara") as? Boolean)?.let { readSensorPara = it }
                }
                val timeoutMs = timeout ?: 15000
                var isSettled = false

                fun settle(result: Boolean) {
                    if (isSettled) {
                        return
                    }

                    isSettled = true
                    promise.resolve(result)
                }

                beacon.connectEnhanced(normalizedPassword(password), timeoutMs, connPara) { state, reason ->
                    sendEvent("onConnectionStateChanged", mapOf(
                        "macAddress" to macAddress,
                        "state" to state,
                        "reason" to reason
                    ))
                    if (state == KBConnState.Connected) {
                        settle(true)
                    } else if (state == KBConnState.Disconnected) {
                        settle(false)
                    }
                }
            } else {
                promise.resolve(false)
            }
        }

        AsyncFunction("disconnect") { macAddress: String, promise: Promise ->
            findBeacon(macAddress)?.let {
                it.disconnect()
                promise.resolve(true)
            } ?: promise.resolve(false)
        }

        AsyncFunction("modifyConfig") { macAddress: String, configs: List<Map<String, Any>>, promise: Promise ->
            val beacon = findBeacon(macAddress)
            if (beacon != null) {
                val cfgList = ArrayList(configs.mapNotNull { mapToKBCfg(it) })
                beacon.modifyConfig(cfgList) { success, exception ->
                    if (success) {
                        promise.resolve(true)
                    } else {
                        promise.reject("CONFIG_ERROR", exception?.description ?: "Unknown error", null)
                    }
                }
            } else {
                promise.resolve(false)
            }
        }

        AsyncFunction("readSensorDataInfo") { macAddress: String, sensorType: Int, promise: Promise ->
            findBeacon(macAddress)?.readSensorDataInfo(sensorType) { success, dataInfo, exception ->
                if (success) {
                    promise.resolve(mapOf(
                        "totalRecordNum" to dataInfo?.totalRecordNum,
                        "unreadRecordNum" to dataInfo?.unreadRecordNum,
                        "readIndex" to dataInfo?.readIndex
                    ))
                } else {
                    promise.reject("READ_ERROR", exception?.description ?: "Unknown error", null)
                }
            } ?: promise.reject("BEACON_NOT_FOUND", "Beacon with MAC $macAddress not found", null)
        }

        AsyncFunction("readSensorHistory") { macAddress: String, sensorType: Int, maxNum: Int, readIndex: Int?, promise: Promise ->
            findBeacon(macAddress)?.readSensorHistory(sensorType, maxNum, readIndex ?: 0) { success, data, exception ->
                if (success) {
                    val records = data?.map { record ->
                        mapOf(
                            "utcTime" to record.utcTime,
                            "data" to record.data.toList()
                        )
                    }
                    promise.resolve(records)
                } else {
                    promise.reject("READ_ERROR", exception?.description ?: "Unknown error", null)
                }
            } ?: promise.reject("BEACON_NOT_FOUND", "Beacon with MAC $macAddress not found", null)
        }

        AsyncFunction("clearSensorHistory") { macAddress: String, sensorType: Int, promise: Promise ->
            findBeacon(macAddress)?.clearSensorHistory(sensorType) { success, exception ->
                if (success) {
                    promise.resolve(true)
                } else {
                    promise.reject("CLEAR_ERROR", exception?.description ?: "Unknown error", null)
                }
            } ?: promise.resolve(false)
        }

        AsyncFunction("subscribeSensorDataNotify") { macAddress: String, sensorType: Int, promise: Promise ->
            findBeacon(macAddress)?.subscribeSensorDataNotify(sensorType, { eventType, data ->
                sendEvent("onNotifyDataReceived", mapOf(
                    "macAddress" to macAddress,
                    "eventType" to eventType,
                    "data" to data.toList()
                ))
            }) { success, exception ->
                if (success) {
                    promise.resolve(true)
                } else {
                    promise.reject("SUBSCRIBE_ERROR", exception?.description ?: "Unknown error", null)
                }
            } ?: promise.resolve(false)
        }

        AsyncFunction("unsubscribeSensorDataNotify") { macAddress: String, sensorType: Int, promise: Promise ->
            findBeacon(macAddress)?.unsubscribeSensorDataNotify(sensorType) { success, exception ->
                if (success) {
                    promise.resolve(true)
                } else {
                    promise.reject("UNSUBSCRIBE_ERROR", exception?.description ?: "Unknown error", null)
                }
            } ?: promise.resolve(false)
        }
    }

    private fun findBeacon(macAddress: String): KBeacon? {
        val normalized = normalizedMac(macAddress)
        return beaconManager?.beacons?.find { normalizedMac(it.mac) == normalized }
    }

    private fun toMap(packet: KBAdvPacketBase): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>("advType" to packet.advType)
        when (packet) {
            is KBAdvPacketIBeacon -> {
                map["uuid"] = packet.uuid
                map["majorID"] = packet.majorID
                map["minorID"] = packet.minorID
            }
            is KBAdvPacketEddyTLM -> {
                map["batteryLevel"] = packet.batteryLevel
                map["temperature"] = packet.temperature
                map["advCount"] = packet.advCount
                map["secCount"] = packet.secCount
            }
            is KBAdvPacketSensor -> {
                map["batteryLevel"] = packet.batteryLevel
                map["temperature"] = packet.temperature
                packet.humidity?.let { map["humidity"] = it }
                packet.accSensor?.let { map["accSensor"] = mapOf("xAis" to it.xAis, "yAis" to it.yAis, "zAis" to it.zAis) }
                packet.alarmStatus?.let { map["alarmStatus"] = it }
                packet.pirIndication?.let { map["pirIndication"] = it }
                packet.luxValue?.let { map["luxValue"] = it }
            }
            is KBAdvPacketEddyUID -> {
                map["nid"] = packet.nid
                map["sid"] = packet.sid
            }
            is KBAdvPacketEddyURL -> {
                map["url"] = packet.url
            }
            is KBAdvPacketSystem -> {
                map["macAddress"] = packet.macAddress
                map["model"] = packet.model
                map["batteryPercent"] = packet.batteryPercent
                map["version"] = packet.version
            }
            is KBAdvPacketEBeacon -> {
                map["mac"] = packet.mac
                map["uuid"] = packet.uuid
                map["utcSecCount"] = packet.utcSecCount
                map["refTxPower"] = packet.refTxPower
            }
        }
        return map
    }

    private fun mapToKBCfg(map: Map<String, Any>): KBCfgBase? {
        val advType = map["advType"] as? Int
        return when (advType) {
            0 -> KBCfgAdvIBeacon().apply {
                (map["uuid"] as? String)?.let { uuid = it }
                (map["majorID"] as? Number)?.let { majorID = it.toInt() }
                (map["minorID"] as? Number)?.let { minorID = it.toInt() }
            }
            // Eddystone UID (AdvType 2)
            2 -> KBCfgAdvEddyUID().apply {
                (map["nid"] as? String)?.let { nid = it }
                (map["sid"] as? String)?.let { sid = it }
            }
            // Add other config types here
            else -> null
        }?.apply {
            if (this is com.kkmcn.kbeaconlib2.KBCfgPackage.KBCfgAdvBase) {
                (map["slotIndex"] as? Number)?.let { slotIndex = it.toInt() }
                (map["txPower"] as? Number)?.let { txPower = it.toInt() }
                (map["advPeriod"] as? Number)?.let { advPeriod = it.toFloat() }
                (map["advMode"] as? Number)?.let { advMode = it.toInt() }
                (map["advTriggerOnly"] as? Boolean)?.let { advTriggerOnly = it }
                (map["advConnectable"] as? Boolean)?.let { advConnectable = it }
            }
        }
    }
}
