<el:formGroup>
    <el:textField name="name" size="8" class=" isRequired"
                  label="${message(code: 'applicantInspectionResultList.name.label', default: 'name')}"
                  value="${applicantInspectionResultList?.name}"/>
</el:formGroup>

<el:formGroup>
%{--<el:select valueMessagePrefix="EnumReceivingParty" from="${ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.values()}"
           name="receivingParty" size="8" class=""
           label="${message(code: 'applicantInspectionResultList.receivingParty.label', default: 'receivingParty')}"
           value="${applicantInspectionResultList?.receivingParty}"/>--}%
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="coreOrganizationId"
                     label="${message(code: 'applicantInspectionResultList.coreOrganizationId.label', default: 'coreOrganizationId')}"
                     values="${[[applicantInspectionResultList?.coreOrganizationId, applicantInspectionResultList?.transientData?.organizationName]]}"
                     id="organizationAutoComplete"/>

</el:formGroup>



<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="inspectionCategory"
                     action="autocomplete" name="inspectionCategory.id" id="inspectionCategory" onchange="restInspection();"
                     label="${message(code: 'inspectionCategory.label', default: 'inspectionCategory')}"
                     values="${[[applicantInspectionResultList?.inspectionCategory?.id, applicantInspectionResultList?.inspectionCategory?.descriptionInfo?.localName]]}"/>
</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="inspection"
                     action="autocomplete" name="inspection.id" paramsGenerateFunction="getInspectionCategory"
                     label="${message(code: 'inspection.label', default: 'inspection')}" id="inspectionAutoComplete"
                     values="${[[applicantInspectionResultList?.inspection?.id, applicantInspectionResultList?.inspection?.descriptionInfo?.localName]]}"/>
</el:formGroup>


<el:formGroup>
    <el:textAreaDescription name="coverLetter" size="8" class=" "
                            label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"
                            value="${applicantInspectionResultList?.coverLetter}"/>

    <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-round width-135"
                  link="${createLink(controller: 'correspondenceTemplate', action: 'listModal')}"
                  label="">
        <i class="fa fa-hand-o-up"></i>
        <g:message code="default.button.select.label"/>
    </el:modalLink>
</el:formGroup>

<script>
    function getInspectionCategory() {
        var searchParams = {};
        searchParams['inspectionCategory.id'] = $("#inspectionCategory").val();
        return searchParams;
    }
    function restInspection() {
        gui.autocomplete.clear("inspectionAutoComplete");
    }
</script>