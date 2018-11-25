<g:render template="/pcore/person/wrapper" model="[bean:personHealthHistory?.person,isSearch:true]" />
<el:formGroup>
    <el:dateField name="affictionDate"  size="8" class=" " label="${message(code:'personHealthHistory.affictionDate.label',default:'affictionDate')}" value="${personHealthHistory?.affictionDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="description" size="8"  class="" label="${message(code:'personHealthHistory.description.label',default:'description')}" value="${personHealthHistory?.description}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="diseaseName" size="8"  class=" " label="${message(code:'personHealthHistory.diseaseName.label',default:'diseaseName')}" value="${personHealthHistory?.diseaseName}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="diseaseType" action="autocomplete" name="diseaseType.id" label="${message(code:'personHealthHistory.diseaseType.label',default:'diseaseType')}" values="${[[personHealthHistory?.diseaseType?.id,personHealthHistory?.diseaseType?.descriptionInfo?.localName]]}" />
</el:formGroup>

<lay:wall title="${g.message(code: "location.label")}">
    <g:render template="/pcore/location/searchWrapper" />
    <el:formGroup>
        <el:textArea name="unstructuredAffictionLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" />
    </el:formGroup>
</lay:wall>