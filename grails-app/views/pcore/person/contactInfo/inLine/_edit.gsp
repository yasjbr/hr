<el:validatableForm name="contactInfoForm" controller="contactInfo" action="update">
    <el:hiddenField name="id" value="${contactInfo?.id}" />
    <g:render template="/pcore/person/contactInfo/form" model="[
                                                   isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                   isRelatedObjectTypeDisabled:isRelatedObjectTypeDisabled?:params.isRelatedObjectTypeDisabled,
                                                   contactInfo:contactInfo]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>