<el:hiddenField name="employee.id" value="${stopVacationRequest?.vacationRequest?.employee?.id}"/>
<el:hiddenField name="vacationRequest.id" value="${stopVacationRequest?.vacationRequest?.id}"/>


<g:render template="/employee/wrapperForm" model="[employee: stopVacationRequest?.vacationRequest?.employee]"/>
<g:render template="/vacationRequest/wrapperForm" model="[vacationRequest: stopVacationRequest?.vacationRequest]"/>



<lay:widget transparent="true" color="blue" icon="icon-info-4"
            title="${g.message(code: "stopVacationRequest.info.label")}">
    <lay:widgetBody>
        <br/>

        <el:formGroup>

            <el:dateField name="requestDate" size="6" class=" isRequired" setMinDateFor="toDate"
                          label="${message(code: 'stopVacationRequest.requestDate.label', default: 'requestDate')}"
                          value="${stopVacationRequest?.requestDate ? stopVacationRequest?.requestDate : java.time.ZonedDateTime.now()}"/>


            <el:dateField name="stopVacationDate" size="6" class=" isRequired"
                          label="${message(code: 'stopVacationRequest.stopVacationDate.label', default: 'stopVacationDate')}"
                          value="${stopVacationRequest?.stopVacationDate ?: java.time.ZonedDateTime.now()}"/>

        </el:formGroup>

    %{-- <el:formGroup>
         <el:checkboxField name="sendSMS" size="6" class=""
                           label="${message(code: 'stopVacationRequest.sendSMS.label', default: 'sendSMS')}"
                           value="${stopVacationRequest?.sendSMS}" isChecked="${stopVacationRequest?.sendSMS}"/>
         <el:checkboxField name="sendEmail" size="6" class=""
                           label="${message(code: 'stopVacationRequest.sendEmail.label', default: 'sendEmail')}"
                           value="${stopVacationRequest?.sendEmail}" isChecked="${stopVacationRequest?.sendEmail}"/>
     </el:formGroup>--}%

        <el:formGroup>
            <el:textField name="stopVacationReason" size="6" class=" isRequired"
                          label="${message(code: 'stopVacationRequest.stopVacationReason.label', default: 'stopVacationReason')}"
                          value="${stopVacationRequest?.stopVacationReason}"/>

            <el:checkboxField name="byHR" size="6" class=""
                              label="${message(code: 'stopVacationRequest.byHR.label', default: 'byHR')}"
                              value="${stopVacationRequest?.byHR}" isChecked="${stopVacationRequest?.byHR}"/>

        </el:formGroup>

        <el:formGroup>
            <g:if test="${stopVacationRequest?.vacationRequest?.toDate?.toLocalDate() == java.time.ZonedDateTime.now()?.toLocalDate()}">
                <el:textArea name="note" size="6" class=""
                             label="${message(code: 'stopVacationRequest.note.label', default: 'note')}"
                             value="${stopVacationRequest?.note}"/>

            </g:if>

            <g:else>
                <el:textArea name="note" size="6" class=" isRequired"
                             label="${message(code: 'stopVacationRequest.note.label', default: 'note')}"
                             value="${stopVacationRequest?.note}"/>

            </g:else></el:formGroup>
    </lay:widgetBody>
</lay:widget>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>
