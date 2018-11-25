<script type="text/javascript">
    function callBackPersonCountryVisit(json){
        if(json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personCountryVisit?.person?.localFullName}", "${personCountryVisit?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');
            }
            _dataTables['personCountryVisitTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonCountryVisit" title="${message(code: 'default.create.label',args: [message(code:'personCountryVisit.entity')])}"
                              width="70%" name="personCountryVisitForm" controller="personCountryVisit" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personCountryVisit/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personCountryVisit:personCountryVisit]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
