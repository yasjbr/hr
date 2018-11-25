<el:hiddenField name="employee.id" value="${bordersSecurityCoordination?.employee?.id}"/>
<el:hiddenField name="person.id" value="${bordersSecurityCoordination?.employee?.personId}"/>

<g:render template="/employee/wrapperForm"
          model="[employee: bordersSecurityCoordination?.employee]"/>

<el:row/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>

        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'bordersSecurityCoordination.requestDate.label', default: 'requestDate')}"
                          value="${bordersSecurityCoordination?.requestDate ? bordersSecurityCoordination?.requestDate : java.time.ZonedDateTime.now()}"/>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" controller="documentType"
                             action="autocomplete" name="legalIdentifierId"
                             label="${message(code: 'bordersSecurityCoordination.legalIdentifierId.label', default: 'legalIdentifierId')}"
                             values="${[[bordersSecurityCoordination?.legalIdentifierId, bordersSecurityCoordination?.transientData?.documentTypeDTO?.descriptionInfo?.localName]]}"/>
        </el:formGroup>

        <el:formGroup>
            <el:dateField name="fromDate" size="6" class=" isRequired" setMinDateFor="toDate"
                          label="${message(code: 'bordersSecurityCoordination.fromDate.label', default: 'fromDate')}"
                          value="${bordersSecurityCoordination?.fromDate}"/>

            <el:dateField name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'bordersSecurityCoordination.toDate.label', default: 'toDate')}"
                          value="${bordersSecurityCoordination?.toDate}"/>
        </el:formGroup>

        <el:formGroup>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             controller="borderCrossingPoint"
                             action="autocomplete" name="borderLocationId"
                             label="${message(code: 'bordersSecurityCoordination.borderLocationId.label', default: 'borderLocationId')}"
                             values="${[[bordersSecurityCoordination?.borderLocationId, bordersSecurityCoordination?.transientData?.borderCrossingPointDTO?.descriptionInfo?.localName]]}"/>


            <el:textField name="requestReason" size="6" class=""
                          label="${message(code: 'bordersSecurityCoordination.requestReason.label', default: 'requestReason')}"
                          value="${bordersSecurityCoordination?.requestReason}"/>

        </el:formGroup>
        <el:formGroup>
            <el:textArea name="unstructuredLocation" size="6" class=""
                         label="${message(code: 'bordersSecurityCoordination.unstructuredLocation.label', default: 'unstructuredLocation')}"
                         value="${bordersSecurityCoordination?.unstructuredLocation}"/>

        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<br/>
<g:if test="${params.action != "edit"}">
    <lay:widget transparent="true" color="blue" icon="icon-info-4"
                title="${g.message(code: "bordersSecurityCoordination.addressesAndPhone.label")}">
        <lay:widgetBody>

            <g:render template="/pcore/person/contactInfo/form"
                      model="${[params: [isDisabled: true, isHiddenPersonInfo: "true"], isRelatedObjectTypeDisabled: true, hideDetails: true, withPhoneNumber: true]}"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>










