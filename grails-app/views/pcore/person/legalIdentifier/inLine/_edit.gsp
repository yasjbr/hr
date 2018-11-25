<el:validatableForm name="legalIdentifierForm" controller="legalIdentifier" action="update">
    <el:hiddenField name="id" value="${legalIdentifier?.id}"/>
    <g:render template="/pcore/person/legalIdentifier/form"
              model="[
                      isPersonDisabled       : isPersonDisabled ?: params.isPersonDisabled,
                      isDocumentOwnerDisabled: isDocumentOwnerDisabled ?: params.isDocumentOwnerDisabled,
                      legalIdentifier        : legalIdentifier]"/>
    <el:formButton isSubmit="true" functionName="save"/>
    <el:formButton functionName="back" accessUrl="${createLink(controller: tabEntityName,action: 'list')}"  onClick="renderInLineList()"/>
</el:validatableForm>
<script>
    gui.initAll.init($('#${tabEntityName}Div'));
</script>