package ps.gov.epsilon.hr.firm.correspondenceList

import grails.transaction.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.hr.enums.v1.EnumCorrespondenceListStatus
import ps.gov.epsilon.hr.enums.v1.EnumDeliveryStatus
import ps.gov.epsilon.hr.enums.v1.EnumFirmSetting
import ps.gov.epsilon.hr.firm.settings.FirmSettingService
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm

import java.time.ZonedDateTime

@Transactional
class CorrespondenceListService {

    def messageSource
    NotificationService notificationService
    FirmSettingService firmSettingService

    public void createReceiveListNotification(CorrespondenceList correspondenceList, ZonedDateTime notificationDate=ZonedDateTime.now()?.minusDays(1)) {
        GrailsParameterMap notificationParams
        Map notificationTermsMap
        Map notificationKeys
        Map notificationValues
        EnumNotificationType notificationType= EnumNotificationType.LIST_MESSAGES

        try {

            String objectSourceReference= correspondenceList?.class?.name
            String controllerName = extractControllerName(objectSourceReference)

            notificationParams = new GrailsParameterMap([:], null)

            //fill notification params and save notification
            notificationParams["objectSourceId"] = correspondenceList?.id
            notificationParams.objectSourceReference = objectSourceReference
            notificationParams.title = "${messageSource.getMessage("${controllerName}" + ".label", [] as Object[], new Locale("ar"))}"
            notificationParams.notificationDate = notificationDate
            notificationParams["notificationType"] = NotificationType.read(notificationType.value)


            notificationTermsMap = [:]
            notificationKeys = [:]
            notificationValues = [:]


            // set ROLE_HR term
            //TODO users who can receive correspondence should be notified
//            notificationKeys.put(1, UserTerm.DEPARTMENT)
//            notificationValues.put(1, EnumApplicationRole.ROLE_HR_DEPARTMENT?.value)

            // set firm term by default
            notificationKeys.put(notificationKeys?.size()+1, UserTerm.FIRM.value())
            notificationValues.put(notificationKeys?.size(), "${correspondenceList?.firm?.id}")

            notificationParams.text = "${messageSource.getMessage("list.notification.received.message", ["${correspondenceList?.id}"] as Object[], new Locale("ar"))}"

            /**
             * Create notification action
             */
            Map notificationActionMap = [:]
            notificationActionMap['action'] = 'manage'+correspondenceList.class.name.substring(correspondenceList.class.name.lastIndexOf('.') + 1)
            notificationActionMap['controller'] = controllerName
            notificationActionMap['label'] = notificationActionMap['controller']+'.manage.label'
            notificationActionMap['icon'] = 'icon-cog'
            notificationActionMap['isModal'] = false
            notificationActionMap['notificationParams'] = []
            notificationActionMap['notificationParams'] << ['name': 'id', 'value': correspondenceList?.id]

            List<Map> notificationActionsList = [notificationActionMap]


            notificationTermsMap.put("key", notificationKeys)
            notificationTermsMap.put("value", notificationValues)
            notificationParams["notificationTerms"] = notificationTermsMap
            notificationParams["notificationActions"] = notificationActionsList

            //save notification
            notificationService?.save(notificationParams)

            if(correspondenceList.deliveryStatus != EnumDeliveryStatus.RESPONSE_DELIVERED){
                correspondenceList.deliveryStatus = EnumDeliveryStatus.RESPONSE_DELIVERED
                correspondenceList.save(failOnError:true)
            }
        } catch (Exception ex) {
            ex.printStackTrace()
            throw new Exception("error create notification for list ", ex)
        }
    }

    /**
     * extracts controller name from domain path
     * @param domainName
     * @return controller name
     */
    public String extractControllerName(String domainName) {
        // Extract controller name from objectSourceReference
        String controllerName = domainName.substring(domainName.lastIndexOf('.') + 1)
        controllerName = "${Character.toLowerCase(controllerName.charAt(0))}" + controllerName.substring(1)
        return controllerName

    }

    /**
     * Decides if a list can be received inside manage list
     * @param correspondenceList
     * @return true or false
     */
    public boolean getCanReceiveList(CorrespondenceList correspondenceList){
        if(correspondenceList?.currentStatus?.correspondenceListStatus == EnumCorrespondenceListStatus.SUBMITTED){
            Boolean isCentralizedWithAOC= firmSettingService.getFirmSettingValue(EnumFirmSetting.CENTRALIZED_WITH_AOC.value, correspondenceList?.firm?.id)?.toBoolean()
            return (!isCentralizedWithAOC || correspondenceList.deliveryStatus== EnumDeliveryStatus.RESPONSE_DELIVERED)
        }
        return false
    }
}
