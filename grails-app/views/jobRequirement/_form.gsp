
<g:render template="/DescriptionInfo/wrapper" model="[bean:jobRequirement?.descriptionInfo]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="jobTitle" action="autocomplete" name="jobTitle.id" label="${message(code:'jobRequirement.jobTitle.label',default:'jobTitle')}" values="${[[jobRequirement?.jobTitle?.id,jobRequirement?.jobTitle?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'jobRequirement.universalCode.label',default:'universalCode')}" value="${jobRequirement?.universalCode}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'jobRequirement.note.label',default:'note')}" value="${jobRequirement?.note}"/>
</el:formGroup>
