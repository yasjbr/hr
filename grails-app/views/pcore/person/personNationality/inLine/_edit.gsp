<el:validatableForm name="personNationalityForm" controller="personNationality" action="update">
    <el:hiddenField name="id" value="${personNationality?.id}" />
    <g:render template="/pcore/person/personNationality/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                      personNationality:personNationality]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>