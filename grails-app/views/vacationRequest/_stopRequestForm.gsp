<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType" %>
<g:render template="/vacationRequest/operationForm" model="[requestType: ps.gov.epsilon.hr.enums.v1.EnumRequestType.REQUEST_FOR_VACATION_STOP,
                                           vacationRequest: request]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "stopAllowance.info.label")}">
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

        <el:formGroup>
            <el:textField name="stopVacationReason" size="6" class=" isRequired"
                          label="${message(code: 'stopVacationRequest.stopVacationReason.label', default: 'stopVacationReason')}"
                          value="${stopVacationRequest?.stopVacationReason}"/>

            <el:checkboxField name="byHR" size="6" class=""
                              label="${message(code: 'stopVacationRequest.byHR.label', default: 'byHR')}"
                              value="${stopVacationRequest?.byHR}" isChecked="${stopVacationRequest?.byHR}"/>

        </el:formGroup>


        <el:formGroup>
            <el:textArea name="note" size="6" class=" "
                         label="${message(code: 'stopVacationRequest.note.label', default: 'note')}"
                         value="${stopVacationRequest?.note}"/>
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