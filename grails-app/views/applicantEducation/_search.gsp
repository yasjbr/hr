<g:render template="/person/wrapper" model="[bean:personEducation?.person,isSearch:true]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationDegree" action="autocomplete" name="educationDegree.id" label="${message(code:'personEducation.educationDegree.label',default:'educationDegree')}" values="${[[personEducation?.educationDegree?.id,personEducation?.educationDegree?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationLevel" action="autocomplete" name="educationLevel.id" label="${message(code:'personEducation.educationLevel.label',default:'educationLevel')}" values="${[[personEducation?.educationLevel?.id,personEducation?.educationLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationMajor" action="autocomplete" name="educationMajor.id" label="${message(code:'personEducation.educationMajor.label',default:'educationMajor')}" values="${[[personEducation?.educationMajor?.id,personEducation?.educationMajor?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="instituteName" size="8"  class=" " label="${message(code:'personEducation.instituteName.label',default:'instituteName')}" value="${personEducation?.instituteName}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organization" action="autocomplete" name="organization.id" label="${message(code:'personEducation.organization.label',default:'organization')}" values="${[[personEducation?.organization?.id,personEducation?.organization?.descriptionInfo?.localName]]}" />
</el:formGroup>

<lay:wall title="${g.message(code: "location.label")}">
    <g:render template="/location/searchWrapper" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}"/>
    </el:formGroup>
</lay:wall>
