<script type="text/javascript">
    function callBackPersonArrestHistory(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personArrestHistory?.person?.localFullName}", "${personArrestHistory?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');

            }
            _dataTables['personArrestHistoryTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonArrestHistory" title="${message(code: 'default.create.label',args: [message(code:'personArrestHistory.entity')])}"
                              width="70%" name="personArrestHistoryForm" controller="personArrestHistory" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personArrestHistory/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personArrestHistory:personArrestHistory]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
