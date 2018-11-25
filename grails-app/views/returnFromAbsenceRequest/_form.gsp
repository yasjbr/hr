<el:hiddenField name="absence.id" value="${returnFromAbsenceRequest?.absence?.id}"/>
<el:hiddenField name="employee.id" value="${returnFromAbsenceRequest?.employee?.id}"/>

<g:render template="/employee/wrapperForm" model="[employee: returnFromAbsenceRequest?.employee]"/>

<br/>

<lay:widget transparent="true" color="blue" icon="icon-asterisk"
            title="${g.message(code: "absence.info.label")}">
    <lay:widgetBody>
        <lay:showWidget size="6">
            <lay:showElement
                    value="${returnFromAbsenceRequest?.absence?.id}"
                    type="String"
                    label="${message(code: 'absence.id.label', default: 'id')}"/>
            <lay:showElement
                    value="${returnFromAbsenceRequest?.absence?.fromDate}"
                    type="ZonedDate"
                    label="${message(code: 'absence.fromDate.label', default: 'fromDate')}"/>
        </lay:showWidget>
        <lay:showWidget size="6">
            <lay:showElement
                    value="${returnFromAbsenceRequest?.absence?.absenceReason}"
                    type="Enum"
                    messagePrefix="EnumAbsenceReason"
                    label="${message(code: 'absence.absenceReason.label', default: 'absenceReason')}"/>
            <lay:showElement
                    value="${returnFromAbsenceRequest?.absence?.violationStatus}"
                    type="Enum"
                    messagePrefix="EnumViolationStatus"
                    label="${message(code: 'absence.violationStatus.label', default: 'violationStatus')}"/>
        </lay:showWidget>
    </lay:widgetBody>
</lay:widget>
<el:row/>
<br/>

<lay:widget transparent="true" color="blue" icon="icon-info-4"
            title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'returnFromAbsenceRequest.requestDate.label', default: 'requestDate')}"
                          value="${returnFromAbsenceRequest?.requestDate}"/>
        <el:select valueMessagePrefix="EnumAbsenceReason"
                   from="${ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.values()}" name="actualAbsenceReason"
                   size="6" class=" isRequired"
                   label="${message(code: 'returnFromAbsenceRequest.actualAbsenceReason.label', default: 'actualAbsenceReason')}"
                   value="${returnFromAbsenceRequest?.actualAbsenceReason}"/>
        </el:formGroup>

        <el:formGroup>
            <el:dateField name="actualReturnDate" size="6" class=" isRequired"
                          label="${message(code: 'returnFromAbsenceRequest.actualReturnDate.label', default: 'actualReturnDate')}"
                          isMaxDate="true"
                          value="${returnFromAbsenceRequest?.actualReturnDate}"/>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'returnFromAbsenceRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${returnFromAbsenceRequest?.requestStatusNote}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<el:row/>
<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request: returnFromAbsenceRequest, formName:'returnFromAbsenceRequestForm']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>
<br/>
<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>
