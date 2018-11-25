package ps.gov.epsilon.hr.common

import grails.transaction.Transactional
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceList
import ps.gov.epsilon.aoc.correspondences.AocCorrespondenceNotificationService
import ps.gov.epsilon.hr.enums.v1.EnumRequestStatus
import ps.gov.epsilon.hr.firm.request.Request
import ps.gov.epsilon.hr.firm.request.RequestService
import ps.gov.epsilon.hr.firm.vacation.EmployeeVacationBalanceService
import ps.gov.epsilon.workflow.enums.v1.EnumWorkflowCalculationResult
import ps.gov.epsilon.workflow.interfaces.v1.INotificationService
import ps.police.notification.v1.EnumNotificationType
import ps.police.notifications.enums.UserTerm

import java.time.ZonedDateTime

@Transactional
class WorkflowNotificationService  implements INotificationService{

    RequestService requestService
    EmployeeVacationBalanceService employeeVacationBalanceService
    AocCorrespondenceNotificationService aocCorrespondenceNotificationService

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
    @Override
    void notifyWorkflowAction(String jobTitleId, String departmentId, String objectSourceId, String objectSourceReference, String notificationText, String notificationTitle, ZonedDateTime notificationDate) {

        if(AocCorrespondenceList.getName().equals(objectSourceReference)){
            // aocCorrespondenceListService should handle this
            aocCorrespondenceNotificationService.notifyWorkflowAction(jobTitleId, departmentId, objectSourceId, objectSourceReference,
                    notificationText, notificationTitle, notificationDate)
            return
        }

        // Extract controller name from objectSourceReference
        String controllerName = requestService.extractControllerName(objectSourceReference)

        /**
         * Create notification action
         */
        Map notificationActionMap = [:]
        notificationActionMap['action'] = 'manageRequestModal'
        notificationActionMap['controller'] = 'request'
        notificationActionMap['label'] = 'request.manageRequest.label'
        notificationActionMap['icon'] = 'icon-cog'
        notificationActionMap['isModal'] = true
        notificationActionMap['notificationParams'] = []
        notificationActionMap['notificationParams'] << ['name': 'controllerName', 'value': controllerName]
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

        requestService.createRequestNotification(objectSourceId, objectSourceReference, notificationDate, EnumRequestStatus.IN_PROGRESS, userTermKeyList,
                userTermValueList, notificationActionsList, EnumNotificationType.WORKFLOW_MESSAGES, notificationText, [])
    }

    @Override
    void notifyWorkflowCompletion(String objectSourceId, String objectSourceReference, EnumWorkflowCalculationResult workflowResult, String notificationText, String notificationTitle, ZonedDateTime notificationDate) {

        if(AocCorrespondenceList.getName().equals(objectSourceReference)){
            // aocCorrespondenceListService should handle this
            aocCorrespondenceNotificationService.notifyWorkflowCompletion(objectSourceId, objectSourceReference, workflowResult,
                    notificationText, notificationTitle, notificationDate)
            return
        }

        EnumRequestStatus requestStatus
        if (workflowResult == EnumWorkflowCalculationResult.COMPLETED) {
            notificationText = "workflow.notification.approved.message"
        } else {
            notificationText = "workflow.notification.rejected.message"
        }
        List<UserTerm> userTermKeyList = []
        List<String> userTermValueList = []
        Request request = Request.read(objectSourceId)
        userTermKeyList << UserTerm.USER
        userTermValueList << request?.employee?.personId
        requestStatus= request?.requestStatus

        requestService.createRequestNotification(objectSourceId, objectSourceReference, notificationDate, requestStatus, userTermKeyList,
                userTermValueList, null, EnumNotificationType.MY_NOTIFICATION, notificationText, [objectSourceId])

    }

}
