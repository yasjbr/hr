<el:validatableForm name="firmSupportContactInfoForm" controller="firm" action="updateFirmSupportContactInfo">
    <el:hiddenField name="id" value="${recruitmentCycle?.id}" />

    <g:render template="/firmSupportContactInfo/form" model="[firm]"/>

    <el:formButton isSubmit="true" functionName="saveAndClose"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>

<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>

