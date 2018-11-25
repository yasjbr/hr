<g:set var="isEdit" value="${employmentRecord?.id != null}" />
<g:set var="currentEmploymentRecord" value="${isEdit?employmentRecord:employmentRecord?.previousEmploymentRecords}" />
<g:set var="internalAssignationValue" value="${currentEmploymentRecord?.employeeInternalAssignations?.size()}" />
<g:set var="internalAssignations" value="${currentEmploymentRecord?.employeeInternalAssignations?currentEmploymentRecord?.employeeInternalAssignations?.max{it.trackingInfo.dateCreatedUTC}:null}" />


<g:if test="${!isEdit}">
    <msg:warning label="${message(code:'employmentRecord.insertInfo.label')}" />
</g:if>

<g:render template="/employee/wrapper" model="[id:(employeeCallBackId?:'employeeId'),
                                               name:'employee.id',
                                               isHiddenInfo:params.isHiddenPersonInfo,
                                               bean:employmentRecord?.employee,
                                               isDisabled:isEmployeeDisabled]" />

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="department" action="autocomplete" name="department.id"
                     label="${message(code:'employmentRecord.department.label',default:'department')}" values="${[[employmentRecord?.department?.id,employmentRecord?.department?.descriptionInfo?.localName]]}" />
</el:formGroup>


<el:formGroup>
    <el:dateField name="fromDate" isMaxDate="true" setMinDateFor="toDate"  size="8" class=" isRequired" label="${message(code:'employmentRecord.fromDate.label',default:'fromDate')}" value="${employmentRecord?.fromDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" " label="${message(code:'employmentRecord.toDate.label',default:'toDate')}" value="${employmentRecord?.toDate}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentCategory" action="autocomplete" name="employmentCategory.id" label="${message(code:'employmentRecord.employmentCategory.label',default:'employmentCategory')}" values="${[[employmentRecord?.employmentCategory?.id,employmentRecord?.employmentCategory?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="jobTitle" action="autocomplete" name="jobTitle.id" label="${message(code:'employmentRecord.jobTitle.label',default:'jobTitle')}" values="${[[employmentRecord?.jobTitle?.id,employmentRecord?.jobTitle?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="province" action="autocomplete" name="province.id" label="${message(code:'province.label',default:'province')}" values="${[[employmentRecord?.province?.id,employmentRecord?.province?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:textArea name="jobDescription" size="8"  class="" label="${message(code:'employmentRecord.jobDescription.label',default:'jobDescription')}" value="${employmentRecord?.jobDescription}"/>
</el:formGroup>

<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'employmentRecord.note.label',default:'note')}" value="${employmentRecord?.note}"/>
</el:formGroup>

<g:if test="${!hideManagerialOrderInfo}">
            <g:render template="/request/wrapperManagerialOrder" model="[hideExternalOrderInfo:true, request:employmentRecord, colSize:8]"/>
</g:if>

<g:if test="${internalAssignationValue > 0}">
    <lay:panel isOpened="true" isCollapsed="false" title="${message(code: 'employmentRecord.assignationInfo.label')}">

        <g:if test="${isEdit}">
            <msg:warning label="${message(code:'employmentRecord.cannotEditAssignation.label')}"/>
        </g:if>

        <g:if test="${!isEdit}">
            <el:formGroup>
                <el:checkboxField id="transferAssignation"  name="transferAssignation" size="8" isChecked="true" value="true"  class="" onchange="setTransferAssignation()"
                                  label="${message(code:'employmentRecord.transferAssignation.label',default:'transfer assignation')}" />
            </el:formGroup>


        </g:if>


        <div id="internalAssignationDiv" style="${internalAssignationValue > 0 ? "" : "display: none;"} ">

            <el:formGroup>


                <el:autocomplete isDisabled="${isEdit}" optionKey="id" optionValue="name" size="8" class=""
                                 paramsGenerateFunction="sendFirmData" controller="department" action="autocomplete" id="assignedToDepartmentId"
                                 name="assignedToDepartment.id"
                                 label="${message(code: 'employmentRecord.assignedToDepartment.label', default: 'assignedToDepartment')}"
                                 values="${[[internalAssignations?.assignedToDepartment?.id,
                                             internalAssignations?.assignedToDepartment?.descriptionInfo?.localName]]}" />

            </el:formGroup>
            <el:formGroup>

                <el:dateField isDisabled="${isEdit}" name="assignedToDepartmentFromDate"  size="8"
                              class=""
                              label="${message(code:'employmentRecord.assignedToDepartmentFromDate.label',default:'assignedToDepartmentFromDate')}"
                              value="${internalAssignations?.assignedToDepartmentFromDate}" />


            </el:formGroup>

            <el:formGroup>

                <el:dateField isDisabled="${isEdit}" name="assignedToDepartmentToDate"  size="8" class=" "
                              label="${message(code:'employmentRecord.assignedToDepartmentToDate.label',default:'assignedToDepartmentToDate')}"
                              value="${internalAssignations?.assignedToDepartmentToDate}" />


            </el:formGroup>

        </div>


    </lay:panel>
</g:if>



<script type="text/javascript">

    function setTransferAssignation() {
        var isChecked = $('#transferAssignation_').is(":checked");

        $('#assignedToDepartmentId').attr("disabled", isChecked);
        $('#assignedToDepartmentFromDate').attr("disabled", isChecked);
        $('#assignedToDepartmentToDate').attr("disabled", isChecked);
        $('#assignedToDepartmentId').trigger('change');

        if(isChecked){

            <g:if test="${internalAssignations}">

            $("#assignedToDepartmentId").val("${internalAssignations?.assignedToDepartment?.id}");
            var newOption = new Option("${internalAssignations?.assignedToDepartment?.descriptionInfo?.localName}","${internalAssignations?.assignedToDepartment?.id}", true, true);
            $('#assignedToDepartmentId').append(newOption);
            $('#assignedToDepartmentId').trigger('change');

            $('#assignedToDepartmentFromDate').val("${internalAssignations?.assignedToDepartmentFromDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}");
            $('#assignedToDepartmentToDate').val("${internalAssignations?.assignedToDepartmentToDate?.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}");

            </g:if>

        }else{

            $('#assignedToDepartmentId').val("");
            $('#assignedToDepartmentFromDate').val("");
            $('#assignedToDepartmentToDate').val("");
            $('#assignedToDepartmentId').trigger('change');

        }

        $('#internalAssignationDiv').show();
    }

    function sendFirmData(){
        return {"firm.id":"${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"};
    }

    <g:if test="${!isEdit}">
    setTransferAssignation();
    </g:if>

</script>
