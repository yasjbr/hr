<script type="text/javascript">
    function callBackPersonEducation(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personEducation?.person?.localFullName}", "${personEducation?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }


            _dataTables['personEducationTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonEducation" title="${message(code: 'default.create.label',args: [message(code:'personEducation.entity')])}"
                              width="70%" name="personEducationForm" controller="personEducation" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personEducation/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       isDocumentOwnerDisabled:isDocumentOwnerDisabled?:params.isDocumentOwnerDisabled,
                                                       personEducation:personEducation]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
