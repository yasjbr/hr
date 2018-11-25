<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${traineeListEmployee?.traineeList?.fromDate}" type="ZonedDate"
                     label="${message(code: 'traineeList.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${traineeListEmployee?.traineeList?.toDate}" type="ZonedDate"
                     label="${message(code: 'traineeList.toDate.label', default: 'toDate')}"/>

    <lay:showElement value="${traineeListEmployee?.traineeList?.transientData?.location}" type="String"
                     label="${message(code: 'traineeList.trainingLocationId.label', default: 'trainingLocationId')}"/>

    <lay:showElement value="${traineeListEmployee?.recordStatus}" type="enum"
                     label="${message(code: 'traineeListEmployee.recordStatus.label', default: 'recordStatus')}"
                     messagePrefix="EnumListRecordStatus"/>
    <g:if test="${traineeListEmployee?.recordStatus == ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.APPROVED}">
        <lay:showElement value="${traineeListEmployee?.trainingEvaluation}" type="Enum"
                         label="${message(code: 'traineeList.trainingEvaluation.label', default: 'trainingEvaluation')}"
                         messagePrefix="EnumTrainingEvaluation"/>
        <lay:showElement value="${traineeListEmployee?.mark}" type="String"
                         label="${message(code: 'traineeList.mark.label', default: 'mark')}"/>
    </g:if>
    <g:else>
        <lay:showElement value="${traineeListEmployee?.trainingRejectionReason}" type="TrainingRejectionReason"
                         label="${message(code: 'traineeList.trainingRejectionReason.label', default: 'trainingRejectionReason')}"/>
    </g:else>

</lay:showWidget>
<el:row/>