<el:validatableForm name="firmSupportContactInfoForm" controller="firmSupportContactInfo" action="update">
    <el:hiddenField name="id" value="${firmSupportContactInfo?.id}" />
    <el:hiddenField name="firm.id" value="${firmSupportContactInfo?.firm?.id}"/>
    <g:render template="/firmSupportContactInfo/form" model="[firmSupportContactInfo:firmSupportContactInfo]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>
