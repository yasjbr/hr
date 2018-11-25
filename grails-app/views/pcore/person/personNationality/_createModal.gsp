<script type="text/javascript">
    function callBackPersonNationality(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personNationality?.person?.localFullName}", "${personNationality?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }

            _dataTables['personNationalityTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonNationality" title="${message(code: 'default.create.label',args: [message(code:'personNationality.entity')])}"
                              width="70%" name="personNationalityForm" controller="personNationality" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personNationality/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personNationality:personNationality]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
