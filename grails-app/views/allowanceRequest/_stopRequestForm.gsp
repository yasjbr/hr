<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/allowanceRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.ALLOWANCE_STOP_REQUEST,
                                           allowanceRequest: request]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "stopAllowance.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'allowanceRequest.requestDate.label', default: 'requestDate')}"
                          value="${java.time.ZonedDateTime.now()}"/>
        </el:formGroup>

        <el:formGroup>
            <el:dateField name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'stopAllowance.stopDate.label', default: 'stopDate')}"/>
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             controller="allowanceStopReason" action="autocomplete"
                             name="extraInfoData.allowanceStopReason.id"
                             label="${message(code: 'stopAllowance.allowanceStopReason.label', default: 'allowanceStopReason')}"/>
        </el:formGroup>

        <el:formGroup>
            <el:checkboxField name="extraInfoData.stoppedByEmployee" size="6" class=""
                              label="${message(code: 'stopAllowance.stoppedByEmployee.label', default: 'stoppedByEmployee')}"/>
            <el:textArea name="extraInfoData.note" size="6" class=""
                         label="${message(code: 'stopAllowance.note.label', default: 'note')}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>
