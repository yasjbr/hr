package ps.gov.epsilon.aoc.correspondences

import grails.transaction.Transactional
import grails.util.Environment
import grails.util.Holders
import grails.web.servlet.mvc.GrailsParameterMap
import ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus
import ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.police.common.beans.v1.SearchBean
import ps.police.common.beans.v1.SearchConditionCriteriaBean
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.NotificationService
import ps.police.notifications.NotificationType
import ps.police.notifications.enums.UserTerm
import ps.police.security.UserService
import ps.police.security.dtos.v1.UserDTO

import java.time.ZonedDateTime

@Transactional
class AocCorrespondenceNotificationService {

    NotificationService notificationService
    UserService userService
    def messageSource

    /**
     * Used by workflow plugin to notify persons who are responsible to take action for a workflow step
     *
     * @param jobTitleId
     * @param departmentId
     * @param objectSourceId : id of the request that needs action
     * @param objectSourceReference : domain of the request that needs action
     * @param notificationText
     * @param notificationTitle
     * @param notificationDate
     */
    void notifyWorkflowAction(String jobTitleId, String departmentId, String objectSourceId, String objectSourceReference,
                              String notificationText, String notificationTitle, ZonedDateTime notificationDate) {

        println("jobTitleId=$jobTitleId, departmentId:$departmentId, objectSourceId:$objectSourceId, objectSourceReference:$objectSourceReference")

        // Extract controller name from objectSourceReference
        String controllerName = "aocCorrespondenceList"

        /**
         * Create notification action
         */
        Map notificationActionMap = [:]
        notificationActionMap['action'] = 'manageListWorkflow'
        notificationActionMap['controller'] = controllerName
        notificationActionMap['label'] = 'aocCorrespondenceList.manageWorkflow.label'
        notificationActionMap['icon'] = 'icon-cog'
        notificationActionMap['isModal'] = false
        notificationActionMap['notificationParams'] = []
        notificationActionMap['notificationParams'] << ['name': 'id', 'value': objectSourceId]


        List<Map> notificationActionsList = [notificationActionMap]

        /**
         * Create notification terms
         */
        List<UserTerm> userTermKeyList = []
        List<String> userTermValueList = []

        userTermKeyList << UserTerm.DEPARTMENT
        userTermKeyList << UserTerm.JOB_TITLE
        userTermValueList << departmentId
        userTermValueList << jobTitleId

        createCorrespondenceNotification(objectSourceId, objectSourceReference, notificationDate, EnumCorrespondenceStatus.IN_PROGRESS, userTermKeyList,
                userTermValueList, notificationActionsList, EnumNotificationType.WORKFLOW_MESSAGES, notificationText, [])
    }


    void notifyWorkflowCompletion(String objectSourceId, String objectSourceReference, EnumWorkflowCalculationResult workflowResult,
                                  String notificationText, String notificationTitle, ZonedDateTime notificationDate) {

        AocCorrespondenceList aocCorrespondenceList= AocCorrespondenceList.read(Long.parseLong(objectSourceId))
        EnumCorrespondenceStatus correspondenceStatus
        if (workflowResult == EnumWorkflowCalculationResult.COMPLETED) {
            notificationText = "workflow.notification.approved.message"
            correspondenceStatus = EnumCorrespondenceStatus.APPROVED
        } else {
            notificationText = "workflow.notification.rejected.message"
            correspondenceStatus = EnumCorrespondenceStatus.REJECTED
        }

        /**
         * update correspondence status, update records status
         */
        updateCorrespondenceStatus(aocCorrespondenceList)

        List<UserTerm> userTermKeyList = []
        List<String> userTermValueList = []
//        Request request = Request.read(objectSourceId)
//        userTermKeyList << UserTerm.USER
//        userTermValueList << request?.employee?.personId

        //TODO who should be notified on correspondence workflow completion
//        createRequestNotification(objectSourceId, objectSourceReference, notificationDate, requestStatus, userTermKeyList,
//                userTermValueList, null, EnumNotificationType.REQUEST_MESSAGES, notificationText, [objectSourceId])
    }


    /**
     * Create a dynamic notification for request
     * @param objectSourceId
     * @param objectSourceReference
     * @param notificationDate
     * @param correspondenceStatus
     * @param userTermKeyList
     * @param userTermValueList
     * @param notificationActionsMap
     * @param notificationType
     * @param notificationTextPrefix
     */
    public void createCorrespondenceNotification(String objectSourceId, String objectSourceReference,
                                                 ZonedDateTime notificationDate, EnumCorrespondenceStatus correspondenceStatus,
                                                 List<UserTerm> userTermKeyList = null,
                                                 List<String> userTermValueList = null,
                                                 List<Map> notificationActionsList = null,
                                                 EnumNotificationType notificationType = EnumNotificationType.WORKFLOW_MESSAGES,
                                                 String notificationTextCode = '', List<String> messageParamList = [], Map notificationMap = [:]) {
        GrailsParameterMap notificationParams
        Map notificationTermsMap
        Map notificationKeys
        Map notificationValues

        Boolean developmentModeWithServiceCatalog = Holders.grailsApplication.config.grails.developmentModeWithServiceCatalog

        try {

            /**
             *
             */
            String controllerName = "aocCorrespondenceList"
//            AocCorrespondenceList aocCorrespondenceList1 = AocCorrespondenceList.read(objectSourceId)


            notificationParams = new GrailsParameterMap([:], null)

            //fill notification params and save notification
            notificationParams["objectSourceId"] = objectSourceId
            notificationParams.objectSourceReference = objectSourceReference
            notificationParams.title = "${messageSource.getMessage("${controllerName}" + ".label", [] as Object[], new Locale("ar"))}"
            notificationParams.notificationDate = notificationDate
            notificationParams["notificationType"] = NotificationType.read(notificationType.value)



            notificationTermsMap = [:]
            notificationKeys = [:]
            notificationValues = [:]


            SearchBean searchBean
            UserDTO userDTO = null
            userTermKeyList?.eachWithIndex { key, index ->
                switch (key) {
                    case UserTerm.USER:
                        searchBean = new SearchBean()
                        searchBean.searchCriteria.put("personId", new SearchConditionCriteriaBean(operand: 'personId', value1: userTermValueList?.get(index)))
                        searchBean.searchCriteria.put("firmId", new SearchConditionCriteriaBean(operand: 'firmId', value1: request?.firm?.id))
                        if (Environment.current == Environment.PRODUCTION || developmentModeWithServiceCatalog) {
                            userDTO = userService.getUser(searchBean)
                            notificationKeys.put(new Integer(index + 1), UserTerm.USER.value())
                            notificationValues.put(new Integer(index + 1), "${userDTO?.username}")
                        }else if ( Environment.current == Environment.DEVELOPMENT) {
                            notificationKeys.put(new Integer(index + 1), UserTerm.USER.value())
                            notificationValues.put(new Integer(index + 1), "admin")
                        }
                        break
                    default:
                        notificationKeys.put(new Integer(index + 1), userTermKeyList?.get(index)?.value())
                        notificationValues.put(new Integer(index + 1), "${userTermValueList?.get(index)}")
                        break
                }
            }

            if (notificationMap["withEmployeeName"]) {
                if (!userDTO) {
                    searchBean = new SearchBean()
                    searchBean.searchCriteria.put("personId", new SearchConditionCriteriaBean(operand: 'personId', value1: request?.employee?.personId))
                    searchBean.searchCriteria.put("firmId", new SearchConditionCriteriaBean(operand: 'firmId', value1: request?.firm?.id))
                    if (Environment.current == Environment.PRODUCTION || developmentModeWithServiceCatalog) {
                        userDTO = userService.getUser(searchBean)
                        messageParamList.push(userDTO?.personName)
                    }else if ( Environment.current == Environment.DEVELOPMENT) {
                        messageParamList.push("admin")
                    }
                }
            }
            if (notificationTextCode) {
                notificationParams.text = "${messageSource.getMessage("${notificationTextCode}", messageParamList as Object[], new Locale("ar"))}"
            } else {
                if (correspondenceStatus == EnumCorrespondenceStatus.REJECTED) {
                    notificationParams.text = "${messageSource.getMessage("aocCorrespondenceList.notification.rejectList.message", ["${objectSourceId}"] as Object[], new Locale("ar"))}"
                } else if (correspondenceStatus == EnumCorrespondenceStatus.APPROVED) {
                    notificationParams.text = "${messageSource.getMessage("aocCorrespondenceList.notification.approveList.message", ["${objectSourceId}"] as Object[], new Locale("ar"))}"
                } else if (correspondenceStatus == EnumCorrespondenceStatus.NEW) {
                    notificationParams.text = "${messageSource.getMessage("list.notification.received.message", ["${objectSourceId}"] as Object[], new Locale("ar"))}"
                }
            }

            notificationTermsMap.put("key", notificationKeys)
            notificationTermsMap.put("value", notificationValues)
            notificationParams["notificationTerms"] = notificationTermsMap
            notificationParams["notificationActions"] = notificationActionsList

            //save notification
            notificationService?.save(notificationParams)
        } catch (Exception ex) {
            ex.printStackTrace()
            throw new Exception("error create notification for request ", ex)
        }
    }

    /**
     * update aocCorrespondenceStatus to partially approved in case some records are approved and others are rejected
     * update hrCorrespondence status to received or closed
     * update hrRecords statuses to approved or rejected
     * called on workflow completion
     * @param aocCorrespondenceList
     */
    private void updateCorrespondenceStatus(AocCorrespondenceList aocCorrespondenceList){
        if(aocCorrespondenceList.currentStatus==EnumCorrespondenceStatus.APPROVED) {
            // change status of correspondence to partially approved if any of its records are rejected
            def rejectedRecordsCount = AocListRecord.createCriteria().get {
                joinedCorrespondenceListRecords {
                    eq('correspondenceList.id', aocCorrespondenceList.id)
                }
                eq('recordStatus', EnumListRecordStatus.REJECTED)
                projections {
                    count('id')
                }
            }
            log.info("Number of rejected records = " + rejectedRecordsCount)
            if (rejectedRecordsCount > 0) {
                log.debug("correspondence status will be updated")
                println("correspondence status will be updated")
                int updatedRows = AocCorrespondenceList.executeUpdate('update ' + AocCorrespondenceList.getName() + ' set currentStatus = :partialApprovedStatus ' +
                        'where id= :listId', [partialApprovedStatus: EnumCorrespondenceStatus.PARTIALLY_APPROVED, listId: aocCorrespondenceList.id])
                if (updatedRows == 0) {
                    log.error("Failed update correspondence status to partially approved")
                }
            }
        }
    }
}
