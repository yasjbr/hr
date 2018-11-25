<el:validatableForm name="personLanguageInfoForm" controller="personLanguageInfo" action="update">
    <el:hiddenField name="id" value="${personLanguageInfo?.id}" />
    <g:render template="/pcore/person/personLanguageInfo/form" model="[isPersonDisabled:isPersonDisabled?:params.isPersonDisabled,
                                                      vehicleLicense:vehicleLicense]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>