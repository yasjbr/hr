<script type="text/javascript">
    function callBackPersonCharacteristics(json){
        if(json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personCharacteristics?.person?.localFullName}", "${personCharacteristics?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }

            _dataTables['personCharacteristicsTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonCharacteristics"
                              title="${message(code: 'default.create.label',args: [message(code:'personCharacteristics.entity')])}"
                              width="70%"
                              name="personCharacteristicsForm"
                              controller="personCharacteristics"
                              action="save">
    <msg:modal />
    <g:render template="/pcore/person/personCharacteristics/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personCharacteristics:personCharacteristics]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
