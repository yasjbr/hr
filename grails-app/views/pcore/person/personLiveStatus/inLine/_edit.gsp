<el:validatableForm name="personLiveStatusForm" controller="personLiveStatus" action="update">
    <el:hiddenField name="id" value="${personLiveStatus?.id}" />
    <g:render template="/pcore/person/personLiveStatus/form" model="[isPersonDisabled:isPersonDisabled ?: params.isPersonDisabled,
                                                        personLiveStatus:personLiveStatus]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>