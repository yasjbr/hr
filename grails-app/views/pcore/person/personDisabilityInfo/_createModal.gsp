<script type="text/javascript">
    function callBackPersonDisabilityInfo(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personDisabilityInfo?.person?.localFullName}", "${personDisabilityInfo?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }

            _dataTables['personDisabilityInfoTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonDisabilityInfo" title="${message(code: 'default.create.label',args: [message(code:'personDisabilityInfo.entity')])}"
                              width="70%" name="personDisabilityInfoForm"
                              controller="personDisabilityInfo"
                              action="save">
    <msg:modal />
    <g:render template="/pcore/person/personDisabilityInfo/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personDisabilityInfo:personDisabilityInfo]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
