<el:hiddenField name="employee.id" value="${suspensionRequest?.employee?.id}"/>
<el:hiddenField name="suspensionType" type="Enum" value="${suspensionRequest?.suspensionType}"/>

<msg:warning label="${message(code: 'suspensionRequest.request.warning')}"/>

<g:render template="/employee/wrapperForm"
          model="[employee: suspensionRequest?.employee]"/>

<el:row/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'suspensionRequest.requestDate.label', default: 'requestDate')}"
                          value="${suspensionRequest?.requestDate ?: java.time.ZonedDateTime.now()}"/>


            <el:select valueMessagePrefix="EnumSuspensionType" isDisabled="true"
                       from="${ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.values()}" name="suspensionType"
                       size="6" class="" isReadOnly="true" value="${suspensionRequest?.suspensionType}"
                       label="${message(code: 'suspensionRequest.suspensionType.label', default: 'suspensionType')}"/>

        </el:formGroup>


        <el:formGroup>
            <el:dateField name="fromDate" size="6" class=" isRequired" setMinDateFor="toDate" id="fromDate"
                          label="${message(code: 'suspensionRequest.fromDate.label', default: 'fromDate')}"
                          value="${suspensionRequest?.fromDate}"/>

            <el:dateField name="toDate" id="toDate" size="6" class=" isRequired"
                          label="${message(code: 'suspensionRequest.toDate.label', default: 'toDate')}"
                          value="${suspensionRequest?.toDate}"/>

        </el:formGroup>


    %{--in case: suspension type is DISCIPLINARY--}%

        <el:formGroup>
            <el:textArea name="requestReason" size="6" class=""
                         label="${message(code: 'suspensionRequest.requestReason.label', default: 'requestReason')}"
                         value="${suspensionRequest?.requestReason}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request: suspensionRequest, formName:'suspensionRequestForm']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>
