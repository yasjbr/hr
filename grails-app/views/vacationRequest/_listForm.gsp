<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             controller="vacationType" action="autocomplete" name="vacationType.id"
                             label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"
                             values="${[[vacationRequest?.vacationType?.id, vacationRequest?.vacationType?.descriptionInfo?.localName]]}"/>

            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'vacationRequest.requestDate.label', default: 'requestDate')}"
                          value="${vacationRequest?.requestDate ? vacationRequest?.requestDate : java.time.ZonedDateTime.now()}"/>

        </el:formGroup>


        <el:formGroup>

            <el:dateField name="fromDate" size="6" class=" isRequired" setMinDateFor="toDate"
                          label="${message(code: 'vacationRequest.fromDate.label', default: 'fromDate')}"
                          value="${vacationRequest?.fromDate}"/>

            <el:dateField name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'vacationRequest.toDate.label', default: 'toDate')}"
                          value="${vacationRequest?.toDate}"/>

        </el:formGroup>



        <el:formGroup>

            <el:textArea name="requestReason" size="6" class=""
                         label="${message(code: 'vacationRequest.requestReason.label', default: 'requestReason')}"
                         value="${vacationRequest?.requestReason}"/>
            <el:checkboxField name="external" size="6" class=" " onchange="vacationTransferValueSettings(this);"
                              label="${message(code: 'vacationRequest.external.label', default: 'external')}"
                              value="${vacationRequest?.external}" isChecked="${vacationRequest?.external}"/>

        </el:formGroup>

    </lay:widgetBody>
</lay:widget>

<g:if test="${params.action != "edit"}">
    <lay:widget transparent="true" color="blue" icon="icon-info-4"
                title="${g.message(code: "bordersSecurityCoordination.addressesAndPhone.label")}">
        <lay:widgetBody>

            <g:render template="/pcore/person/contactInfo/form"
                      model="${[params: [isDisabled: true, isHiddenPersonInfo: "true"], isRelatedObjectTypeDisabled: true, hideDetails: true, withPhoneNumber: true, isRequiredPhoneNumber: false]}"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>


<lay:wall title="${g.message(code: 'vacationRequest.employees.label')}">

    <div id="errorDiv" style="display: none;">
        <msg:error label="${message(code: 'loanRequest.relatedPersonError.label')}"/>
    </div>
    <br/>

    <div id="relatedPersonDiv">
        <el:formGroup>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="8" id="employeeAutoComplete"
                    class=" "
                    controller="employee"
                    action="autocomplete"
                    name="employees" paramsGenerateFunction="employeeParams"
                    label="${message(code: 'employee.transientData.personDTO.localFullName.label', default: 'employee name')}"/>

            <btn:addButton onclick="addRelatedEmployee()"/>
        </el:formGroup>
        <el:row/>

        <ol id="employeeList">

        </ol>
    </div>
</lay:wall>


<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:if test="${vacationRequest?.vacationType?.needsExternalApproval}">
                <g:render template="/request/wrapperManagerialOrder"/>
            </g:if>
            <g:else>
                <g:render template="/request/wrapperManagerialOrder" model="[hideExternalOrderInfo:true]"/>
            </g:else>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>







