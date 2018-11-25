<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/dispatchRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.DISPATCH_STOP_REQUEST,
                                           dispatchRequest: request]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "stopDispatch.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'dispatchRequest.requestDate.label', default: 'requestDate')}"
                          value="${java.time.ZonedDateTime.now()}"/>

            <el:dateField name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'stopDispatch.stopDate.label', default: 'stopDate')}"/>
        </el:formGroup>

        <el:formGroup>
            <el:textArea name="extraInfoData.note" size="6" class=""
                         label="${message(code: 'stopDispatch.note.label', default: 'note')}"/>
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
