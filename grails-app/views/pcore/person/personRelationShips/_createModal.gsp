<script type="text/javascript">
    function callBackPersonRelationShips(json){
        if (json.success) {
            if (json.data && json.data.person && json.data.person.id) {
                $("#personId").val(json.data.person.id);
                var newOption = new Option("${personRelationShips?.person?.localFullName}", "${personRelationShips?.person?.id}", true, true);
                $('#personId').append(newOption);
                $('#personId').trigger('change');

            }


            _dataTables['personRelationShipsTable'].draw();
        }
    }
</script>
<el:validatableResetModalForm callBackFunction="callBackPersonRelationShips" title="${message(code: 'default.create.label',args: [message(code:'personRelationShips.entity')])}"
                              width="70%" name="personRelationShipsForm" controller="personRelationShips" action="save">
    <msg:modal />
    <g:render template="/pcore/person/personRelationShips/form" model="[
                                                       organizationCallBackId:'organizationId',
                                                       personCallBackId:'personId',
                                                       isOrganizationDisabled:isOrganizationDisabled?:params.isOrganizationDisabled,
                                                       isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personRelationShips:personRelationShips]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton withClose="true" isSubmit="true" functionName="saveAndClose" />
</el:validatableResetModalForm>
