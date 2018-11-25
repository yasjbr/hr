<el:hiddenField name="employee.id" value="${promotionRequest?.employee?.id}"/>
<el:hiddenField name="requestType" value="${promotionRequest?.requestType}"/>
<g:render template="/employee/wrapperForm" model="[employee: promotionRequest?.employee , isPromotion:true]"/>
<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <g:set var="requestTypeValue" value="EnumRequestType.${promotionRequest?.requestType}" />
            <el:textField name="requestType" value="${message(code: requestTypeValue)}"
                          isReadOnly="true"
                          label="${message(code: 'promotionRequest.requestType.label')}"
                          size="6"/>
            <el:dateField isMaxDate="true" name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'promotionRequest.requestDate.label', default: 'requestDate')}"
                          value="${promotionRequest?.requestDate}"/>

        </el:formGroup>
        <el:formGroup>
            <g:if test="${promotionRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.SITUATION_SETTLEMENT}">
                <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" controller="educationDegree"
                                 action="autocomplete"
                                 name="educationDegreeId"
                                 label="${message(code: 'promotionRequest.educationDegreeId.label', default: 'educationDegreeId')}"
                                 values="${[[promotionRequest?.educationDegreeId, promotionRequest?.transientData?.educationDegreeDTO?.descriptionInfo?.localName]]}"/>
            </g:if>

            <g:if test="${promotionRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.EXCEPTIONAL_REQUEST}">
                <g:set var="isRequired" value=" isRequired" />
            </g:if>
            <g:else>
                <g:set var="isRequired" value="" />
            </g:else>

            <el:textArea name="requestStatusNote"
                         size="6"
                         class="${isRequired}"
                         label="${message(code: 'promotionRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${promotionRequest?.requestStatusNote}"/>

        </el:formGroup>

    </lay:widgetBody>
</lay:widget>
<br/>

<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request: promotionRequest, formName:'promotionRequestForm',parentFolder:'promotionList']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>
