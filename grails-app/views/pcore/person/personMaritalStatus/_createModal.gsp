<script type="text/javascript">
    function callBackPersonMaritalStatus(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personMaritalStatus?.person?.localFullName}", "${personMaritalStatus?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }


            _dataTables['personMaritalStatusTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonMaritalStatus"
                              title="${message(code: 'default.create.label',args: [message(code:'personMaritalStatus.entity')])}"
                              width="70%"
                              name="personMaritalStatusForm"
                              controller="personMaritalStatus" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personMaritalStatus/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personMaritalStatus:personMaritalStatus]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
