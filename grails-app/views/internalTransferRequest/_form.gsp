<el:hiddenField name="employee.id" value="${internalTransferRequest?.employee?.id}"/>

<g:render template="/employee/wrapperForm" model="[employee: internalTransferRequest?.employee]"/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>

        <el:formGroup>
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" controller="department"
                             action="autocomplete"
                             name="department.id" paramsGenerateFunction="sendDepartmentData"
                             label="${message(code: 'internalTransferRequest.department.label', default: 'department')}"
                             values="${[[internalTransferRequest?.department?.id,
                                         internalTransferRequest?.department?.descriptionInfo?.localName]]}"/>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" "
                             paramsGenerateFunction="sendFirmData" controller="jobTitle" action="autocomplete"
                             name="jobTitle.id"
                             label="${message(code: 'internalTransferRequest.jobTitle.label', default: 'jobTitle')}"
                             values="${[[internalTransferRequest?.jobTitle?.id,
                                         internalTransferRequest?.jobTitle?.descriptionInfo?.localName]]}"/>

        </el:formGroup>

        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'internalTransferRequest.requestDate.label', default: 'requestDate')}"
                          value="${internalTransferRequest?.requestDate ?: java.time.ZonedDateTime.now()}"/>


            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             paramsGenerateFunction="sendFirmData" controller="employmentCategory" action="autocomplete"
                             name="employmentCategory.id"
                             label="${message(code: 'internalTransferRequest.employmentCategory.label', default: 'employmentCategory')}"
                             values="${[[(internalTransferRequest?.employmentCategory?.id) ?: (internalTransferRequest?.currentEmploymentRecord?.employmentCategory?.id),
                                         (internalTransferRequest?.employmentCategory?.descriptionInfo?.localName) ?: (internalTransferRequest?.currentEmploymentRecord?.employmentCategory?.descriptionInfo?.localName)]]}"/>

        </el:formGroup>

        <el:formGroup>
            <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="employee"
                             action="autocomplete"
                             name="alternativeEmployee.id"
                             label="${message(code: 'internalTransferRequest.alternativeEmployee.label', default: 'alternativeEmployee')}"
                             values="${[[internalTransferRequest?.alternativeEmployee?.id,
                                         internalTransferRequest?.alternativeEmployee?.transientData?.personDTO?.localFullName]]}"/>

            <el:textField name="requestReason" size="6" class=""
                          label="${message(code: 'internalTransferRequest.requestReason.label', default: 'requestReason')}"
                          value="${internalTransferRequest?.requestReason}"/>

        </el:formGroup>

        <el:formGroup>
            <el:textArea name="requestStatusNote" size="6"
                         class=""
                         label="${message(code: 'internalTransferRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${internalTransferRequest?.requestStatusNote}"/>

        </el:formGroup>

        <g:if test="${!hideManagerialOrderInfo}">
            <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
                <lay:widgetBody>
                    <g:render template="/request/wrapperManagerialOrder" model="[request:internalTransferRequest, hideExternalOrderInfo:true, formName:'internalTransferRequestForm']"/>
                </lay:widgetBody>
            </lay:widget>
        </g:if>

        <g:if test="${workflowPathHeader}">
            <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
        </g:if>

    </lay:widgetBody>
</lay:widget>


<script type="text/javascript">
    function sendFirmData() {
        return {"firm.id": "${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"};
    }

    function sendDepartmentData() {
        return {"excludeIds[]": "${internalTransferRequest?.currentEmploymentRecord?.department?.id}"};
    }
</script>