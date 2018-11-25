<el:validatableForm name="personDisabilityInfoForm" controller="personDisabilityInfo" action="update">
    <el:hiddenField name="id" value="${personDisabilityInfo?.id}" />
    <g:render template="/pcore/person/personDisabilityInfo/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                            personDisabilityInfo:personDisabilityInfo]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>