<el:formGroup>
    <el:textField name="name" size="8" class=" isRequired"
                  label="${message(code: 'generalList.name.label', default: 'name')}"
                  value="${generalList?.name}"/>
</el:formGroup>

<el:formGroup>
%{--<el:select valueMessagePrefix="EnumReceivingParty" from="${ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.values()}"
           name="receivingParty" size="8" class=""
           label="${message(code: 'generalList.receivingParty.label', default: 'receivingParty')}"
           value="${generalList?.receivingParty}"/>--}%
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="coreOrganizationId"
                     label="${message(code: 'generalList.coreOrganizationId.label', default: 'coreOrganizationId')}"
                     values="${[[generalList?.coreOrganizationId, generalList?.transientData?.organizationName]]}"
                     id="organizationAutoComplete"/>

</el:formGroup>

<el:formGroup>
    <el:textAreaDescription name="coverLetter" size="8" class=" "
                            label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"
                            value="${generalList?.coverLetter}"/>

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
</script>