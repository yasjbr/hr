<el:hiddenField name="employee.id" value="${suspensionExtensionRequest?.suspensionRequest?.employee?.id}"/>
<el:hiddenField name="suspensionRequest.id" value="${suspensionExtensionRequest?.suspensionRequest?.id}"/>
<el:hiddenField name="firm.id" value="${suspensionExtensionRequest?.firm?.id}"/>

<g:render template="/employee/wrapperForm" model="[employee: suspensionExtensionRequest?.suspensionRequest?.employee]"/>
<g:render template="/suspensionRequest/wrapperForm"
          model="[suspensionRequest: suspensionExtensionRequest?.suspensionRequest]"/>


<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired" setMinDateFor="toDate"
                          label="${message(code: 'suspensionExtensionRequest.requestDate.label', default: 'requestDate')}"
                          value="${suspensionExtensionRequest?.requestDate ? suspensionExtensionRequest?.requestDate : java.time.ZonedDateTime.now()}"/>


            <el:dateField name="startDate" size="6" class=" isRequired" isDisabled="true"
                          isReadOnly="true"
                          label="${message(code: 'suspensionExtensionRequest.startDate.label', default: 'startDate')}"
                          value="${suspensionExtensionRequest?.suspensionRequest?.toDate}"/>

        </el:formGroup>

        <el:formGroup>
            <el:dateField name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'suspensionExtensionRequest.toDate.label', default: 'toDate')}"
                          value="${suspensionExtensionRequest?.toDate}"/>

            <el:textArea name="requestReason" size="6" class=""
                         label="${message(code: 'suspensionExtensionRequest.requestReason.label', default: 'requestReason')}"
                         value="${suspensionExtensionRequest?.requestReason}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<br/>
<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>
