package com.raif.onlinecashier.services

import com.raif.onlinecashier.Constants
import com.raif.onlinecashier.models.QrObject
import com.raif.onlinecashier.models.QrObjectRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val qrObjectRepository: QrObjectRepository,
) {
    private val logger = LoggerFactory.getLogger("PaymentService")
    @Scheduled(fixedDelay = 1000)
    fun checkUpdates() {
        val activeQrs = qrObjectRepository.findAllByQrStatus("NEW")
        for (qr in activeQrs) {
            val response = khttp.get(
                "${Constants.PAYMENT_API_URL}/qrs/${qr.qrId}"
            )
            logger.debug("{}({}) looking = {}", qr.qrId, qr.id, response)
            val data = try {
                response.jsonObject
            } catch (e: Exception) {
                logger.info("${qr.qrId}(${qr.id}) skipped: $e")
                continue
            }
            if (qr.qrStatus != data["qrStatus"].toString()) {
                statusUpdateHandler(qr, data["qrStatus"].toString())
            }
        }
        qrObjectRepository.flush()
    }

    fun statusUpdateHandler(qrObject: QrObject, newStatus: String) {
        logger.info("${qrObject.qrId}: ${qrObject.qrStatus} -> $newStatus")
        qrObject.qrStatus = newStatus
        qrObjectRepository.save(qrObject)
    }

}
