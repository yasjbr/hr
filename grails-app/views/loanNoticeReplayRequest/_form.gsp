
<el:hiddenField name="loanNotice.encodedId" value="${loanNoticeReplayRequest?.loanNotice?.encodedId}" />

<lay:widget transparent="true" color="blue" icon="icon-info-4"
            title="${g.message(code: "loanNoticeReplayRequest.loanNoticeInfo.label")}">

    <lay:widgetBody>
        <lay:showWidget size="6">

            <lay:showElement value="${loanNoticeReplayRequest?.loanNotice?.requestedJob}" type="String"
                             label="${message(code:'loanNotice.requestedJob.label',default:'requestedJob')}" />



            <lay:showElement value="${loanNoticeReplayRequest?.loanNotice?.fromDate}" type="ZonedDate"
                             label="${message(code:'loanNotice.fromDate.label',default:'fromDate')}" />


            <lay:showElement value="${loanNoticeReplayRequest?.loanNotice?.numberOfPositions}" type="Short"
                             label="${message(code:'loanNotice.numberOfPositions.label',default:'numberOfPositions')}" />




        </lay:showWidget>

        <lay:showWidget size="6">

            <lay:showElement value="${loanNoticeReplayRequest?.loanNotice?.transientData?.requesterOrganizationDTO}" type="String"
                             label="${message(code:'loanNotice.requesterOrganizationId.label',default:'requestedFromOrganizationId')}" />

            <lay:showElement value="${loanNoticeReplayRequest?.loanNotice?.toDate}" type="ZonedDate"
                             label="${message(code:'loanNotice.toDate.label',default:'toDate')}" />

            <lay:showElement value="${loanNoticeReplayRequest?.loanNotice?.loanNoticeStatus}" type="enum"
                             messagePrefix="EnumLoanNoticeStatus"
                             label="${message(code:'loanNotice.loanNoticeStatus.label',default:'loanNoticeStatus')}" />

        </lay:showWidget>

        <el:row/>
    </lay:widgetBody>
</lay:widget>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>


        <msg:warning label="${message(code:'loanNoticeReplayRequest.justCommittedEmployee.label')}" />






        <el:formGroup>

            <g:render template="/employee/wrapper" model="[isDisabled            : (loanNoticeReplayRequest?.employee?.id != null),
                                                           name                  : 'employee.id',
                                                           id                    : 'employee.id',
                                                           paramsGenerateFunction: 'employeeParams',
                                                           size                  :  6,
                                                           bean                  : loanNoticeReplayRequest?.employee,
                                                           disableFormGroupName  : true
            ]"/>


            <el:textField name="requestReason" size="6"  class=""
                          label="${message(code:'loanNoticeReplayRequest.requestReason.label',default:'requestReason')}"
                          value="${loanNoticeReplayRequest?.requestReason}"/>
        </el:formGroup>

        <el:formGroup>

            <el:textArea name="requestStatusNote" size="6"
                         class=""
                         label="${message(code:'loanNoticeReplayRequest.requestStatusNote.label',default:'requestStatusNote')}"
                         value="${loanNoticeReplayRequest?.requestStatusNote}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request: loanNoticeReplayRequest, formName: 'loanNoticeReplayRequestForm',parentFolder:'loanNoticeReplayList']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>
<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>


<script type="text/javascript">

    function employeeParams() {
        return {'categoryStatusId':'${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.value}'}
    }

</script>