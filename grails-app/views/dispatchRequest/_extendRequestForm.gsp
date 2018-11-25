<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/dispatchRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.DISPATCH_EXTEND_REQUEST,
                                           dispatchRequest: request]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'dispatchRequest.requestDate.label', default: 'requestDate')}"
                          value="${java.time.ZonedDateTime.now()}"/>
            <el:dateField name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'dispatchRequest.toDate.label', default: 'toDate')}"/>
        </el:formGroup>

        <el:formGroup>
            <el:textArea name="requestReason" size="6" class=" isRequired"
                         label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>

