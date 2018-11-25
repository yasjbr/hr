<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/maritalStatusRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.MARITAL_STATUS_CANCEL_REQUEST,
                                           maritalStatusRequest: request]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <g:if test="${showAllLevels}">
            <el:formGroup>
                <el:checkboxField name="extraInfoData.allLevels" size="6"
                                  label="${message(code: 'request.allLevels.label', default: 'All levels')}"/>
            </el:formGroup>
        </g:if>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'maritalStatusRequest.requestDate.label', default: 'requestDate')}"
                          value="${java.time.ZonedDateTime.now()}"/>
            <el:textArea name="requestReason" size="6" class="isRequired"
                         label="${message(code: 'request.requestReason.label', default: 'requestReason')}"/>
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
