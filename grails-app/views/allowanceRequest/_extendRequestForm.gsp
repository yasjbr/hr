<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/allowanceRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.ALLOWANCE_CONTINUE_REQUEST,
                                           allowanceRequest: request]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'allowanceRequest.requestDate.label', default: 'requestDate')}"
                          value="${java.time.ZonedDateTime.now()}"/>
            <el:dateField name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'allowanceRequest.toDate.label', default: 'toDate')}"/>
        </el:formGroup>

        <el:formGroup>
            <el:textArea name="requestReason" size="6" class=" isRequired"
                         label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>
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
