<script type="text/javascript">
    function callBackPersonLanguageInfo(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personLanguageInfo?.person?.localFullName}", "${personLanguageInfo?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');

            }


            _dataTables['personLanguageInfoTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonLanguageInfo" title="${message(code: 'default.create.label',args: [message(code:'personLanguageInfo.entity')])}"
                              width="70%" name="personLanguageInfoForm"
                              controller="personLanguageInfo" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personLanguageInfo/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personLanguageInfo:personLanguageInfo]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
