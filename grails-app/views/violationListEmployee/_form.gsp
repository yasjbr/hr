
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="absence" action="autocomplete" name="absence.id" label="${message(code:'violationListEmployee.absence.label',default:'absence')}" values="${[[violationListEmployee?.absence?.id,violationListEmployee?.absence?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="violationList" action="autocomplete" name="violationList.id" label="${message(code:'violationListEmployee.violationList.label',default:'violationList')}" values="${[[violationListEmployee?.violationList?.id,violationListEmployee?.violationList?.descriptionInfo?.localName]]}" />
</el:formGroup>