<script type="text/javascript">
    function callBackPersonHealth(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personHealthHistory?.person?.localFullName}", "${personHealthHistory?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }


            _dataTables['personHealthHistoryTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonHealth" title="${message(code: 'default.create.label',args: [message(code:'personHealthHistory.entity')])}"
                              width="70%" name="personHealthHistoryForm" controller="personHealthHistory" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personHealthHistory/form" model="[
                                  organizationCallBackId:'organizationId',
                                  personCallBackId:'personId',
                                  isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                  isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                  personHealthHistory:personHealthHistory]"/>

    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>