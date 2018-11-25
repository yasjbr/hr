<script type="text/javascript">
    function callBackPersonEmploymentHistory(json) {
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personEmploymentHistory?.person?.localFullName}", "${personEmploymentHistory?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }


            _dataTables['personEmploymentHistoryTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonEmploymentHistory"
                              title="${message(code: 'default.create.label',args: [message(code:'personEmploymentHistory.entity')])}"
                              width="70%" name="personEmploymentHistoryForm" controller="personEmploymentHistory" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personEmploymentHistory/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       isDocumentOwnerDisabled:isDocumentOwnerDisabled?:params.isDocumentOwnerDisabled,
                                                       personEmploymentHistory:personEmploymentHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
