<script type="text/javascript">
    function callBackPersonLiveStatus(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personLiveStatus?.person?.localFullName}", "${personLiveStatus?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }


            _dataTables['personLiveStatusTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonLiveStatus" title="${message(code: 'default.create.label',args: [message(code:'personLiveStatus.entity')])}"
                              width="70%" name="personLiveStatusForm" controller="personLiveStatus" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personLiveStatus/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personLiveStatus:personLiveStatus]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
