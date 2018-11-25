<el:hiddenField name="employee.id" value="${updateMilitaryRankRequest?.employee?.id}"/>
<el:hiddenField name="requestType" value="${updateMilitaryRankRequest?.requestType}"/>

<g:render template="/employee/wrapperForm" model="[employee: updateMilitaryRankRequest?.employee]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>

        <el:formGroup>
            <g:set var="requestTypeValue" value="EnumRequestType.${updateMilitaryRankRequest?.requestType}" />
            <el:textField name="requestType" value="${message(code: requestTypeValue)}"
                      isReadOnly="true"
                      label="${message(code: 'updateMilitaryRankRequest.requestType.label')}"
                      size="6"/>

            <el:dateField isMaxDate="true" name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'updateMilitaryRankRequest.requestDate.label', default: 'requestDate')}"
                          value="${updateMilitaryRankRequest?.requestDate}"/>

        </el:formGroup>

        <g:if test="${updateMilitaryRankRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.UPDATE_MILITARY_RANK_TYPE}">
            <el:formGroup>
                <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="militaryRankType"
                                 action="autocomplete" name="oldRankType.id" id="oldRankTypeId"
                                 paramsGenerateFunction="oldRankTypeParams"
                                 label="${message(code: 'updateMilitaryRankRequest.oldRankType.label', default: 'oldRankType')}"
                                 values="${[[updateMilitaryRankRequest?.oldRankType?.id, updateMilitaryRankRequest?.oldRankType?.descriptionInfo?.localName]]}"/>

                <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                                 controller="militaryRankType"
                                 action="autocomplete" name="newRankType.id" id="newRankTypeId"
                                 paramsGenerateFunction="newRankTypeParams"
                                 label="${message(code: 'updateMilitaryRankRequest.newRankType.label', default: 'newRankType')}"
                                 values="${[[updateMilitaryRankRequest?.newRankType?.id, updateMilitaryRankRequest?.newRankType?.descriptionInfo?.localName]]}"/>
            </el:formGroup>
        </g:if>
        <g:else>
            <el:formGroup>
                <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                                 controller="militaryRankClassification"
                                 action="autocomplete" name="oldRankClassification.id" id="oldRankClassificationId"
                                 paramsGenerateFunction="oldRankClassificationParams"
                                 label="${message(code: 'updateMilitaryRankRequest.oldRankClassification.label', default: 'oldRankClassification')}"
                                 values="${[[updateMilitaryRankRequest?.oldRankClassification?.id, updateMilitaryRankRequest?.oldRankClassification?.descriptionInfo?.localName]]}"/>
                <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                                 controller="militaryRankClassification"
                                 action="autocomplete" name="newRankClassification.id" id="newRankClassificationId"
                                 paramsGenerateFunction="newRankClassificationParams"
                                 label="${message(code: 'updateMilitaryRankRequest.newRankClassification.label', default: 'newRankClassification')}"
                                 values="${[[updateMilitaryRankRequest?.newRankClassification?.id, updateMilitaryRankRequest?.newRankClassification?.descriptionInfo?.localName]]}"/>
            </el:formGroup>
        </g:else>



        <el:formGroup>
            <el:dateField name="dueDate" size="6" class=" isRequired"
                          label="${message(code: 'updateMilitaryRankRequest.dueDate.label', default: 'dueDate')}"
                          value="${updateMilitaryRankRequest?.dueDate}"/>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'updateMilitaryRankRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${updateMilitaryRankRequest?.requestStatusNote}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<br/>
<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request: updateMilitaryRankRequest, formName:'updateMilitaryRankRequestForm']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>
