<el:hiddenField name="employee.id" value="${allowanceRequest?.employee?.id}"/>
<el:hiddenField name="allowanceType.id" value="${allowanceRequest?.allowanceType?.id}"/>
<el:hiddenField name="oldRequestId" value="${allowanceRequest?.id}"/>

<g:if test="${!hideEmployeeInfo}">
    <g:render template="/employee/wrapperForm"
              model="[employee: allowanceRequest?.employee, isAllowance: true, allowanceRequest: allowanceRequest]"/>
</g:if>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>

        <g:if test="${allowanceRequest?.allowanceType?.relationshipTypeId}">
            <msg:info label="${message(code: 'allowanceRequest.relationship.type.message')}"/>
            <el:row/>
        </g:if>

        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'allowanceRequest.requestDate.label', default: 'requestDate')}"
                          value="${allowanceRequest?.requestDate ? allowanceRequest?.requestDate : java.time.ZonedDateTime.now()}"/>


            <g:if test="${allowanceRequest?.allowanceType?.relationshipTypeId}">
                <el:autocomplete
                        optionKey="id"
                        optionValue="name"
                        size="6"
                        class=" isRequired"
                        paramsGenerateFunction="paramsForPersonAutocomplete"
                        controller="personRelationShips"
                        action="autocomplete"
                        name="personRelationShipsId"
                        label="${message(code: 'allowanceRequest.personRelationShipsId.label', default: 'personRelationShipsId')}"
                        values="${[[allowanceRequest?.personRelationShipsId, allowanceRequest?.transientData?.personRelationShipsName]]}"/>

            </g:if>
        </el:formGroup>

        <g:if test="${!hideInterval}">
            <el:formGroup>
                <el:dateField name="effectiveDate" size="6" class=" isRequired"
                              label="${message(code: 'allowanceRequest.effectiveDate.label', default: 'effectiveDate')}"
                              value="${allowanceRequest?.effectiveDate}"/>

                <el:dateField name="toDate" size="6" class=" "
                              label="${message(code: 'allowanceRequest.toDate.label', default: 'toDate')}"
                              value="${allowanceRequest?.toDate}"/>
            </el:formGroup>
        </g:if>

        <el:formGroup>

            <el:textField name="requestReason" size="6" class=""
                          label="${message(code: 'request.requestReason.label', default: 'requestReason')}"
                          value="${allowanceRequest?.requestReason}"/>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${allowanceRequest?.requestStatusNote}"/>
        </el:formGroup>

        <g:if test="${!hideManagerialOrderInfo}">
            <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
                <lay:widgetBody>
                    <g:render template="/request/wrapperManagerialOrder" model="[request: allowanceRequest, formName: 'allowanceRequestForm']"/>
                </lay:widgetBody>
            </lay:widget>
        </g:if>

        <g:if test="${workflowPathHeader}">
            <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
        </g:if>

    </lay:widgetBody>
</lay:widget>

<script>
    function paramsForPersonAutocomplete() {
        var searchParams = {};
        searchParams["relationshipType.id"] = "${allowanceRequest?.allowanceType?.relationshipTypeId}";
        searchParams["person.id"] = "${allowanceRequest?.employee?.personId}";
        searchParams["nameProperty"] = "relatedPerson.localFullName";
        return searchParams;
    }
</script>
