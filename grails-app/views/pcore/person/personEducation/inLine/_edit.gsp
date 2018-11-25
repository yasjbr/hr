<el:validatableForm name="personEducationForm" controller="personEducation" action="update">
    <el:hiddenField name="id" value="${personEducation?.id}" />
    <g:render template="/pcore/person/personEducation/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                       personEducation:personEducation]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>